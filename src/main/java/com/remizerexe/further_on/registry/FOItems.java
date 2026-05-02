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
            .register();
    public static final ItemEntry<Item> FIRE_CLAY_BRICK = REGISTRATE.item("fire_clay_brick", Item::new)
            .lang("Fire Clay Brick")
            .properties((p) -> p.fireResistant())
            .register();

    /* Materials */
    public static final ItemEntry<Item> CARBON_STEEL = REGISTRATE.item("carbon_steel", Item::new)
            .lang("Carbon Steel Ingot")
            .register();
    public static final ItemEntry<Item> STRUCTURAL_STEEL = REGISTRATE.item("structural_steel", Item::new)
            .lang("Structural Steel Ingot")
            .register();
    public static final ItemEntry<Item> STAINLESS_STEEL = REGISTRATE.item("stainless_steel", Item::new)
            .lang("Stainless Steel Ingot")
            .register();
    public static final ItemEntry<Item> MAGNESIUM = REGISTRATE.item("magnesium", Item::new)
            .lang("Magnesium Ingot")
            .register();
    public static final ItemEntry<Item> ALUMINIUM = REGISTRATE.item("aluminium", Item::new)
            .lang("Aluminium Ingot")
            .register();
    public static final ItemEntry<Item> ZIRCONIUM = REGISTRATE.item("zirconium", Item::new)
            .lang("Zirconium Ingot")
            .register();

    public static final ItemEntry<Item> BAUXITE_DUST = REGISTRATE.item("bauxite_dust", Item::new)
            .lang("Bauxite Dust")
            .register();
    public static final ItemEntry<Item> GRAPHITE = REGISTRATE.item("graphite", Item::new)
            .lang("Graphite")
            .register();
    public static final ItemEntry<Item> COKE = REGISTRATE.item("coke", Item::new)
            .lang("Coke")
            .burnTime(3200)
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
