package com.remizerexe.further_on.content.blast_furnace;

import com.remizerexe.further_on.multiblock.MultiblockComponentBlock;
import com.remizerexe.further_on.registry.FOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * The center block of the blast furnace base layer.
 * Looks like bricks but has a block entity that exposes the slag inventory
 * on its bottom face for Create funnels and belts.
 */
public class BlastFurnaceHatchBlock extends MultiblockComponentBlock {

    public BlastFurnaceHatchBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return FOBlockEntities.BLAST_FURNACE_HATCH.create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}