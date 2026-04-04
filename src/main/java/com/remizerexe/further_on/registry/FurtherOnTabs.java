package com.remizerexe.further_on.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.remizerexe.further_on.FurtherOn.MODID;
import static com.remizerexe.further_on.registry.FurtherOnBlocks.*;
import static com.remizerexe.further_on.registry.FurtherOnItems.FIRE_CLAY;

public class FurtherOnTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FURTHER_ON_TAB = CREATIVE_MODE_TABS.register("zfurther_on_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.further_on")).icon(() -> FIRE_CLAY.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(FIRE_CLAY);
        output.accept(BLAST_FURNACE_MAIN_ITEM);
    }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FURTHER_ON_BUILDING_TAB = CREATIVE_MODE_TABS.register("zfurther_on_building_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.further_on_building")).icon(() -> FIRE_CLAY_BRICKS_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(FIRE_CLAY_BRICKS_ITEM);
        output.accept(FIRE_CLAY_BRICKS_WALL_ITEM);
    }).build());
}
