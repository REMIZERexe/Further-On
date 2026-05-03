package com.remizerexe.further_on;

import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthScreen;
import com.remizerexe.further_on.registry.FOMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = FurtherOn.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FOClientSetup {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(FOMenuTypes.BLAST_FURNACE_HEARTH.get(),
                BlastFurnaceHearthScreen::new);
    }
}