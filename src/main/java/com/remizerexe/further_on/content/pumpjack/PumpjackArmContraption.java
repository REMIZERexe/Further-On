package com.remizerexe.further_on.content.pumpjack;

import com.remizerexe.further_on.registry.FOContraptionTypes;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class PumpjackArmContraption extends BearingContraption {

    public PumpjackArmContraption() {}

    public PumpjackArmContraption(Direction facing) {
        this.facing = facing;
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        BlockPos offset = pos.above();
        if (!searchMovedStructure(world, offset, null))
            return false;
        startMoving(world);
        expandBoundsAroundAxis(facing.getAxis());
        return !blocks.isEmpty();
    }

    @Override
    public ContraptionType getType() {
        return FOContraptionTypes.PUMPJACK_ARM.value();
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return pos.equals(anchor.below());
    }
}