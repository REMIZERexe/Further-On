package com.remizerexe.further_on.content.blast_furnace;

import com.remizerexe.further_on.FurtherOn;
import com.remizerexe.further_on.multiblock.JsonMultiblockDefinition;
import com.remizerexe.further_on.multiblock.MultiblockControllerBE;
import com.remizerexe.further_on.multiblock.MultiblockJsonLoader;
import com.remizerexe.further_on.multiblock.MultiblockStructure;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class BlastFurnaceHearthBlockEntity extends MultiblockControllerBE
        implements IHaveGoggleInformation {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Structure definition loaded from assets/further_on/multiblocks/blast_furnace_hearth.json */
    private static final JsonMultiblockDefinition DEFINITION =
            MultiblockJsonLoader.load("further_on", "blast_furnace_hearth");

    /** Base processing time at 1 RPM — 600 ticks = 30 seconds. */
    private static final int BASE_TICKS = 600;

    /** Items required per layer. */
    private static final int COAL_PER_LAYER  = 2;
    private static final int IRON_PER_LAYER  = 1;
    private static final int STEEL_PER_LAYER = 1;
    private static final int SLAG_PER_LAYER  = 1;

    // -------------------------------------------------------------------------
    // Inventories
    // -------------------------------------------------------------------------

    /** Slot 0 = coal, slot 1 = iron ingot. */
    public final ItemStackHandler inputInventory = new ItemStackHandler(2) {
        @Override public int getSlotLimit(int slot) { return 64 * maxCapacityLayers(); }
    };

    /** Stores processed steel ingots, extracted from the hearth face. */
    public final ItemStackHandler outputInventory = new ItemStackHandler(1) {
        @Override public int getSlotLimit(int slot) { return 64 * maxCapacityLayers(); }
    };

    /** Stores slag, extracted from below the base layer center. */
    public final ItemStackHandler slagInventory = new ItemStackHandler(1) {
        @Override public int getSlotLimit(int slot) { return 64 * maxCapacityLayers(); }
    };

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    /** Number of complete input layers currently loaded. */
    private int accumulatedLayers = 0;

    /** Processing progress from 0.0 to 1.0. Resets on each completed batch. */
    private float processingProgress = 0f;

    /** Current fan RPM, read each tick from the encased fan block entity. */
    private int currentRPM = 0;

    /**
     * Partial batch buffer — items dropped into the structure accumulate here
     * before being converted into full layers.
     */
    int bufferedCoal = 0;
    int bufferedIron = 0;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public BlastFurnaceHearthBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // -------------------------------------------------------------------------
    // Tick
    // -------------------------------------------------------------------------

    /**
     * Called every server tick by the block's ticker.
     * Order: scan for dropped items → read fan RPM → advance processing.
     */
    public void tick() {
        if (level == null || level.isClientSide()) return;
        if (!isFormed()) return;

        int layersBefore = accumulatedLayers;
        float progressBefore = processingProgress;

        scanForDroppedItems();

        currentRPM = readFanRPM();
        if (currentRPM > 0 && accumulatedLayers > 0 && hasEnoughInputs()) {
            float tickProgress = 1.0f / (BASE_TICKS * 2) * ((float) currentRPM / 64f);
            processingProgress += tickProgress;

            if (processingProgress >= 1.0f) {
                processingProgress = 0f;
                processOneBatch();
            }
        }

        // Sync to client every 10 ticks or when layers change
        if (accumulatedLayers != layersBefore
                || level.getGameTime() % 10 == 0) {
            syncToClient();
        }

        // Smoke particles — client side, center top of chimney
        if (level.isClientSide()) {
            if (currentRPM > 0 && isFormed()) {
                Direction facing = getFacing();
                BlockPos smokePos = worldPosition.relative(facing, 1).above(2 + capacityLayers);
                level.addParticle(
                        net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        smokePos.getX() + 0.5,
                        smokePos.getY() + 0.5,
                        smokePos.getZ() + 0.5,
                        0, 0.02, 0
                );
            }
        }

        setChanged();
    }

    // -------------------------------------------------------------------------
    // Processing
    // -------------------------------------------------------------------------

    /** Returns true if the input inventory has enough items for one batch. */
    private boolean hasEnoughInputs() {
        ItemStack coal = inputInventory.getStackInSlot(0);
        ItemStack iron = inputInventory.getStackInSlot(1);
        return coal.is(Items.COAL)      && coal.getCount() >= COAL_PER_LAYER
                && iron.is(Items.IRON_INGOT) && iron.getCount() >= IRON_PER_LAYER;
    }

    /**
     * Consumes one layer of inputs and produces steel + slag.
     * TODO: replace Items.IRON_INGOT with actual steel item when registered.
     * TODO: replace Items.GRAVEL with actual slag item when registered.
     */
    private void processOneBatch() {
        inputInventory.getStackInSlot(0).shrink(COAL_PER_LAYER);
        inputInventory.getStackInSlot(1).shrink(IRON_PER_LAYER);

        ItemStack steel = outputInventory.getStackInSlot(0);
        if (steel.isEmpty()) {
            outputInventory.setStackInSlot(0, new ItemStack(Items.IRON_INGOT, STEEL_PER_LAYER));
        } else {
            steel.grow(STEEL_PER_LAYER);
        }

        ItemStack slag = slagInventory.getStackInSlot(0);
        if (slag.isEmpty()) {
            slagInventory.setStackInSlot(0, new ItemStack(Items.GRAVEL, SLAG_PER_LAYER));
        } else {
            slag.grow(SLAG_PER_LAYER);
        }

        accumulatedLayers = Math.max(0, accumulatedLayers - 1);
        setChanged();
    }

    // -------------------------------------------------------------------------
    // Fan RPM
    // -------------------------------------------------------------------------

    /**
     * Reads the speed of the encased fan positioned 2 blocks in front of the controller.
     * Returns 0 if the fan is missing, stopped, or not a kinetic block entity.
     */
    private int readFanRPM() {
        Direction facing = getFacing();
        BlockPos fanPos = worldPosition.relative(facing, 2);
        BlockEntity be = level.getBlockEntity(fanPos);
        if (be instanceof KineticBlockEntity kinetic) {
            return (int) Math.abs(kinetic.getSpeed());
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // Item drop detection
    // -------------------------------------------------------------------------

    /**
     * Scans the center bottom position of the structure (1 block in front of
     * the controller, same Y) for dropped item entities each tick.
     *
     * Coal and iron are absorbed into the partial batch buffer.
     * Any other item is ejected downward out the bottom of the structure.
     * Full batches are flushed from the buffer into the input inventory as layers.
     */
    public void scanForDroppedItems() {
        if (level == null || level.isClientSide()) return;
        if (!isFormed()) return;

        Direction facing = getFacing();
        BlockPos centerPos = worldPosition.relative(facing, 1);
        AABB scanBox = new AABB(centerPos);

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, scanBox);
        if (items.isEmpty()) return;

        for (ItemEntity itemEntity : items) {
            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) continue;

            if (stack.is(Items.COAL)) {
                bufferedCoal += stack.getCount();
                itemEntity.discard();
            } else if (stack.is(Items.IRON_INGOT)) {
                bufferedIron += stack.getCount();
                itemEntity.discard();
            } else {
                // Wrong item — eject it downward out the bottom
                ejectItem(stack);
                itemEntity.discard();
            }
        }

        // Convert complete batches from the buffer into full layers
        flushBuffer();
        setChanged();
    }

    /**
     * Converts as many complete recipe batches as possible from the buffer
     * into accumulated layers, up to the maximum capacity.
     */
    // Each physical capacity layer holds 8 input batches
    private static final int BATCHES_PER_LAYER = 8;
    private void flushBuffer() {
        while (bufferedCoal >= COAL_PER_LAYER
                && bufferedIron >= IRON_PER_LAYER
                && accumulatedLayers < capacityLayers * BATCHES_PER_LAYER) {

            bufferedCoal -= COAL_PER_LAYER;
            bufferedIron -= IRON_PER_LAYER;

            ItemStack coalSlot = inputInventory.getStackInSlot(0);
            ItemStack ironSlot = inputInventory.getStackInSlot(1);

            if (coalSlot.isEmpty()) {
                inputInventory.setStackInSlot(0, new ItemStack(Items.COAL, COAL_PER_LAYER));
            } else {
                coalSlot.grow(COAL_PER_LAYER);
            }

            if (ironSlot.isEmpty()) {
                inputInventory.setStackInSlot(1, new ItemStack(Items.IRON_INGOT, IRON_PER_LAYER));
            } else {
                ironSlot.grow(IRON_PER_LAYER);
            }

            accumulatedLayers++;
        }

    }

    /**
     * Spawns an item entity below the base layer center, moving downward.
     * Used to eject invalid items dropped into the structure.
     */
    private void ejectItem(ItemStack stack) {
        if (level == null) return;
        Direction facing = getFacing();
        BlockPos ejectPos = worldPosition.below(1).relative(facing, 1);
        ItemEntity ejected = new ItemEntity(
                level,
                ejectPos.getX() + 0.5,
                ejectPos.getY() + 0.5,
                ejectPos.getZ() + 0.5,
                stack.copy()
        );
        ejected.setDeltaMovement(0, -0.1, 0);
        level.addFreshEntity(ejected);
    }

    // -------------------------------------------------------------------------
    // Capability — expose inventories to Create funnels and belts
    // -------------------------------------------------------------------------

    /**
     * Returns the inventory exposed on a given face:
     * - Facing direction (front) → steel output
     * - Down → slag output
     * - All other faces → input inventory
     */
    public ItemStackHandler getInventoryForFace(Direction face) {
        Direction facing = getFacing();
        if (face == Direction.DOWN)             return slagInventory;
        if (face == facing.getOpposite())       return outputInventory;
        return null;
    }

    // -------------------------------------------------------------------------
    // Multiblock structure
    // -------------------------------------------------------------------------

    @Override
    protected MultiblockStructure buildStructure(int capacityLayers) {
        return DEFINITION.buildStructure(capacityLayers);
    }

    /**
     * A capacity layer is detected by checking that the block directly above
     * the controller (offset by facing) has air at its center and bricks on
     * all four cardinal sides relative to the facing direction.
     */
    @Override
    protected boolean isCapacityLayer(BlockPos centerPos) {
        if (level == null) return false;
        Direction facing = getFacing();
        BlockPos actualCenter = centerPos.relative(facing, 1);

        return level.getBlockState(actualCenter).is(Blocks.AIR)
                && level.getBlockState(actualCenter.relative(facing.getClockWise())).is(Blocks.BRICKS)
                && level.getBlockState(actualCenter.relative(facing.getCounterClockWise())).is(Blocks.BRICKS)
                && level.getBlockState(actualCenter.relative(facing)).is(Blocks.BRICKS)
                && level.getBlockState(actualCenter.relative(facing.getOpposite())).is(Blocks.BRICKS);
    }

    @Override protected int minCapacityLayers() { return DEFINITION.getMinCapacityLayers(); }
    @Override protected int maxCapacityLayers() { return DEFINITION.getMaxCapacityLayers(); }

    @Override
    protected void onFormed(int capacityLayers) {
        processingProgress = 0f;
        if (level == null) return;
        Direction facing = getFacing();
        BlockPos hatchPos = worldPosition.relative(facing, 1).below(1);
        FurtherOn.LOGGER.warn("Looking for hatch at {}", hatchPos);
        BlockEntity hatchBE = level.getBlockEntity(hatchPos);
        FurtherOn.LOGGER.warn("Found: {}", hatchBE);
        if (hatchBE instanceof BlastFurnaceHatchBlockEntity hatch) {
            hatch.setControllerPos(worldPosition);
            FurtherOn.LOGGER.warn("Controller pos set to {}", worldPosition);
        }
    }

    @Override
    protected void onUnformed() {
        processingProgress = 0f;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public int   getAccumulatedLayers()   { return accumulatedLayers; }
    public int   getCurrentRPM()          { return currentRPM; }
    public float getProcessingProgress()  { return processingProgress; }

    // -------------------------------------------------------------------------
    // NBT
    // -------------------------------------------------------------------------

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("InputInventory",      inputInventory.serializeNBT(registries));
        tag.put("OutputInventory",     outputInventory.serializeNBT(registries));
        tag.put("SlagInventory",       slagInventory.serializeNBT(registries));
        tag.putInt("AccumulatedLayers",    accumulatedLayers);
        tag.putFloat("ProcessingProgress", processingProgress);
        tag.putInt("BufferedCoal",         bufferedCoal);
        tag.putInt("BufferedIron",         bufferedIron);
        tag.putInt("CurrentRPM", currentRPM);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("InputInventory"))
            inputInventory.deserializeNBT(registries, tag.getCompound("InputInventory"));
        if (tag.contains("OutputInventory"))
            outputInventory.deserializeNBT(registries, tag.getCompound("OutputInventory"));
        if (tag.contains("SlagInventory"))
            slagInventory.deserializeNBT(registries, tag.getCompound("SlagInventory"));
        accumulatedLayers  = tag.getInt("AccumulatedLayers");
        processingProgress = tag.getFloat("ProcessingProgress");
        bufferedCoal       = tag.getInt("BufferedCoal");
        bufferedIron       = tag.getInt("BufferedIron");
        currentRPM = tag.getInt("CurrentRPM");
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
    }

    public void dropContents() {
        if (level == null) return;
        // Drop all inventories as items
        for (int i = 0; i < inputInventory.getSlots(); i++) {
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), inputInventory.getStackInSlot(i));
        }
        for (int i = 0; i < outputInventory.getSlots(); i++) {
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), outputInventory.getStackInSlot(i));
        }
        for (int i = 0; i < slagInventory.getSlots(); i++) {
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), slagInventory.getStackInSlot(i));
        }
        accumulatedLayers = 0;
        bufferedCoal = 0;
        bufferedIron = 0;
        processingProgress = 0f;
    }

    /**
     * Client-side tick — handles visual effects only.
     * Smoke emits from the center top of the chimney when the fan is running.
     */
    public void clientTick() {
        if (!isFormed()) return;
        if (currentRPM <= 0) return;
        if (level == null || !level.isClientSide()) return;

        // Only spawn smoke occasionally, not every tick
        if (level.random.nextInt(4) != 0) return;

        Direction facing = getFacing();
        // Top of chimney = controller Y + 2 (collar) + capacity layers + 1 (top layer)
        BlockPos smokePos = worldPosition.relative(facing, 1)
                .above(2 + capacityLayers);

        level.addParticle(
                net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE,
                smokePos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3,
                smokePos.getY(),
                smokePos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3,
                0,
                0.05 + level.random.nextDouble() * 0.02,
                0
        );
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!isFormed()) {
            tooltip.add(Component.literal("Blast Furnace — Unformed")
                    .withStyle(ChatFormatting.RED));
            return true;
        }

        // Title
        tooltip.add(Component.literal("Blast Furnace Hearth")
                .withStyle(ChatFormatting.WHITE));

        // Layers
        tooltip.add(Component.literal(" Layers: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(accumulatedLayers + " / " + (capacityLayers * 8))
                        .withStyle(ChatFormatting.AQUA)));

        // RPM
        tooltip.add(Component.literal(" Fan RPM: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(currentRPM))
                        .withStyle(currentRPM > 0 ? ChatFormatting.GREEN : ChatFormatting.RED)));

        // Progress
        int progressPercent = (int) (processingProgress * 100);
        tooltip.add(Component.literal(" Progress: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(progressPercent + "%")
                        .withStyle(ChatFormatting.YELLOW)));

        return true;
    }
}