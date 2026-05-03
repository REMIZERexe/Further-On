package com.remizerexe.further_on.content.pipes;

import com.remizerexe.further_on.registry.FOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class IndustrialPipeBlockEntity extends BlockEntity {

    public static final int CAPACITY = 800;

    public final FluidTank tank = new FluidTank(CAPACITY) {
        @Override protected void onContentsChanged() { setChanged(); }
    };

    public IndustrialPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide()) return;
        if (tank.isEmpty()) return;

        BlockState state = getBlockState();

        for (Direction dir : Direction.values()) {
            var prop = switch (dir) {
                case NORTH -> IndustrialPipeBlock.NORTH;
                case SOUTH -> IndustrialPipeBlock.SOUTH;
                case EAST  -> IndustrialPipeBlock.EAST;
                case WEST  -> IndustrialPipeBlock.WEST;
                case UP    -> IndustrialPipeBlock.UP;
                case DOWN  -> IndustrialPipeBlock.DOWN;
            };
            if (!state.getValue(prop)) continue;

            BlockPos neighborPos = worldPosition.relative(dir);
            if (level.getBlockState(neighborPos).getBlock() instanceof IndustrialPipeBlock) continue;

            var handler = level.getCapability(Capabilities.FluidHandler.BLOCK,
                    neighborPos, dir.getOpposite());
            if (handler == null) continue;

            FluidStack toInsert = tank.getFluid().copy();
            int inserted = handler.fill(toInsert, IFluidHandler.FluidAction.EXECUTE);
            if (inserted > 0) {
                tank.drain(inserted, IFluidHandler.FluidAction.EXECUTE);
                break;
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Tank", tank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Tank"))
            tank.readFromNBT(registries, tag.getCompound("Tank"));
    }
}