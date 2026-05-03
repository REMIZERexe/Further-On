package com.remizerexe.further_on.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/**
 * Tracks whether a multiblock structure is currently formed,
 * and where its controller is. Serializes to NBT for chunk persistence.
 */
public class MultiblockState {

    private boolean formed;
    private BlockPos controllerPos;

    public MultiblockState() {
        this.formed = false;
        this.controllerPos = BlockPos.ZERO;
    }

    public MultiblockState(boolean formed, BlockPos controllerPos) {
        this.formed = formed;
        this.controllerPos = controllerPos;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public boolean isFormed() {
        return formed;
    }

    public void setFormed(boolean formed) {
        this.formed = formed;
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    // -------------------------------------------------------------------------
    // NBT serialization
    // -------------------------------------------------------------------------

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Formed", formed);
        tag.putInt("ControllerX", controllerPos.getX());
        tag.putInt("ControllerY", controllerPos.getY());
        tag.putInt("ControllerZ", controllerPos.getZ());
        return tag;
    }

    public static MultiblockState load(CompoundTag tag) {
        boolean formed = tag.getBoolean("Formed");
        BlockPos pos = new BlockPos(
                tag.getInt("ControllerX"),
                tag.getInt("ControllerY"),
                tag.getInt("ControllerZ")
        );
        return new MultiblockState(formed, pos);
    }
}