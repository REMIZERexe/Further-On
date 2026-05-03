package com.remizerexe.further_on.content.blast_furnace;

import com.remizerexe.further_on.FurtherOn;
import com.remizerexe.further_on.multiblock.JsonMultiblockDefinition;
import com.remizerexe.further_on.multiblock.MultiblockControllerBE;
import com.remizerexe.further_on.multiblock.MultiblockJsonLoader;
import com.remizerexe.further_on.multiblock.MultiblockStructure;
import com.remizerexe.further_on.registry.FOBlocks;
import com.remizerexe.further_on.registry.FOFluids;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class BlastFurnaceHearthBlockEntity extends MultiblockControllerBE
        implements IHaveGoggleInformation {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final JsonMultiblockDefinition DEFINITION =
            MultiblockJsonLoader.load("further_on", "blast_furnace_hearth");

    private static final int BASE_TICKS = 600;

    private static final int COAL_PER_LAYER  = 2;
    private static final int IRON_PER_LAYER  = 1;
    private static final int STEEL_MB_PER_BATCH = 144;
    private static final int SLAG_PER_LAYER  = 1;

    // -------------------------------------------------------------------------
    // Inventories & tanks
    // -------------------------------------------------------------------------

    public final ItemStackHandler inputInventory = new ItemStackHandler(2) {
        @Override public int getSlotLimit(int slot) { return 64 * maxCapacityLayers(); }
    };

    public final ItemStackHandler slagInventory = new ItemStackHandler(1) {
        @Override public int getSlotLimit(int slot) { return 64 * maxCapacityLayers(); }
    };

    public final FluidTank steelTank = new FluidTank(16000) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private int accumulatedLayers = 0;
    private float processingProgress = 0f;
    private int currentRPM = 0;

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

    public void tick() {
        if (level == null || level.isClientSide()) return;
        if (!isFormed()) return;

        int layersBefore = accumulatedLayers;

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

        if (accumulatedLayers != layersBefore || level.getGameTime() % 10 == 0) {
            syncToClient();
        }

        setChanged();
    }

    // -------------------------------------------------------------------------
    // Processing
    // -------------------------------------------------------------------------

    private boolean hasEnoughInputs() {
        ItemStack coal = inputInventory.getStackInSlot(0);
        ItemStack iron = inputInventory.getStackInSlot(1);
        return coal.is(Items.COAL)       && coal.getCount() >= COAL_PER_LAYER
                && iron.is(Items.IRON_INGOT) && iron.getCount() >= IRON_PER_LAYER;
    }

    private void processOneBatch() {
        inputInventory.getStackInSlot(0).shrink(COAL_PER_LAYER);
        inputInventory.getStackInSlot(1).shrink(IRON_PER_LAYER);

        // Produce molten steel fluid
        FluidStack steelFluid = new FluidStack(
                FOFluids.MOLTEN_STEEL_STILL.get(), STEEL_MB_PER_BATCH);
        steelTank.fill(steelFluid, IFluidHandler.FluidAction.EXECUTE);

        // Produce slag
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

    private int readFanRPM() {
        Direction facing = getFacing();
        BlockPos fanPos = worldPosition.relative(facing.getOpposite(), 2);
        BlockEntity be = level.getBlockEntity(fanPos);
        if (be instanceof KineticBlockEntity kinetic) {
            return (int) Math.abs(kinetic.getSpeed());
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // Item drop detection
    // -------------------------------------------------------------------------

    public void scanForDroppedItems() {
        if (level == null || level.isClientSide()) return;
        if (!isFormed()) return;

        Direction facing = getFacing();
        BlockPos centerPos = worldPosition.relative(facing.getOpposite(), 1);
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
                ejectItem(stack);
                itemEntity.discard();
            }
        }

        flushBuffer();
        setChanged();
    }

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

    private void ejectItem(ItemStack stack) {
        if (level == null) return;
        Direction facing = getFacing();
        BlockPos ejectPos = worldPosition.below(1).relative(facing.getOpposite(), 1);
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
    // Capabilities
    // -------------------------------------------------------------------------

    public ItemStackHandler getInventoryForFace(Direction face) {
        if (face == Direction.DOWN) return slagInventory;
        return null;
    }

    public FluidTank getFluidTankForFace(Direction face) {
        Direction facing = getFacing();
        if (face == facing) return steelTank;
        return null;
    }

    // -------------------------------------------------------------------------
    // Multiblock
    // -------------------------------------------------------------------------

    @Override
    protected MultiblockStructure buildStructure(int capacityLayers) {
        return DEFINITION.buildStructure(capacityLayers);
    }

    @Override
    protected boolean isCapacityLayer(BlockPos centerPos) {
        if (level == null) return false;
        Direction facing = getFacing();
        BlockPos chamberCenter = centerPos.relative(facing.getOpposite());

        return level.getBlockState(chamberCenter).isAir()
                && level.getBlockState(chamberCenter.north()).is(FOBlocks.FIRE_CLAY_BRICKS)
                && level.getBlockState(chamberCenter.south()).is(FOBlocks.FIRE_CLAY_BRICKS)
                && level.getBlockState(chamberCenter.east()).is(FOBlocks.FIRE_CLAY_BRICKS)
                && level.getBlockState(chamberCenter.west()).is(FOBlocks.FIRE_CLAY_BRICKS);
    }

    @Override protected int minCapacityLayers() { return DEFINITION.getMinCapacityLayers(); }
    @Override protected int maxCapacityLayers() { return DEFINITION.getMaxCapacityLayers(); }

    @Override
    protected void onFormed(int capacityLayers) {
        processingProgress = 0f;
        if (level == null) return;
        Direction facing = getFacing();
        BlockPos hatchPos = worldPosition.relative(facing.getOpposite(), 1).below(1);
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

    public int   getAccumulatedLayers()  { return accumulatedLayers; }
    public int   getCurrentRPM()         { return currentRPM; }
    public float getProcessingProgress() { return processingProgress; }

    // -------------------------------------------------------------------------
    // NBT
    // -------------------------------------------------------------------------

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("InputInventory",      inputInventory.serializeNBT(registries));
        tag.put("SlagInventory",       slagInventory.serializeNBT(registries));
        tag.put("SteelTank",           steelTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("AccumulatedLayers",    accumulatedLayers);
        tag.putFloat("ProcessingProgress", processingProgress);
        tag.putInt("BufferedCoal",         bufferedCoal);
        tag.putInt("BufferedIron",         bufferedIron);
        tag.putInt("CurrentRPM",           currentRPM);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("InputInventory"))
            inputInventory.deserializeNBT(registries, tag.getCompound("InputInventory"));
        if (tag.contains("SlagInventory"))
            slagInventory.deserializeNBT(registries, tag.getCompound("SlagInventory"));
        if (tag.contains("SteelTank"))
            steelTank.readFromNBT(registries, tag.getCompound("SteelTank"));
        accumulatedLayers  = tag.getInt("AccumulatedLayers");
        processingProgress = tag.getFloat("ProcessingProgress");
        bufferedCoal       = tag.getInt("BufferedCoal");
        bufferedIron       = tag.getInt("BufferedIron");
        currentRPM         = tag.getInt("CurrentRPM");
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
    }

    public void dropContents() {
        if (level == null) return;
        for (int i = 0; i < inputInventory.getSlots(); i++) {
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(),
                    worldPosition.getY(), worldPosition.getZ(), inputInventory.getStackInSlot(i));
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

    public void clientTick() {
        if (!isFormed()) return;
        if (currentRPM <= 0) return;
        if (level == null || !level.isClientSide()) return;

        if (level.random.nextInt(4) != 0) return;

        Direction facing = getFacing();
        BlockPos smokePos = worldPosition.relative(facing.getOpposite(), 1)
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

        tooltip.add(Component.literal("Blast Furnace Hearth")
                .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(" Layers: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(accumulatedLayers + " / " + (capacityLayers * 8))
                        .withStyle(ChatFormatting.AQUA)));

        tooltip.add(Component.literal(" Fan RPM: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(currentRPM))
                        .withStyle(currentRPM > 0 ? ChatFormatting.GREEN : ChatFormatting.RED)));

        int progressPercent = (int) (processingProgress * 100);
        tooltip.add(Component.literal(" Progress: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(progressPercent + "%")
                        .withStyle(ChatFormatting.YELLOW)));

        tooltip.add(Component.literal(" Steel: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(steelTank.getFluidAmount() + " / " + steelTank.getCapacity() + " mb")
                        .withStyle(ChatFormatting.GOLD)));

        return true;
    }
}