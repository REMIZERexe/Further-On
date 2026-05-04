package com.remizerexe.further_on.content.pumpjack;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PumpjackRotationJointBlockEntity extends GeneratingKineticBlockEntity
        implements IBearingBlockEntity {

    public ControlledContraptionEntity movedContraption;
    public boolean running;
    public boolean assembleNextTick;
    protected float angle;
    protected float clientAngleDiff;
    protected AssemblyException lastException;
    private float prevAngle;

    // Oscillation max en degrés
    private static final float MAX_ANGLE = 15f;

    public PumpjackRotationJointBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(3);
    }

    @Override
    public boolean isWoodenTop() { return false; }

    @Override
    public void addBehaviours(java.util.List<com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }

    @Override
    public void remove() {
        if (!level.isClientSide) disassemble();
        super.remove();
    }

    public void assemble() {
        Direction facing = getBlockState().getValue(BearingBlock.FACING);
        PumpjackArmContraption contraption = new PumpjackArmContraption(facing);
        try {
            if (!contraption.assemble(level, worldPosition)) return;
            lastException = null;
        } catch (AssemblyException e) {
            lastException = e;
            sendData();
            return;
        }

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        movedContraption = ControlledContraptionEntity.create(level, this, contraption);
        BlockPos anchor = worldPosition.above();
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        movedContraption.setRotationAxis(facing.getAxis());
        level.addFreshEntity(movedContraption);

        running = true;
        angle = 0;
        sendData();
        updateGeneratedRotation();
    }

    public void disassemble() {
        if (!running && movedContraption == null) return;
        angle = 0;
        if (movedContraption != null) movedContraption.disassemble();
        movedContraption = null;
        running = false;
        updateGeneratedRotation();
        assembleNextTick = false;
        sendData();
    }

    @Override
    public void tick() {
        super.tick();
        prevAngle = angle;
        if (level.isClientSide) clientAngleDiff /= 2;

        if (!level.isClientSide && assembleNextTick) {
            assembleNextTick = false;
            if (running) {
                if (speed == 0 && movedContraption == null) {
                    disassemble();
                    return;
                }
            } else {
                if (speed == 0) return;
                assemble();
            }
        }

        if (!running) return;

        if (!(movedContraption != null && movedContraption.isStalled())) {
            // Oscillation sinusoïdale limitée à MAX_ANGLE
            float time = level.getGameTime();
            angle = (float) (MAX_ANGLE * Math.sin(Math.toRadians(time * getSpeed() * 0.3f)));
        }

        applyRotation();
    }

    public float getAngularSpeed() {
        float speed = convertToAngular(getSpeed());
        if (getSpeed() == 0) speed = 0;
        if (level.isClientSide) {
            speed *= com.simibubi.create.foundation.utility.ServerSpeedProvider.get();
            speed += clientAngleDiff / 3f;
        }
        return speed;
    }

    protected void applyRotation() {
        if (movedContraption == null) return;
        movedContraption.setAngle(angle);
        Direction facing = getBlockState().getValue(BearingBlock.FACING);
        movedContraption.setRotationAxis(facing.getAxis());
    }

    @Override
    public float getInterpolatedAngle(float partialTicks) {
        if (movedContraption == null || movedContraption.isStalled() || !running)
            partialTicks = 0;
        return Mth.lerp(partialTicks, angle, angle + getAngularSpeed());
    }

    @Override
    public void setAngle(float angle) { this.angle = angle; }

    @Override
    public void attach(ControlledContraptionEntity contraption) {
        this.movedContraption = contraption;
        setChanged();
        Direction facing = getBlockState().getValue(BearingBlock.FACING);
        BlockPos anchor = worldPosition.above();
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        movedContraption.setRotationAxis(facing.getAxis());
        if (!level.isClientSide) {
            this.running = true;
            sendData();
        }
    }

    @Override
    public void onStall() {
        if (!level.isClientSide) sendData();
    }

    @Override
    public boolean isValid() { return !isRemoved(); }

    @Override
    public boolean isAttachedTo(com.simibubi.create.content.contraptions.AbstractContraptionEntity contraption) {
        return movedContraption == contraption;
    }

    @Override
    public BlockPos getBlockPosition() { return worldPosition; }

    @Override
    public float getGeneratedSpeed() { return 0; }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putBoolean("Running", running);
        compound.putFloat("Angle", angle);
        AssemblyException.write(compound, registries, lastException);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (wasMoved) { super.read(compound, registries, clientPacket); return; }
        float angleBefore = angle;
        running = compound.getBoolean("Running");
        angle = compound.getFloat("Angle");
        lastException = AssemblyException.read(compound, registries);
        super.read(compound, registries, clientPacket);
        if (!clientPacket) return;
        if (running && (movedContraption == null || !movedContraption.isStalled()))
            clientAngleDiff = AngleHelper.getShortestAngleDiff(angleBefore, angle);
    }
}