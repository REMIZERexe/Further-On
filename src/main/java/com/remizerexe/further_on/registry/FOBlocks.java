package com.remizerexe.further_on.registry;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.remizerexe.further_on.FurtherOn.MODID;
import static com.remizerexe.further_on.FurtherOn.REGISTRATE;

public class FOBlocks {
    /*----- BLOCKS REGISTERED HERE WILL SHOW UP IN THE MAIN TAB -----*/
    static {
        REGISTRATE.setCreativeTab(FOTabs.FURTHER_ON_TAB);
    }


    public static final BlockEntry<Block> BLAST_FURNACE_HEARTH = REGISTRATE.block("blast_furnace_hearth", Block::new)
            .lang("Blast Furnace Hearth")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.parse("block/bricks");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .simpleItem()
            .register();


    /*----- BLOCKS REGISTERED HERE WILL SHOW UP IN THE BUILDING TAB -----*/
    static {
        REGISTRATE.setCreativeTab(FOTabs.FURTHER_ON_BUILDING_TAB);
    }


    public static final BlockEntry<Block> FIRE_CLAY_BRICKS = REGISTRATE.block("fire_clay_bricks", Block::new)
            .lang("Fire Clay Bricks")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.parse("block/clay");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .simpleItem()
            .register();

    public static final BlockEntry<WallBlock> FIRE_CLAY_BRICKS_WALL = REGISTRATE.block("fire_clay_bricks_wall", WallBlock::new)
            .lang("Fire Clay Bricks Wall")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.parse("block/bricks");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .simpleItem()
            .register();


    /* Blocks of materials */
    public static final BlockEntry<Block> CARBON_STEEL_BLOCK = REGISTRATE.block("carbon_steel_block", Block::new)
            .lang("Carbon Steel Block")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/carbon_steel_block");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.NETHERITE_BLOCK))
            .simpleItem()
            .register();

    public static final BlockEntry<Block> STRUCTURAL_STEEL_BLOCK = REGISTRATE.block("structural_steel_block", Block::new)
            .lang("Structural Steel Block")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/structural_steel_block");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.NETHERITE_BLOCK))
            .simpleItem()
            .register();
    public static final BlockEntry<Block> STAINLESS_STEEL_BLOCK = REGISTRATE.block("stainless_steel_block", Block::new)
            .lang("Stainless Steel Block")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/stainless_steel_block");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.NETHERITE_BLOCK))
            .simpleItem()
            .register();
    public static final BlockEntry<Block> MAGNESIUM_BLOCK = REGISTRATE.block("magnesium_block", Block::new)
            .lang("Magnesium Block")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/magnesium_block");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.METAL))
            .simpleItem()
            .register();
    public static final BlockEntry<Block> ALUMINIUM_BLOCK = REGISTRATE.block("aluminium_block", Block::new)
            .lang("Aluminium Block")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/aluminium_block");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.METAL))
            .simpleItem()
            .register();

    /* Resources and ores */
    public static final BlockEntry<Block> BAUXITE = REGISTRATE.block("bauxite", Block::new)
            .lang("Bauxite")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/bauxite");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.DEEPSLATE))
            .simpleItem()
            .register();


    /*----- BLOCKS REGISTERED HERE WILL NOT SHOW UP IN ANY TAB -----*/
    static {
        REGISTRATE.setCreativeTab(null);
    }


    public static void register() { }
}
