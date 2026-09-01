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

    private static final JsonMultiblockDefinition DEFINITION =
            MultiblockJsonLoader.load("further_on", "blast_furnace_hearth");

    private static final int BASE_TICKS = 600;
    private static final int COAL_PER_LAYER  = 2;
    private static final int IRON_PER_LAYER  = 1;
    private static final int STEEL_MB_PER_BATCH = 144;
    private static final int SLAG_PER_LAYER  = 1;

    public final ItemStackHandler inputInventory = new ItemStackHandler(3) {
        @Override public int getSlotLimit(int slot) { return 64 * (maxCapacityLayers()+2); }
    };

    public final ItemStackHandler slagInventory = new ItemStackHandler(1) {
        @Override public int getSlotLimit(int slot) { return 64 * maxCapacityLayers(); }
    };

    public final FluidTank steelTank = new FluidTank(16000) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    private int accumulatedLayers = 0;
    private float processingProgress = 0f;
    private int currentRPM = 0;
    int bufferedCoal = 0;
    int bufferedIron = 0;

    public BlastFurnaceHearthBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;
        if (!isFormed()) return;

        // BUG FIX: Ensure the physical multiblock hasn't been broken by a player!
        // Without this, players could form the furnace, break the bricks, and let it run in mid-air forever.
        if (level.getGameTime() % 20 == 0) {
            revalidate();
            if (!isFormed()) return;
        }

        int layersBefore = accumulatedLayers;

        scanForDroppedItems();

        if (currentRPM > 0 && accumulatedLayers > 0 && hasEnoughInputs()) {
            float ticksDuration = 69419f / currentRPM + 929f;
            processingProgress += 1.0f / ticksDuration;

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

    private boolean hasEnoughInputs() {
        ItemStack coke = inputInventory.getStackInSlot(0);
        ItemStack iron = inputInventory.getStackInSlot(1);
        ItemStack calcite = inputInventory.getStackInSlot(2);
        
        int batches = 1 + capacityLayers;
        
        // BUG FIX: Ensure the output inventory isn't full, otherwise the furnace 
        // will keep running and literally void all the items into the ether!
        ItemStack currentSlag = slagInventory.getStackInSlot(0);
        boolean canFitSlag = currentSlag.isEmpty() || 
            (currentSlag.is(com.remizerexe.further_on.registry.FOItems.slag.get()) && currentSlag.getCount() + (SLAG_PER_LAYER * batches) <= slagInventory.getSlotLimit(0) && currentSlag.getCount() + (SLAG_PER_LAYER * batches) <= currentSlag.getMaxStackSize());
            
        boolean canFitFluid = steelTank.getSpace() >= (STEEL_MB_PER_BATCH * batches);
        
        // BUG FIX: Ensure we have enough inputs for the ENTIRE multiblock height, not just 1 batch!
        // Otherwise this acts as a massive dupe machine if the inventory runs low!
        return canFitSlag && canFitFluid
                && coke.is(com.remizerexe.further_on.registry.FOItems.COKE.get()) && coke.getCount() >= (COAL_PER_LAYER * batches)
                && iron.is(com.remizerexe.further_on.registry.FOItems.spongy_iron.get()) && iron.getCount() >= (IRON_PER_LAYER * batches)
                && calcite.is(Items.CALCITE) && calcite.getCount() >= (1 * batches); 
    }

    private void processOneBatch() {
        // BUG FIX: Unsafely calling shrink() on the raw stack can cause ghost item desyncs.
        // Use the native extractItem API to guarantee inventory synchronization across client/server.
        inputInventory.extractItem(0, COAL_PER_LAYER, false);
        inputInventory.extractItem(1, IRON_PER_LAYER, false);
        inputInventory.extractItem(2, 1, false);

        FluidStack steelFluid = new FluidStack(
                FOFluids.MOLTEN_STEEL_STILL.get(), STEEL_MB_PER_BATCH);
        steelTank.fill(steelFluid, IFluidHandler.FluidAction.EXECUTE);

        // BUG FIX: Same stack overflow crash potential here for the byproduct.
        ItemStack slagOutput = new ItemStack(com.remizerexe.further_on.registry.FOItems.slag.get(), SLAG_PER_LAYER);
        slagInventory.insertItem(0, slagOutput, false);

        accumulatedLayers = Math.max(0, accumulatedLayers - 1);
        setChanged();
    }

    private int readFanRPM() {
        Direction facing = getFacing();
        BlockPos fanPos = worldPosition.relative(facing.getOpposite(), 2);
        BlockEntity be = level.getBlockEntity(fanPos);
        if (be instanceof KineticBlockEntity kinetic) {
            return (int) Math.abs(kinetic.getSpeed());
        }
        return 0;
    }

    public void scanForDroppedItems() {
        if (level == null || level.isClientSide()) return;
        if (!isFormed()) return;

        Direction facing = getFacing();
        BlockPos centerPos = worldPosition.relative(facing.getOpposite(), 1);
        AABB scanBox = new AABB(centerPos);

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, scanBox);
        if (items.isEmpty()) return;

        for (ItemEntity itemEntity : items) {
            if (itemEntity.isRemoved()) continue; // Prevent dupe if two furnaces overlap!
            
            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) continue;

            int maxBuffer = 64 * 4; // Prevent infinite buffering / integer overflow lag machines

            if (stack.is(com.remizerexe.further_on.registry.FOItems.COKE.get()) && bufferedCoal < maxBuffer) {
                bufferedCoal += stack.getCount();
                itemEntity.discard();
            } else if (stack.is(com.remizerexe.further_on.registry.FOItems.spongy_iron.get()) && bufferedIron < maxBuffer) {
                bufferedIron += stack.getCount();
                itemEntity.discard();
            } else if (stack.is(Items.CALCITE) && bufferedCalcite < maxBuffer) {
                bufferedCalcite += stack.getCount();
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
    int bufferedCalcite = 0; // Added buffer for Calcite

    private void flushBuffer() {
        while (bufferedCoal >= COAL_PER_LAYER
                && bufferedIron >= IRON_PER_LAYER
                && bufferedCalcite >= 1
                && accumulatedLayers < (capacityLayers + 2) * BATCHES_PER_LAYER) {

            bufferedCoal -= COAL_PER_LAYER;
            bufferedIron -= IRON_PER_LAYER;
            bufferedCalcite -= 1;

            ItemStack cokeStack = new ItemStack(com.remizerexe.further_on.registry.FOItems.COKE.get(), COAL_PER_LAYER);
            ItemStack ironStack = new ItemStack(com.remizerexe.further_on.registry.FOItems.spongy_iron.get(), IRON_PER_LAYER);
            ItemStack calciteStack = new ItemStack(Items.CALCITE, 1);

            // BUG FIX: using stack.grow() bypasses NeoForge's internal limits and can crash
            // client-side rendering if a single ItemStack goes over 99. Using insertItem safely distributes it!
            inputInventory.insertItem(0, cokeStack, false);
            inputInventory.insertItem(1, ironStack, false);
            inputInventory.insertItem(2, calciteStack, false);

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

    public ItemStackHandler getInventoryForFace(Direction face) {
        if (face == Direction.DOWN) return slagInventory;
        if (face == Direction.UP) return inputInventory;
        return null;
    }

    public FluidTank getFluidTankForFace(Direction face) {
        Direction facing = getFacing();
        if (face == facing) return steelTank;
        return null;
    }

    @Override
    protected MultiblockStructure buildStructure(int capacityLayers) {
        return DEFINITION.buildStructure(capacityLayers);
    }

    @Override
    protected boolean isCapacityLayer(BlockPos centerPos) {
        if (level == null) return false;
        Direction facing = getFacing();
        BlockPos actualCenter = centerPos.relative(facing.getOpposite(), 1);

        return level.getBlockState(actualCenter).is(Blocks.AIR)
                && level.getBlockState(actualCenter.north()).is(FOBlocks.FIRE_CLAY_BRICKS.get())
                && level.getBlockState(actualCenter.south()).is(FOBlocks.FIRE_CLAY_BRICKS.get())
                && level.getBlockState(actualCenter.east()).is(FOBlocks.FIRE_CLAY_BRICKS.get())
                && level.getBlockState(actualCenter.west()).is(FOBlocks.FIRE_CLAY_BRICKS.get());
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

    public int   getAccumulatedLayers()  { return accumulatedLayers; }
    public int   getCurrentRPM()         { return currentRPM; }
    public float getProcessingProgress() { return processingProgress; }

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
        tag.putInt("BufferedCalcite",      bufferedCalcite);
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
        bufferedCalcite    = tag.getInt("BufferedCalcite");
        currentRPM         = tag.getInt("CurrentRPM");
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
    }

    public void dropContents() {
        if (level == null) return;
        
        // Drop buffered items that haven't been flushed to the main inventory yet, 
        // ensuring we respect the max stack size limit to prevent client rendering crashes.
        while (bufferedCoal > 0) {
            int toDrop = Math.min(bufferedCoal, 64);
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), new ItemStack(com.remizerexe.further_on.registry.FOItems.COKE.get(), toDrop));
            bufferedCoal -= toDrop;
        }
        while (bufferedIron > 0) {
            int toDrop = Math.min(bufferedIron, 64);
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), new ItemStack(com.remizerexe.further_on.registry.FOItems.spongy_iron.get(), toDrop));
            bufferedIron -= toDrop;
        }
        while (bufferedCalcite > 0) {
            int toDrop = Math.min(bufferedCalcite, 64);
            net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), new ItemStack(Items.CALCITE, toDrop));
            bufferedCalcite -= toDrop;
        }

        for (int i = 0; i < inputInventory.getSlots(); i++) {
            ItemStack stack = inputInventory.getStackInSlot(i);
            while (stack.getCount() > 0) {
                int toDrop = Math.min(stack.getCount(), stack.getMaxStackSize());
                net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(),
                        worldPosition.getY(), worldPosition.getZ(), stack.split(toDrop));
            }
        }
        for (int i = 0; i < slagInventory.getSlots(); i++) {
            ItemStack stack = slagInventory.getStackInSlot(i);
            while (stack.getCount() > 0) {
                int toDrop = Math.min(stack.getCount(), stack.getMaxStackSize());
                net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX(),
                        worldPosition.getY(), worldPosition.getZ(), stack.split(toDrop));
            }
        }
        accumulatedLayers = 0;
        bufferedCoal = 0;
        bufferedIron = 0;
        bufferedCalcite = 0; // BUG FIX: clear the calcite buffer on break!
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

        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("Blast Furnace Hearth")
                .withStyle(ChatFormatting.WHITE));

        tooltip.add(Component.literal(" Queued Crafts: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(accumulatedLayers + " / " + ((capacityLayers + 2) * 8))
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

        int slagCount = slagInventory.getStackInSlot(0).getCount();
        tooltip.add(Component.literal(" Slag: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.valueOf(slagCount))
                        .withStyle(ChatFormatting.DARK_GRAY)));

        return true;
    }
}
