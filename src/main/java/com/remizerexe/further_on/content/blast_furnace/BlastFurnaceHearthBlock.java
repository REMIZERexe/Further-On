package com.remizerexe.further_on.content.blast_furnace;

import com.remizerexe.further_on.multiblock.MultiblockControllerBlock;
import com.remizerexe.further_on.registry.FOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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

public class BlastFurnaceHearthBlock extends MultiblockControllerBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlastFurnaceHearthBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // -------------------------------------------------------------------------
    // Block state
    // -------------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * When placed, the hearth faces the direction the player is looking.
     * The structure extends in that direction.
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    // -------------------------------------------------------------------------
    // Block entity
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return FOBlockEntities.BLAST_FURNACE_HEARTH.create(pos, state);
    }

    /**
     * Server-side only ticker — drives the hearth's processing logic each tick.
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                  BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            // Client ticker — smoke particles only
            return (lvl, pos, st, be) -> {
                if (be instanceof BlastFurnaceHearthBlockEntity hearth) {
                    hearth.clientTick();
                }
            };
        }
        // Server ticker — processing logic
        return (lvl, pos, st, be) -> {
            if (be instanceof BlastFurnaceHearthBlockEntity hearth) {
                hearth.tick();
            }
        };
    }

    // -------------------------------------------------------------------------
    // Player interaction
    // -------------------------------------------------------------------------

    /**
     * Right-click the hearth to see its current status.
     * When unformed, triggers a revalidation attempt.
     */
    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level,
                                            BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BlastFurnaceHearthBlockEntity hearth) {
            if (!hearth.isFormed()) {
                hearth.revalidate();
                if (!hearth.isFormed()) {
                    player.displayClientMessage(
                            Component.literal("Structure incomplete."), true);
                    return InteractionResult.CONSUME;
                }
            }
            // Ouvre le GUI
            net.minecraft.server.level.ServerPlayer serverPlayer =
                    (net.minecraft.server.level.ServerPlayer) player;
            player.openMenu(
                    new net.minecraft.world.MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.literal("Blast Furnace Hearth");
                        }
                        @Override
                        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(
                                int id, Inventory inv, Player p) {
                            return new BlastFurnaceHearthMenu(id, inv, hearth);
                        }
                    },
                    buf -> buf.writeBlockPos(pos)
            );
        }
        return InteractionResult.CONSUME;
    }

    // In BlastFurnaceHearthBlock:
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlastFurnaceHearthBlockEntity hearth) {
                hearth.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}