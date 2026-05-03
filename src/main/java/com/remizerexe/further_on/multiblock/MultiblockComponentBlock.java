package com.remizerexe.further_on.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for non-controller multiblock component blocks.
 * Implements EntityBlock so subclasses can attach a block entity.
 */
public abstract class MultiblockComponentBlock extends Block implements EntityBlock {

    protected MultiblockComponentBlock(Properties properties) {
        super(properties);
    }
}