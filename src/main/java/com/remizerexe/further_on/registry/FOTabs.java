package com.remizerexe.further_on.registry;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.remizerexe.further_on.FurtherOn.REGISTRATE;
import static com.remizerexe.further_on.registry.FOBlocks.FIRE_CLAY_BRICKS;
import static com.remizerexe.further_on.registry.FORegistries.CREATIVE_MODE_TABS;
import static com.remizerexe.further_on.registry.FOItems.FIRE_CLAY;

public class FOTabs {
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FURTHER_ON_TAB = CREATIVE_MODE_TABS.register("further_on_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.further_on"))
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(FIRE_CLAY::asStack)
            .displayItems((parameters, output) -> {
                for (RegistryEntry<Block, Block> entry : REGISTRATE.getAll(Registries.BLOCK)) {
                    if (!CreateRegistrate.isInCreativeTab(entry, FOTabs.FURTHER_ON_TAB))
                        continue;
                    if (entry.get() instanceof LiquidBlock)
                        continue;

                    output.accept(entry.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
                for (RegistryEntry<Item, Item> entry : REGISTRATE.getAll(Registries.ITEM)) {
                    if (!CreateRegistrate.isInCreativeTab(entry, FOTabs.FURTHER_ON_TAB))
                        continue;
                    output.accept(entry.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FURTHER_ON_BUILDING_TAB = CREATIVE_MODE_TABS.register("further_on_building_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.further_on_building"))
            .withTabsBefore(FURTHER_ON_TAB.getKey())
            .icon(FIRE_CLAY_BRICKS::asStack)
            .displayItems((parameters, output) -> {
                for (RegistryEntry<Block, Block> entry : REGISTRATE.getAll(Registries.BLOCK)) {
                    if (!CreateRegistrate.isInCreativeTab(entry, FOTabs.FURTHER_ON_BUILDING_TAB))
                        continue;
                    if (entry.get() instanceof LiquidBlock)
                        continue;

                    output.accept(entry.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
                for (RegistryEntry<Item, Item> entry : REGISTRATE.getAll(Registries.ITEM)) {
                    if (!CreateRegistrate.isInCreativeTab(entry, FOTabs.FURTHER_ON_BUILDING_TAB))
                        continue;
                    output.accept(entry.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }).build());

    public static void register() { }
}
