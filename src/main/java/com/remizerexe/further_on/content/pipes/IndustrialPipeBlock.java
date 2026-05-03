package com.remizerexe.further_on.content.pipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class IndustrialPipeBlock extends Block implements EntityBlock {

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST  = BooleanProperty.create("east");
    public static final BooleanProperty WEST  = BooleanProperty.create("west");
    public static final BooleanProperty UP    = BooleanProperty.create("up");
    public static final BooleanProperty DOWN  = BooleanProperty.create("down");

    private static final VoxelShape CORE     = Block.box(4, 4, 4, 12, 12, 12);
    private static final VoxelShape ARM_NORTH = Block.box(4, 4, 0,  12, 12, 4);
    private static final VoxelShape ARM_SOUTH = Block.box(4, 4, 12, 12, 12, 16);
    private static final VoxelShape ARM_EAST  = Block.box(12, 4, 4, 16, 12, 12);
    private static final VoxelShape ARM_WEST  = Block.box(0,  4, 4, 4,  12, 12);
    private static final VoxelShape ARM_UP    = Block.box(4, 12, 4, 12, 16, 12);
    private static final VoxelShape ARM_DOWN  = Block.box(4, 0,  4, 12, 4,  12);

    public IndustrialPipeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CORE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, ARM_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, ARM_SOUTH);
        if (state.getValue(EAST))  shape = Shapes.or(shape, ARM_EAST);
        if (state.getValue(WEST))  shape = Shapes.or(shape, ARM_WEST);
        if (state.getValue(UP))    shape = Shapes.or(shape, ARM_UP);
        if (state.getValue(DOWN))  shape = Shapes.or(shape, ARM_DOWN);
        return shape;
    }

    private boolean connectsTo(LevelAccessor level, BlockPos pos, Direction dir) {
        BlockPos neighbor = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighbor);
        if (neighborState.getBlock() instanceof IndustrialPipeBlock) return true;
        if (level instanceof Level world) {
            var cap = world.getCapability(Capabilities.FluidHandler.BLOCK, neighbor, dir.getOpposite());
            return cap != null;
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return defaultBlockState()
                .setValue(NORTH, connectsTo(level, pos, Direction.NORTH))
                .setValue(SOUTH, connectsTo(level, pos, Direction.SOUTH))
                .setValue(EAST,  connectsTo(level, pos, Direction.EAST))
                .setValue(WEST,  connectsTo(level, pos, Direction.WEST))
                .setValue(UP,    connectsTo(level, pos, Direction.UP))
                .setValue(DOWN,  connectsTo(level, pos, Direction.DOWN));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(directionToProperty(direction), connectsTo(level, pos, direction));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide()) {
            Direction dir = null;
            for (Direction d : Direction.values()) {
                if (pos.relative(d).equals(neighborPos)) { dir = d; break; }
            }
            if (dir != null) {
                BooleanProperty prop = directionToProperty(dir);
                BlockState newState = state.setValue(prop, connectsTo(level, pos, dir));
                if (newState != state) level.setBlock(pos, newState, 3);
            }
        }
    }

    private BooleanProperty directionToProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            case UP    -> UP;
            case DOWN  -> DOWN;
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return com.remizerexe.further_on.registry.FOBlockEntities.INDUSTRIAL_PIPE.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof IndustrialPipeBlockEntity pipe) pipe.tick();
        };
    }
}