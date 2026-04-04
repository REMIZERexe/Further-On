package com.remizerexe.further_on.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FurtherOnItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<Item> FIRE_CLAY = ITEMS.registerSimpleItem("fire_clay", new Item.Properties().fireResistant());
}
