package com.remizerexe.further_on.content.cementation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.remizerexe.further_on.registry.FOBlocks;
import com.remizerexe.further_on.registry.FOPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Draws the swinging door, the coke fill in the pit and the ingots on the chimney floor. */
public class CementationOvenRenderer implements BlockEntityRenderer<CementationOvenBlockEntity> {

    /** Door hinge x position in block units (3px). */
    private static final float HINGE_X = 3f / 16f;

    private static final float INGOT_SCALE = 0.3f;
    private static final float INGOT_PITCH = 1f / 3f;
    private static final float INGOT_FLOOR_OFFSET = 0.02f;

    public CementationOvenRenderer(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(CementationOvenBlockEntity oven, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        renderDoor(oven, partialTick, poseStack, bufferSource, packedLight);
        if (!oven.isFormed()) return;
        renderContents(oven, poseStack, bufferSource, packedOverlay);
    }

    /** Coke fill in the pit, one block model per unit, then the ingots on the chimney floor. */
    private void renderContents(CementationOvenBlockEntity oven, PoseStack poseStack,
                                MultiBufferSource bufferSource, int packedOverlay) {
        BlockPos origin = oven.getBlockPos();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockState coke = FOBlocks.COKE_BLOCK.getDefaultState();

        List<BlockPos> pit = oven.pitPositions();
        int fill = Math.min(oven.getCoke(), pit.size());
        for (int i = 0; i < fill; i++) {
            BlockPos cell = pit.get(i);
            poseStack.pushPose();
            poseStack.translate(cell.getX() - origin.getX(), cell.getY() - origin.getY(), cell.getZ() - origin.getZ());
            blockRenderer.renderSingleBlock(coke, poseStack, bufferSource,
                    LevelRenderer.getLightColor(oven.getLevel(), cell), packedOverlay);
            poseStack.popPose();
        }

        if (oven.isLit()) return;
        ItemStack stack = oven.getContents();
        if (stack.isEmpty() && oven.hasOutputs()) stack = oven.getOutputs().get(0);
        if (stack.isEmpty()) return;
        BlockPos floor = oven.chimneyFloor();
        int light = LevelRenderer.getLightColor(oven.getLevel(), floor);
        float facingRot = -oven.getFacing().toYRot();
        int count = Math.min(stack.getCount(), CementationOvenBlockEntity.MAX_INGOTS);
        for (int i = 0; i < count; i++) {
            int cx = i % 3 - 1;
            int cz = i / 3 - 1;
            poseStack.pushPose();
            poseStack.translate(floor.getX() - origin.getX() + 0.5,
                    floor.getY() - origin.getY() + INGOT_FLOOR_OFFSET,
                    floor.getZ() - origin.getZ() + 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(facingRot));
            poseStack.translate(cx * INGOT_PITCH, 0, cz * INGOT_PITCH);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(INGOT_SCALE, INGOT_SCALE, INGOT_SCALE);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, packedOverlay,
                    poseStack, bufferSource, oven.getLevel(), i);
            poseStack.popPose();
        }
    }

    /**
     * Swings the door partial model about a hinge on the opening's west edge.
     * The partial is authored facing north; the block's facing is applied
     * first so the hinge follows the blockstate rotation.
     */
    private void renderDoor(CementationOvenBlockEntity oven, float partialTick, PoseStack poseStack,
                            MultiBufferSource bufferSource, int packedLight) {
        Direction facing = oven.getFacing();
        float angle = oven.getDoorAngle(partialTick);

        poseStack.pushPose();
        // Match the blockstate rotation (horizontalBlock: (toYRot + 180) % 360, clockwise from above).
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - facing.toYRot()));
        poseStack.translate(-0.5, -0.5, -0.5);

        // Hinge at x=3px on the front face (z=0); positive rotation swings the door outward.
        poseStack.translate(HINGE_X, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(-HINGE_X, 0, 0);

        CachedBuffers.partial(FOPartialModels.CEMENTATION_OVEN_DOOR, oven.getBlockState())
                .light(packedLight)
                .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(CementationOvenBlockEntity oven) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
