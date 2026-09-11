package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.FurtherOn;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class FOPartialModels {

    public static final PartialModel PUMPJACK_CRANK =
            PartialModel.of(FurtherOn.asResource("block/pumpjackbase_crank"));

    /** Hinged door over the cementation oven's front opening, animated by its renderer. */
    public static final PartialModel CEMENTATION_OVEN_DOOR =
            PartialModel.of(FurtherOn.asResource("block/cementation_oven_door"));

    public static void init() {}
}