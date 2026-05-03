package com.remizerexe.further_on.content.blast_furnace;

import com.remizerexe.further_on.registry.FOFluids;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class MoltenSteelFluid extends BaseFlowingFluid {

    @Override protected int getSlopeFindDistance(LevelReader level) { return 2; }
    @Override protected int getDropOff(LevelReader level) { return 2; }
    @Override public int getTickDelay(LevelReader level) { return 30; }
    @Override protected float getExplosionResistance() { return 100f; }

    protected MoltenSteelFluid(Properties properties) { super(properties); }

    public static class Still extends MoltenSteelFluid {
        public Still(Properties properties) { super(properties); }
        @Override public boolean isSource(FluidState state) { return true; }
        @Override public int getAmount(FluidState state) { return 8; }
    }

    public static class Flowing extends MoltenSteelFluid {
        public Flowing(Properties properties) { super(properties); }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override public boolean isSource(FluidState state) { return false; }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
    }
}