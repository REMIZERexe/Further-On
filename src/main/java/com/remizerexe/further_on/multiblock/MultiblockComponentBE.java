package com.remizerexe.further_on.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for non-controller multiblock component block entities.
 * Stores a reference to the controller position so it can delegate
 * inventory/capability calls to the controller BE.
 */
public abstract class MultiblockComponentBE extends BlockEntity {

    /** Position of the multiblock controller this component belongs to. */
    protected BlockPos controllerPos = BlockPos.ZERO;

    public MultiblockComponentBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setControllerPos(BlockPos pos) {
        this.controllerPos = pos;
        setChanged();
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    /**
     * Attempts to retrieve the controller BE from the level.
     * Returns null if not found or wrong type.
     */
    @SuppressWarnings("unchecked")
    public <T extends MultiblockControllerBE> T getController(Class<T> type) {
        if (level == null) return null;
        BlockEntity be = level.getBlockEntity(controllerPos);
        if (type.isInstance(be)) return (T) be;
        return null;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("ControllerX", controllerPos.getX());
        tag.putInt("ControllerY", controllerPos.getY());
        tag.putInt("ControllerZ", controllerPos.getZ());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        controllerPos = new BlockPos(
                tag.getInt("ControllerX"),
                tag.getInt("ControllerY"),
                tag.getInt("ControllerZ")
        );
    }
}