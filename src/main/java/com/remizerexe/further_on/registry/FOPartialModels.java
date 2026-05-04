package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.FurtherOn;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class FOPartialModels {

    public static final PartialModel PUMPJACK_CRANK =
            PartialModel.of(FurtherOn.asResource("block/pumpjackbase_crank"));

    @net.neoforged.bus.api.SubscribeEvent
    public static void init() {}
}