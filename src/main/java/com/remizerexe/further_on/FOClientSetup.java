package com.remizerexe.further_on;

import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthScreen;
import com.remizerexe.further_on.content.pumpjack.PumpjackBaseRenderer;
import com.remizerexe.further_on.registry.FOBlockEntities;
import com.remizerexe.further_on.registry.FOMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = FurtherOn.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FOClientSetup {

    @SubscribeEvent
    public static void registerRenderers(RegisterMenuScreensEvent event) {
        // déjà existant
    }

    @SubscribeEvent
    public static void onRegisterRenderers(
            net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                FOBlockEntities.PUMPJACK_BASE.get(),
                PumpjackBaseRenderer::new);
    }
}
