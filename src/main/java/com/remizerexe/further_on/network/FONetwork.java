package com.remizerexe.further_on.network;

import com.remizerexe.further_on.FurtherOn;
import com.remizerexe.further_on.content.equipment.WeldingMaskItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FurtherOn.MODID, bus = EventBusSubscriber.Bus.MOD)
public class FONetwork {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ToggleWeldingMaskVisorPayload.TYPE,
                ToggleWeldingMaskVisorPayload.STREAM_CODEC,
                (payload, context) -> WeldingMaskItem.toggleVisor(context.player()));
    }
}
