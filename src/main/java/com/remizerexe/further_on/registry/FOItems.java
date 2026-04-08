package com.remizerexe.further_on.registry;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import static com.remizerexe.further_on.FurtherOn.REGISTRATE;

public class FOItems {
    /*----- ITEMS REGISTERED HERE WILL SHOW UP IN THE MAIN TAB -----*/
    static {
        REGISTRATE.setCreativeTab(FOTabs.FURTHER_ON_TAB);
    }


    public static final ItemEntry<Item> FIRE_CLAY = REGISTRATE.item("fire_clay", Item::new)
            .lang("Fire Clay")
            .properties((p) -> p.fireResistant())
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), ResourceLocation.parse("clay_ball")))
            .register();


    /*----- ITEMS REGISTERED HERE WILL SHOW UP IN THE BUILDING TAB -----*/
    static {
        REGISTRATE.setCreativeTab(FOTabs.FURTHER_ON_BUILDING_TAB);
    }



    /*----- ITEMS REGISTERED HERE WILL NOT SHOW UP IN ANY TAB -----*/
    static {
        REGISTRATE.setCreativeTab(null);
    }

    public static void register() { }
}
