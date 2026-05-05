package com.remizerexe.further_on.content.pumpjack;

import com.remizerexe.further_on.registry.FOBlocks;
import com.remizerexe.further_on.registry.FOContraptionTypes;
import com.remizerexe.further_on.content.pumpjack.PumpjackRotationAxleBlock;
import com.remizerexe.further_on.content.pumpjack.PumpjackHeadBlock;
import com.remizerexe.further_on.content.pumpjack.PumpjackRotationLinkBlock;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
        if (blocks.isEmpty()) return false;

        // Validation
        if (!validateArm(world, pos)) return false;

        return true;
    }

    private boolean validateArm(Level world, BlockPos jointPos) {
        BlockPos axlePos = null;
        BlockPos headPos = null;
        BlockPos linkPos = null;
        int axleCount = 0, headCount = 0, linkCount = 0;

        for (var entry : blocks.entrySet()) {
            // Les positions dans blocks sont relatives — on les convertit en absolues
            BlockPos worldPos = entry.getValue().pos().offset(anchor);
            Block block = entry.getValue().state().getBlock();

            if (block instanceof PumpjackRotationAxleBlock) {
                axleCount++;
                axlePos = worldPos;
            } else if (block instanceof PumpjackHeadBlock) {
                headCount++;
                headPos = worldPos;
            } else if (block instanceof PumpjackRotationLinkBlock) {
                linkCount++;
                linkPos = worldPos;
            }
        }

        if (axleCount != 1 || headCount != 1 || linkCount != 1) return false;
        if (!axlePos.equals(jointPos.above())) return false;

        int distHead = Math.abs(axlePos.getX() - headPos.getX()) + Math.abs(axlePos.getZ() - headPos.getZ());
        int distLink = Math.abs(axlePos.getX() - linkPos.getX()) + Math.abs(axlePos.getZ() - linkPos.getZ());

        return distHead >= 4 && distLink >= 4;
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