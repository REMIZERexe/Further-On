package com.remizerexe.further_on.content.pumpjack;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackBaseBlockEntity extends KineticBlockEntity {

    // Angle des cranks en degrés — calculé à partir de la vitesse de rotation
    public float crankAngle = 0f;

    // Rayon du crank en blocs (distance du centre au bout)
    public static final float CRANK_RADIUS = 0.7f;

    // Position Y du bout du crank (varie entre -radius et +radius)
    public float crankHeightOffset = 0f;

    public PumpjackBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || !level.isClientSide()) return;

        float speed = getSpeed();
        if (speed == 0) return;

        float time = net.createmod.catnip.animation.AnimationTickHolder.getRenderTime(level);
        crankAngle = (time * speed *0.3f) % 360f;
        crankHeightOffset = (float) (CRANK_RADIUS * Math.sin(Math.toRadians(crankAngle)));
    }

    // Expose l'angle pour le renderer
    public float getCrankAngle() { return crankAngle; }

    // Direction du facing
    public Direction getFacing() {
        return getBlockState().getValue(PumpjackBaseBlock.FACING);
    }

    @Override
    public void addBehaviours(java.util.List<com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour> behaviours) {}
}