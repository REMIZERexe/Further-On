package com.remizerexe.further_on.content.oil;

import com.remizerexe.further_on.registry.FOFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class OilFluid extends BaseFlowingFluid {

    /** Oil flows slower than water (water=5, lava=2), oil=3 */
    @Override
    protected int getSlopeFindDistance(LevelReader level) { return 3; }

    /** How many blocks oil drops per horizontal block (water=1, lava=2), oil=2 */
    @Override
    protected int getDropOff(LevelReader level) { return 2; }

    /** Tick rate — higher = slower flow. Water=5, lava=30, oil=20 */
    @Override
    public int getTickDelay(LevelReader level) { return 20; }

    /** Spread speed — lower = slower. Water=4, lava=2, oil=2 */
    @Override
    protected float getExplosionResistance() { return 100f; }

    protected OilFluid(Properties properties) {
        super(properties);
    }

    // -------------------------------------------------------------------------
    // Still variant
    // -------------------------------------------------------------------------

    public static class Still extends OilFluid {
        public Still(Properties properties) { super(properties); }

        @Override public boolean isSource(FluidState state) { return true; }
        @Override public int getAmount(FluidState state) { return 8; }
    }

    // -------------------------------------------------------------------------
    // Flowing variant
    // -------------------------------------------------------------------------

    public static class Flowing extends OilFluid {
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