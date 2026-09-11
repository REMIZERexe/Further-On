package com.remizerexe.further_on.content.cementation;

import com.remizerexe.further_on.multiblock.MultiblockControllerBlock;
import com.remizerexe.further_on.registry.FOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Controller block of the cementation oven: the front block with the hinged
 * door. The structure extends behind it.
 */
public class CementationOvenBlock extends MultiblockControllerBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CementationOvenBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /** Faces the player when placed; the structure extends away from the player. */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return FOBlockEntities.CEMENTATION_OVEN.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof CementationOvenBlockEntity oven) oven.clientTick();
            };
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof CementationOvenBlockEntity oven) oven.tick();
        };
    }

    // -------------------------------------------------------------------------
    // Player interaction
    // -------------------------------------------------------------------------

    /**
     * Empty hand: sneaking toggles the door; an unformed oven re-checks its
     * structure; a formed one hands out its contents.
     */
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof CementationOvenBlockEntity oven)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            oven.toggleDoor();
            return InteractionResult.CONSUME;
        }
        if (!oven.isFormed()) {
            oven.revalidate();
            player.displayClientMessage(Component.literal(
                    oven.isFormed() ? "Structure formed!" : "Structure incomplete."), true);
            return InteractionResult.CONSUME;
        }
        return oven.useWithoutItem(player);
    }

    /** Coke blocks, recipe ingots and flint and steel reach the oven; everything else falls through. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!(level.getBlockEntity(pos) instanceof CementationOvenBlockEntity oven) || !oven.isFormed()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!oven.acceptsItem(stack)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        return oven.useItemOn(stack, player, hand);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CementationOvenBlockEntity oven) oven.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
