package com.remizerexe.further_on.content.pumpjack;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PumpjackBaseBlock extends KineticBlock implements IBE<PumpjackBaseBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public PumpjackBaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1
                && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, Half.BOTTOM);
        }
        return null;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (state.getValue(HALF) == Half.BOTTOM) {
            level.setBlock(pos.above(), state.setValue(HALF, Half.TOP), 3);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (state.getValue(HALF) == Half.BOTTOM) {
                BlockState above = level.getBlockState(pos.above());
                if (above.is(this) && above.getValue(HALF) == Half.TOP) {
                    level.removeBlock(pos.above(), false);
                }
            } else {
                BlockState below = level.getBlockState(pos.below());
                if (below.is(this) && below.getValue(HALF) == Half.BOTTOM) {
                    level.removeBlock(pos.below(), false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == Half.TOP) return true;
        return level.getBlockState(pos.above()).canBeReplaced();
    }

    // Shaft et cranks seulement sur le bloc du bas
    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getClockWise().getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if (state.getValue(HALF) == Half.TOP) return false;
        return face.getAxis() == state.getValue(FACING).getClockWise().getAxis();
    }

    @Override
    public Class<PumpjackBaseBlockEntity> getBlockEntityClass() {
        return PumpjackBaseBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PumpjackBaseBlockEntity> getBlockEntityType() {
        return com.remizerexe.further_on.registry.FOBlockEntities.PUMPJACK_BASE.get();
    }
}