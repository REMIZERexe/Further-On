package com.remizerexe.further_on.content.pumpjack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.remizerexe.further_on.registry.FOPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

public class PumpjackBaseRenderer extends KineticBlockEntityRenderer<PumpjackBaseBlockEntity> {

    public PumpjackBaseRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(PumpjackBaseBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {

        BlockState blockState = be.getBlockState();

        // Ne rendre que sur le bloc du bas — le modèle des cranks monte au niveau du TOP
        if (blockState.getValue(PumpjackBaseBlock.HALF) != Half.BOTTOM) return;

        Direction facing = blockState.getValue(PumpjackBaseBlock.FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        float angle = be.crankAngle;

        ms.pushPose();
        var msr = TransformStack.of(ms);

        // Centre du bloc du haut (1 bloc au-dessus)
        msr.translate(0.5, 1.5, 0.5);

        if (facing.getAxis() == Direction.Axis.X) {
            msr.rotateDegrees(90, Direction.Axis.Y);
        }

        msr.rotateDegrees(angle, Direction.Axis.X);
        msr.translate(-0.5, -0.5, -0.5);

        CachedBuffers.partial(FOPartialModels.PUMPJACK_CRANK, blockState)
                .light(light)
                .renderInto(ms, vb);

        ms.popPose();
    }

    @Override
    protected BlockState getRenderedBlockState(PumpjackBaseBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}