package com.remizerexe.further_on;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.remizerexe.further_on.content.equipment.WeldingMaskItem;
import com.remizerexe.further_on.content.pumpjack.PumpjackBaseRenderer;
import com.remizerexe.further_on.registry.FOBlockEntities;
import com.remizerexe.further_on.registry.FOItems;
import com.remizerexe.further_on.registry.FOMenuTypes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = FurtherOn.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FOClientSetup {

    @SubscribeEvent
    public static void registerRenderers(RegisterMenuScreensEvent event) {
        // déjà existant
    }

    // ARGB: ~40% opacity purple, dark enough to read as "welding glass"
    private static final int WELDING_MASK_TINT = 0x665B2A86;
    // Vignette: near-opaque black edges fading to a clear central slit
    private static final ResourceLocation WELDING_MASK_OVERLAY = FurtherOn
            .asResource("textures/misc/welding_mask_overlay.png");

    public static final Lazy<KeyMapping> TOGGLE_WELDING_MASK_VISOR = Lazy.of(() -> new KeyMapping(
            "key.further_on.toggle_welding_mask_visor", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.further_on"));

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_WELDING_MASK_VISOR.get());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                FOItems.WELDING_MASK.get(),
                FurtherOn.asResource("open"),
                (stack, level, entity, seed) -> WeldingMaskItem.isVisorOpen(stack) ? 1.0F : 0.0F));
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerBelowAll(FurtherOn.asResource("welding_mask_tint"), (guiGraphics, deltaTracker) -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null)
                return;
            if (!WeldingMaskItem.isProtecting(player))
                return;
            guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), WELDING_MASK_TINT);
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            guiGraphics.blit(WELDING_MASK_OVERLAY, 0, 0, -90, 0.0F, 0.0F,
                    guiGraphics.guiWidth(), guiGraphics.guiHeight(),
                    guiGraphics.guiWidth(), guiGraphics.guiHeight());
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(
            net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                FOBlockEntities.PUMPJACK_BASE.get(),
                PumpjackBaseRenderer::new);
    }
}
