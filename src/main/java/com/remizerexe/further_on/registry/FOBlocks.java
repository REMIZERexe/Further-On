package com.remizerexe.further_on.registry;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;

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


    /*----- BLOCKS REGISTERED HERE WILL NOT SHOW UP IN ANY TAB -----*/
    static {
        REGISTRATE.setCreativeTab(null);
    }


    public static void register() { }
}
