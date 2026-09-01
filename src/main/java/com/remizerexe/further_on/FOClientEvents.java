package com.remizerexe.further_on;

import com.remizerexe.further_on.content.equipment.WeldingMaskItem;
import com.remizerexe.further_on.network.ToggleWeldingMaskVisorPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = FurtherOn.MODID, value = Dist.CLIENT)
public class FOClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        while (FOClientSetup.TOGGLE_WELDING_MASK_VISOR.get().consumeClick()) {
            if (mc.player != null && WeldingMaskItem.isWearing(mc.player))
                PacketDistributor.sendToServer(ToggleWeldingMaskVisorPayload.INSTANCE);
        }
    }
}
