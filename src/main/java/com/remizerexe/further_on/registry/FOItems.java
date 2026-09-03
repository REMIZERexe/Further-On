package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.content.equipment.WeldingMaskItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.remizerexe.further_on.FurtherOn.REGISTRATE;

public class FOItems {
    /*----- ITEMS REGISTERED HERE WILL SHOW UP IN THE MAIN TAB -----*/
    static {
        REGISTRATE.setCreativeTab(FOTabs.FURTHER_ON_TAB);
    }

    public static final com.tterrag.registrate.util.entry.ItemEntry<Item> LIMESTONE_DUST = REGISTRATE.item("limestone_dust", Item::new)
            .lang("Limestone Dust")
            .register();

    public static final com.tterrag.registrate.util.entry.ItemEntry<Item> QUICKLIME = REGISTRATE.item("quicklime", Item::new)
            .lang("Quicklime")
            .register();

    public static final ItemEntry<Item> FIRE_CLAY_BALL = REGISTRATE.item("fire_clay_ball", Item::new)
            .lang("Fire Clay Ball")
            .properties((p) -> p.fireResistant())
            .register();

    public static final ItemEntry<Item> FIRE_CLAY_BRICK = REGISTRATE.item("fire_clay_brick", Item::new)
            .lang("Fire Clay Brick")
            .properties((p) -> p.fireResistant())
            .register();

    /* Materials */
    public static final ItemEntry<Item> CARBON_STEEL = REGISTRATE.item("carbon_steel", Item::new)
            .lang("Carbon Steel Ingot")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/carbon_steel")), net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/steel")))
            .register();
    public static final ItemEntry<Item> STRUCTURAL_STEEL = REGISTRATE.item("structural_steel", Item::new)
            .lang("Structural Steel Ingot")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/structural_steel")), net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/steel")))
            .register();
    public static final ItemEntry<Item> STAINLESS_STEEL = REGISTRATE.item("stainless_steel", Item::new)
            .lang("Stainless Steel Ingot")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/stainless_steel")), net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/steel")))
            .register();
    public static final ItemEntry<Item> MAGNESIUM = REGISTRATE.item("magnesium", Item::new)
            .lang("Magnesium Ingot")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/magnesium")))
            .register();
    public static final ItemEntry<Item> ALUMINIUM = REGISTRATE.item("aluminium", Item::new)
            .lang("Aluminium Ingot")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/aluminum")))
            .register();
    public static final ItemEntry<Item> ZIRCONIUM = REGISTRATE.item("zirconium", Item::new)
            .lang("Zirconium Ingot")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/zirconium")))
            .register();

    /* Equipment */
    public static final ItemEntry<WeldingMaskItem> WELDING_MASK = REGISTRATE.item("welding_mask", WeldingMaskItem::new)
            .lang("Welding Mask")
            .properties((p) -> p.stacksTo(1))
            .model(NonNullBiConsumer.noop())
            .register();

    public static final ItemEntry<Item> BAUXITE_DUST = REGISTRATE.item("bauxite_dust", Item::new)
            .lang("Bauxite Dust")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts/bauxite")))
            .register();
    public static final ItemEntry<Item> GRAPHITE = REGISTRATE.item("graphite", Item::new)
            .lang("Graphite")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/graphite")))
            .register();
    public static final ItemEntry<Item> COKE = REGISTRATE.item("coke", Item::new)
            .lang("Coke")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "gems/coal_coke")))
            .burnTime(3200)
            .register();

    public static final ItemEntry<Item> slag = REGISTRATE.item("slag", Item::new)
            .lang("Slag")
            .register();

    // TODO: dumping these here bc idk where else to put them rn

    public static final ItemEntry<Item> welding_torch_item = REGISTRATE.item("welding_torch", Item::new)
            .lang("Welding Torch")
            // FIXME: bruh how do i add durability to this
            .register();

    public static final ItemEntry<com.remizerexe.further_on.content.build_gun.BuildGunItem> build_gun = REGISTRATE.item("build_gun", com.remizerexe.further_on.content.build_gun.BuildGunItem::new)
            .lang("Architect's Build Gun")
            .properties(p -> p.stacksTo(1)) // Handheld schematic cannon upgrade!
            .register();

    public static final ItemEntry<com.remizerexe.further_on.content.equipment.HazardBootsItem> hazard_boots = REGISTRATE.item("hazard_boots", com.remizerexe.further_on.content.equipment.HazardBootsItem::new)
            .lang("Rubber Hazard Boots")
            .properties(p -> p.stacksTo(1)) // insulated boots!
            .register();

    public static final ItemEntry<Item> niko_pancakes = REGISTRATE.item("pancakes", Item::new)
            .lang("Pancakes")
            // .food(...) -> neoforge 1.21 food properties are confusing rn
            .register();

    public static final ItemEntry<Item> spruce_resin_item = REGISTRATE.item("spruce_resin", Item::new)
            .lang("Spruce Resin")
            .register();
            
    public static final ItemEntry<Item> maple_syrup = REGISTRATE.item("syrup", Item::new)
            .lang("Syrup") 
            .register();

    public static final ItemEntry<Item> turpentine = REGISTRATE.item("turpentine", Item::new)
            .lang("Turpentine") 
            .register();

    public static final ItemEntry<Item> latex = REGISTRATE.item("latex", Item::new)
            .lang("Latex") 
            .register();

    public static final ItemEntry<Item> biofuel_pellet = REGISTRATE.item("biofuel_pellet", Item::new)
            .lang("Biofuel Pellet")
            .burnTime(1600) // same as coal for now
            .register();

    public static final ItemEntry<Item> coal_tar = REGISTRATE.item("coal_tar", Item::new)
            .lang("Coal Tar") 
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "slimeballs"))) // often interchangeable
            .register();

    public static final ItemEntry<Item> feldspar = REGISTRATE.item("feldspar", Item::new)
            .lang("Feldspar") 
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/aluminum")), net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ores/bauxite")))
            .register();

    public static final ItemEntry<Item> duralumin_ingot = REGISTRATE.item("duralumin", Item::new)
            .lang("Duralumin Ingot") 
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/duralumin")))
            .register();

    public static final ItemEntry<Item> tungsten_ingot = REGISTRATE.item("tungsten", Item::new)
            .lang("Tungsten Ingot") 
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/tungsten")))
            .register();

    // Electrical / Power Generation components
    public static final ItemEntry<Item> copper_coil = REGISTRATE.item("copper_coil", Item::new)
            .lang("Copper Coil")
            .register();

    public static final ItemEntry<Item> permanent_magnet = REGISTRATE.item("permanent_magnet", Item::new)
            .lang("Permanent Magnet")
            .register();

    public static final ItemEntry<Item> stator_core = REGISTRATE.item("stator_core", Item::new)
            .lang("Stator Core")
            .register();

    public static final ItemEntry<Item> rotor_core = REGISTRATE.item("rotor_core", Item::new)
            .lang("Rotor Core")
            .register();

    public static final ItemEntry<Item> copper_wire = REGISTRATE.item("copper_wire", Item::new)
            .lang("Copper Wire")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "wires/copper")))
            .register();

    public static final ItemEntry<Item> gold_wire = REGISTRATE.item("gold_wire", Item::new)
            .lang("Gold Wire")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "wires/gold")))
            .register();

    public static final ItemEntry<Item> silver_wire = REGISTRATE.item("silver_wire", Item::new)
            .lang("Silver Wire")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "wires/silver")))
            .register();

    // Electronics & Logic
    public static final ItemEntry<Item> silicon_boule = REGISTRATE.item("silicon_boule", Item::new)
            .lang("Silicon Boule")
            .register();

    public static final ItemEntry<Item> silicon_wafer = REGISTRATE.item("silicon_wafer", Item::new)
            .lang("Silicon Wafer")
            .register();

    public static final ItemEntry<Item> polished_silicon_wafer = REGISTRATE.item("polished_silicon_wafer", Item::new)
            .lang("Polished Silicon Wafer")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "silicon"))) // ae2/rs compat
            .register();

    public static final ItemEntry<Item> capacitor = REGISTRATE.item("capacitor", Item::new)
            .lang("Capacitor")
            .register();

    public static final ItemEntry<Item> transistor = REGISTRATE.item("transistor", Item::new)
            .lang("Transistor")
            .register();

    public static final ItemEntry<Item> printed_circuit_board = REGISTRATE.item("printed_circuit_board", Item::new)
            .lang("Printed Circuit Board")
            .register();

    // Primitive steel processing
    public static final ItemEntry<Item> spongy_iron = REGISTRATE.item("spongy_iron", Item::new)
            .lang("Spongy Iron Bloom")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/steel")))
            .register();

    // Minecraft-Themed Endgame
    public static final ItemEntry<Item> bedrock_alloy_ingot = REGISTRATE.item("bedrock_alloy", Item::new)
            .lang("Bedrock Alloy Ingot")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/bedrock_alloy")))
            .register();

    // Complex Ore Processing Intermediates
    public static final ItemEntry<Item> alumina_dust = REGISTRATE.item("alumina_dust", Item::new)
            .lang("Alumina Dust")
            .register(); // Bayer process intermediate

    public static final ItemEntry<Item> raw_graphite = REGISTRATE.item("raw_graphite", Item::new)
            .lang("Raw Graphite")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/graphite")))
            .register();

    public static final ItemEntry<Item> graphite_dust = REGISTRATE.item("graphite_dust", Item::new)
            .lang("Graphite Dust")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts/graphite")))
            .register();

    // Semiconductor / Silicon Intermediates
    public static final ItemEntry<Item> raw_silicon = REGISTRATE.item("raw_silicon", Item::new)
            .lang("Metallurgical Silicon")
            .register(); // Carbothermic reduction of silica

    public static final ItemEntry<Item> purified_silicon = REGISTRATE.item("purified_silicon", Item::new)
            .lang("Polycrystalline Silicon")
            .register(); // Siemens process purification

    // Nuclear & Thermal components
    public static final ItemEntry<Item> RAW_URANIUM = REGISTRATE.item("raw_uranium", Item::new)
            .lang("Raw Uranium")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/uranium")))
            .register();

    public static final ItemEntry<Item> crushed_raw_uranium = REGISTRATE.item("crushed_raw_uranium", Item::new)
            .lang("Crushed Raw Uranium")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "crushed_ores/uranium")))
            .register();

    public static final ItemEntry<Item> yellowcake = REGISTRATE.item("yellowcake", Item::new)
            .lang("Yellowcake (U3O8)")
            .register(); // Uranium ore milling

    public static final ItemEntry<Item> fluorite = REGISTRATE.item("fluorite", Item::new)
            .lang("Fluorite")
            .register(); // Fluorine source for UF6

    public static final ItemEntry<Item> uranium_hexafluoride = REGISTRATE.item("uranium_hexafluoride", Item::new)
            .lang("Uranium Hexafluoride")
            .register(); // UF6 conversion process

    public static final ItemEntry<Item> enriched_uranium_pellet = REGISTRATE.item("enriched_uranium_pellet", Item::new)
            .lang("Enriched Uranium Pellet")
            .register(); // Gas centrifuge enrichment output

    public static final ItemEntry<Item> URANIUM = REGISTRATE.item("uranium", Item::new)
            .lang("Uranium Ingot")
            .tag(net.minecraft.tags.ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/uranium")))
            .register();

    public static final ItemEntry<Item> uranium_rod = REGISTRATE.item("uranium_rod", Item::new)
            .lang("Uranium Rod")
            .register();

    public static final ItemEntry<Item> control_rod = REGISTRATE.item("control_rod", Item::new)
            .lang("Control Rod")
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
