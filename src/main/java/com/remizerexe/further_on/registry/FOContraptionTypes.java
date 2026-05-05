package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.FurtherOn;
import com.remizerexe.further_on.content.pumpjack.PumpjackArmContraption;
import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public class FOContraptionTypes {

    public static final Holder.Reference<ContraptionType> PUMPJACK_ARM =
            register("pumpjack_arm", PumpjackArmContraption::new);

    private static Holder.Reference<ContraptionType> register(String name,
                                                              Supplier<? extends Contraption> factory) {
        ContraptionType type = new ContraptionType(factory);
        AllContraptionTypes.BY_LEGACY_NAME.put(name, type);
        return Registry.registerForHolder(
                CreateBuiltInRegistries.CONTRAPTION_TYPE,
                FurtherOn.asResource(name),
                type);
    }

    public static void prepare() {}
}