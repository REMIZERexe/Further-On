package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.content.oil.OilClusterFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.List;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOWorldgen {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, MODID);

    public static final DeferredHolder<Feature<?>, OilClusterFeature> OIL_CLUSTER_FEATURE =
            FEATURES.register("oil_cluster", () ->
                    new OilClusterFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }
}