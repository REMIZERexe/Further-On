package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHatchBlock;
import com.remizerexe.further_on.content.cast_beam.CastBeamBlock;
import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthBlock;
import com.remizerexe.further_on.content.oil.OilNodeBlock;
import com.remizerexe.further_on.content.pumpjack.PumpjackBaseBlock;
import com.remizerexe.further_on.content.pumpjack.PumpjackRotationJointBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.remizerexe.further_on.content.pipes.IndustrialPipeBlock;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;

import static com.remizerexe.further_on.FurtherOn.MODID;
import static com.remizerexe.further_on.FurtherOn.REGISTRATE;

public class FOBlocks {
    /*----- BLOCKS REGISTERED HERE WILL SHOW UP IN THE MAIN TAB -----*/
    static {
        REGISTRATE.setCreativeTab(FOTabs.FURTHER_ON_TAB);
    }

    public static final BlockEntry<PumpjackRotationJointBlock> PUMPJACK_ROTATION_JOINT =
            REGISTRATE.block("pumpjack_rotation_joint", PumpjackRotationJointBlock::new)
                    .lang("Pumpjack Rotation Joint")
                    .blockstate((ctx, prov) -> {})
                    .properties(c -> c.sound(SoundType.METAL))
                    .simpleItem()
                    .register();

    public static final BlockEntry<PumpjackBaseBlock> PUMPJACK_BASE =
            REGISTRATE.block("pumpjack_base", PumpjackBaseBlock::new)
                    .lang("Pumpjack Base")
                    .blockstate((ctx, prov) -> {
                        prov.getVariantBuilder(ctx.get())
                                .forAllStates(state -> {
                                    Half half = state.getValue(PumpjackBaseBlock.HALF);
                                    Direction facing = state.getValue(PumpjackBaseBlock.FACING);
                                    int yRot = (int) facing.toYRot();

                                    if (half == Half.BOTTOM) {
                                        return ConfiguredModel.builder()
                                                .modelFile(prov.models().cubeAll(ctx.getName() + "_bottom",
                                                        ResourceLocation.fromNamespaceAndPath(MODID, "block/pumpjack_base")))
                                                .rotationY(yRot)
                                                .build();
                                    } else {
                                        return ConfiguredModel.builder()
                                                .modelFile(prov.models().cubeAll(ctx.getName() + "_top",
                                                        ResourceLocation.fromNamespaceAndPath(MODID, "block/pumpjack_base")))
                                                .rotationY(yRot)
                                                .build();
                                    }
                                });
                    })
                    .properties(c -> c.sound(SoundType.METAL).noOcclusion())
                    .simpleItem()
                    .register();

    public static final BlockEntry<BlastFurnaceHearthBlock> BLAST_FURNACE_HEARTH =
            REGISTRATE.block("blast_furnace_hearth", BlastFurnaceHearthBlock::new)
                    .lang("Blast Furnace Hearth")
                    .blockstate((ctx, prov) -> {
                        ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay_bricks");
                        ResourceLocation textureLocFront = ResourceLocation.fromNamespaceAndPath(MODID, "block/blast_furnace_hearth");

                        prov.horizontalBlock(ctx.get(), prov.models()
                                .orientable("blast_furnace_hearth", textureLoc, textureLocFront, textureLoc));
                    })
                    .simpleItem()
                    .register();

    public static final BlockEntry<BlastFurnaceHatchBlock> BLAST_FURNACE_HATCH =
            REGISTRATE.block("blast_furnace_hatch", BlastFurnaceHatchBlock::new)
                    .lang("Blast Furnace Hatch")
                    .blockstate((ctx, prov) -> {
                        prov.simpleBlock(ctx.get(), prov.models().singleTexture(
                                ctx.getName(),
                                ResourceLocation.withDefaultNamespace("block/cube_all"),
                                "all",
                                ResourceLocation.fromNamespaceAndPath(MODID, "block/blast_furnace_hatch")
                        ));
                    })
                    .simpleItem()
                    .register();

    public static final BlockEntry<com.remizerexe.further_on.content.pipes.IndustrialPipeBlock> INDUSTRIAL_PIPE =
            REGISTRATE.block("industrial_pipe", com.remizerexe.further_on.content.pipes.IndustrialPipeBlock::new)
                    .lang("Industrial Pipe")
                    .blockstate((ctx, prov) -> {})
                    .properties(c -> c.sound(SoundType.METAL).noOcclusion())
                    .item()
                    .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            ResourceLocation.fromNamespaceAndPath(MODID, "block/industrialpipe_core")))
                    .build()
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
    public static final BlockEntry<StairBlock> FIRE_CLAY_BRICK_STAIRS = REGISTRATE.block("fire_clay_brick_stairs", p -> new StairBlock(FIRE_CLAY_BRICKS.getDefaultState(), p))
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

    public static final BlockEntry<Block> FIRE_CLAY = REGISTRATE.block("fire_clay", Block::new)
            .lang("Fire Clay")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay");
                ResourceLocation textureLocTop = ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay_top");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_column");

                prov.simpleBlock(ctx.get(), prov.models().cubeColumn("fire_clay", textureLoc, textureLocTop));
            })
            .properties(c -> c.sound(SoundType.GRAVEL))
            .simpleItem()
            .register();


    public static final BlockEntry<Block> GRAPHITE_ORE = REGISTRATE.block("graphite_ore", Block::new)
            .lang("Graphite Ore")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/graphite_ore");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.GRAVEL))
            .simpleItem()
            .register();
    public static final BlockEntry<Block> DEEPSLATE_GRAPHITE_ORE = REGISTRATE.block("deepslate_graphite_ore", Block::new)
            .lang("Deepslate Graphite Ore")
            .blockstate((ctx, prov) -> {
                ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(MODID, "block/deepslate_graphite_ore");
                ResourceLocation cubeAll = ResourceLocation.withDefaultNamespace("block/cube_all");

                prov.simpleBlock(ctx.get(), prov.models().singleTexture(ctx.getName(), cubeAll, "all", textureLoc));
            })
            .properties(c -> c.sound(SoundType.GRAVEL))
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

    public static final BlockEntry<OilNodeBlock> OIL_NODE_POOR =
            REGISTRATE.block("oil_node_poor", p -> new OilNodeBlock(OilNodeBlock.Richness.POOR, p))
                    .lang("Poor Oil Node")
                    .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
                            .singleTexture(ctx.getName(),
                                    ResourceLocation.withDefaultNamespace("block/cube_all"),
                                    "all", ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay"))))
                    .register();

    public static final BlockEntry<OilNodeBlock> OIL_NODE_NORMAL =
            REGISTRATE.block("oil_node_normal", p -> new OilNodeBlock(OilNodeBlock.Richness.NORMAL, p))
                    .lang("Oil Node")
                    .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
                            .singleTexture(ctx.getName(),
                                    ResourceLocation.withDefaultNamespace("block/cube_all"),
                                    "all", ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay"))))
                    .register();

    public static final BlockEntry<OilNodeBlock> OIL_NODE_RICH =
            REGISTRATE.block("oil_node_rich", p -> new OilNodeBlock(OilNodeBlock.Richness.RICH, p))
                    .lang("Rich Oil Node")
                    .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models()
                            .singleTexture(ctx.getName(),
                                    ResourceLocation.withDefaultNamespace("block/cube_all"),
                                    "all", ResourceLocation.fromNamespaceAndPath(MODID, "block/fire_clay"))))
                    .register();

    public static void register() { }
}
