package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.content.cast_beam.CastBeamBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

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
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/blast_furnace_hearth");
                ResourceLocation textureLoc1 = ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay_bricks");
                ResourceLocation orientable = ResourceLocation.withDefaultNamespace("block/orientable");

                prov.simpleBlock(ctx.get(), prov.models().orientable(ctx.getName(), textureLoc1, textureLoc, textureLoc1));
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
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay_bricks");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.STONE))
            .simpleItem()
            .register();
    public static final BlockEntry<WallBlock> FIRE_CLAY_BRICK_WALL = REGISTRATE.block("fire_clay_brick_wall", WallBlock::new)
            .lang("Fire Clay Brick Wall")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay_bricks");

                prov.wallBlock(ctx.get(), prov.models().wallPost("fire_clay_brick_wall_post", textureLoc),
                        prov.models().wallSide("fire_clay_brick_wall_side", textureLoc),
                        prov.models().wallSideTall("fire_clay_brick_wall_side_tall", textureLoc));
            })
            .properties(c -> c.sound(SoundType.STONE))
            .tag(BlockTags.WALLS)
            .simpleItem()
            .register();
    public static final BlockEntry<StairBlock> FIRE_CLAY_BRICK_STAIRS = REGISTRATE.block("fire_clay_brick_stairs", p -> new StairBlock(  FIRE_CLAY_BRICKS.getDefaultState(), p  ))
            .lang("Fire Clay Brick Stairs")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay_bricks");

                prov.stairsBlock((StairBlock) ctx.get(), prov.models().stairs("fire_clay_brick_stairs", textureLoc, textureLoc, textureLoc),
                        prov.models().stairsInner("fire_clay_brick_stairs_inner", textureLoc, textureLoc, textureLoc),
                        prov.models().stairsOuter("fire_clay_brick_stairs_outer", textureLoc, textureLoc, textureLoc));
            })
            .properties(c -> c.sound(SoundType.STONE))
            .tag(BlockTags.STAIRS)
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
    public static final BlockEntry<Block> COKE_BLOCK = REGISTRATE.block("coke_block", Block::new)
            .lang("Coke Block")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/coke_block");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.DEEPSLATE))
            .simpleItem()
            .register();
    public static final BlockEntry<Block> ZIRCONIUM_BLOCK = REGISTRATE.block("zirconium_block", Block::new)
            .lang("Zirconium Block")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/zirconium_block");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.NETHERITE_BLOCK))
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

    public static final BlockEntry<CastBeamBlock> CAST_BEAM = REGISTRATE.block("cast_beam", CastBeamBlock::new)
            .lang("Cast Beam")
            .blockstate(CastBeamBlock::blockstate)
            .properties(c -> c)
            .register();


    public static void register() { }
}
