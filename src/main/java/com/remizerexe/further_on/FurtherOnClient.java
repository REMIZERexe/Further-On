package com.remizerexe.further_on;

import com.remizerexe.further_on.content.ponder.FOPonderPlugin;
import com.remizerexe.further_on.registry.FOBlockEntities;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static com.remizerexe.further_on.FurtherOn.MODID;
import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthRenderer;

@Mod(value = MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class FurtherOnClient {
    public FurtherOnClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new FOPonderPlugin());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(
            net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                FOBlockEntities.BLAST_FURNACE_HEARTH.get(),
                BlastFurnaceHearthRenderer::new
        );
    }
}
