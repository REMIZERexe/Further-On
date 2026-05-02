package com.remizerexe.further_on.multiblock;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for all multiblock controller blocks.
 *
 * Triggers revalidation on:
 * - Block placement (onPlace)
 * - Neighbor block changes (neighborChanged)
 * - Block removal (onRemove) — unforms before the BE is destroyed
 *
 * Subclasses handle player interaction (useWithoutItem) directly,
 * since each controller may want different feedback or GUI behavior.
 */
public abstract class MultiblockControllerBlock extends BaseEntityBlock {

    protected MultiblockControllerBlock(Properties properties) {
        super(properties);
    }

    // -------------------------------------------------------------------------
    // Revalidation triggers
    // -------------------------------------------------------------------------

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos,
                        BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        revalidate(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                net.minecraft.world.level.block.Block block,
                                BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        revalidate(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            // Unform the structure before the block entity is removed
            revalidate(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Retrieves the controller BE at the given position and calls revalidate().
     * No-ops on the client side — validation is server-only.
     */
    protected void revalidate(Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MultiblockControllerBE controller) {
            controller.revalidate();
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /**
     * Multiblock controller blocks do not use the codec system.
     * Each subclass provides its own newBlockEntity() implementation.
     */
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new UnsupportedOperationException("Multiblock controller blocks do not use codecs.");
    }
}