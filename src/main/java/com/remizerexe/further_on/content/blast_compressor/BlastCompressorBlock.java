package com.remizerexe.further_on.content.blast_compressor;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import com.remizerexe.further_on.registry.FOBlockEntities;

public class BlastCompressorBlock extends Block implements IBE<BlastCompressorBlockEntity> {
    public BlastCompressorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlastCompressorBlockEntity compressor) {
                if (compressor.interact(player, hand)) {
                    return ItemInteractionResult.SUCCESS;
                }
            }
        } else {
            // Client side needs to predict success to swing the arm correctly without ghost items
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlastCompressorBlockEntity compressor) {
                ItemStack held = player.getItemInHand(hand);
                ItemStack inSlot = compressor.inventory.getStackInSlot(0);
                if ((held.isEmpty() && !inSlot.isEmpty()) || (!held.isEmpty() && inSlot.isEmpty())) {
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public Class<BlastCompressorBlockEntity> getBlockEntityClass() {
        return BlastCompressorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlastCompressorBlockEntity> getBlockEntityType() {
        return FOBlockEntities.BLAST_COMPRESSOR_BE.get();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlastCompressorBlockEntity compressor) {
                net.minecraft.world.Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), compressor.inventory.getStackInSlot(0));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
