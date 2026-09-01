package com.remizerexe.further_on.content.power;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.remizerexe.further_on.api.power.VoltageTier;
import com.remizerexe.further_on.api.power.PowerDamageHandler;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.remizerexe.further_on.registry.FOBlockEntities;

public class LiveWireBlock extends Block implements IBE<LiveWireBlockEntity> {
    
    private final VoltageTier operatingVoltage;

    public LiveWireBlock(Properties properties, VoltageTier voltage) {
        super(properties);
        this.operatingVoltage = voltage;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        // ZAP!
        PowerDamageHandler.shockEntity(entity, operatingVoltage, level);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        // ZAP if they walk through it (important for when we make it a thin cable hitbox)
        PowerDamageHandler.shockEntity(entity, operatingVoltage, level);
    }

    @Override
    public Class<LiveWireBlockEntity> getBlockEntityClass() {
        return LiveWireBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LiveWireBlockEntity> getBlockEntityType() {
        return FOBlockEntities.LIVE_WIRE.get();
    }
}
