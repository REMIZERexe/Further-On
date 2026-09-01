package com.remizerexe.further_on.network;

import com.remizerexe.further_on.FurtherOn;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ToggleWeldingMaskVisorPayload() implements CustomPacketPayload {
    public static final ToggleWeldingMaskVisorPayload INSTANCE = new ToggleWeldingMaskVisorPayload();
    public static final CustomPacketPayload.Type<ToggleWeldingMaskVisorPayload> TYPE = new CustomPacketPayload.Type<>(
            FurtherOn.asResource("toggle_welding_mask_visor"));
    public static final StreamCodec<ByteBuf, ToggleWeldingMaskVisorPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
