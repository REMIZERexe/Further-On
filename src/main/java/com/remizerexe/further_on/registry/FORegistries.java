package com.remizerexe.further_on.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.remizerexe.further_on.FurtherOn.MODID;
import static net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB;

public class FORegistries {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<net.minecraft.world.item.crafting.RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_TYPE, MODID);

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
    }
}
