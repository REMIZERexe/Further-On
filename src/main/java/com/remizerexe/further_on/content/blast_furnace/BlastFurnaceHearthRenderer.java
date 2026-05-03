package com.remizerexe.further_on.content.blast_furnace;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.remizerexe.further_on.FurtherOn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;

public class BlastFurnaceHearthRenderer
        implements BlockEntityRenderer<BlastFurnaceHearthBlockEntity> {

    /** Thickness of one accumulated layer in block units (1/8 of a block). */
    private static final float LAYER_HEIGHT = 1f / 8f;

    /**
     * Interior width of the chimney — the center column is 1 block wide
     * but we inset slightly so layers don't z-fight with walls.
     */
    private static final float INTERIOR_SIZE = 0.98f;
    private static final float INTERIOR_OFFSET = (1f - INTERIOR_SIZE) / 2f;

    public BlastFurnaceHearthRenderer(BlockEntityRendererProvider.Context context) { }

    @Override
    public void render(BlastFurnaceHearthBlockEntity be, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {

        if (!be.isFormed()) return;
        if (be.getAccumulatedLayers() <= 0) return;

        // Get the coal block texture from the block atlas
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModelShaper()
                .getParticleIcon(Blocks.COAL_BLOCK.defaultBlockState());

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());

        Direction facing = be.getFacing();

        // The center of the chimney interior is 1 block in front of the controller
        // We render relative to the BE position, so forward offset = facing vector
        float centerX = 0.5f - facing.getStepX();
        float centerZ = 0.5f - facing.getStepZ();

        // Bottom of the chimney interior starts at Y=1 (one above controller)
        float baseY = 1.0f;

        // Each physical capacity block holds 8 input layers
        // Total chimney height = capacityLayers blocks
        int maxLayers = be.getCapacityLayers() * 8;
        int currentLayers = be.getAccumulatedLayers();

        for (int i = 0; i < currentLayers; i++) {
            float y = LAYER_HEIGHT*(1 + i);
            renderLayer(poseStack, consumer, sprite,
                    centerX, y, centerZ,
                    net.minecraft.client.renderer.LightTexture.FULL_BRIGHT,
                    packedOverlay);
        }
    }

    /**
     * Renders a single horizontal quad (flat layer) at the given world-relative position.
     */
    private void renderLayer(PoseStack poseStack, VertexConsumer consumer,
                             TextureAtlasSprite sprite,
                             float cx, float y, float cz,
                             int packedLight, int packedOverlay) {

        float minX = cx - INTERIOR_SIZE / 2f;
        float maxX = cx + INTERIOR_SIZE / 2f;
        float minZ = cz - INTERIOR_SIZE / 2f;
        float maxZ = cz + INTERIOR_SIZE / 2f;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        PoseStack.Pose pose = poseStack.last();

        // Top face — normal pointing up
        consumer.addVertex(pose, minX, y, minZ).setColor(1f, 1f, 1f, 1f)
                .setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        consumer.addVertex(pose, minX, y, maxZ).setColor(1f, 1f, 1f, 1f)
                .setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        consumer.addVertex(pose, maxX, y, maxZ).setColor(1f, 1f, 1f, 1f)
                .setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
        consumer.addVertex(pose, maxX, y, minZ).setColor(1f, 1f, 1f, 1f)
                .setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight)
                .setNormal(pose, 0, 1, 0);
    }

    @Override
    public boolean shouldRenderOffScreen(BlastFurnaceHearthBlockEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}