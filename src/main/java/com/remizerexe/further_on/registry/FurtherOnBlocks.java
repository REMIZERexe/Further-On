package com.remizerexe.further_on.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.remizerexe.further_on.FurtherOn.MODID;
import static com.remizerexe.further_on.registry.FurtherOnItems.ITEMS;

public class FurtherOnBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredBlock<Block> FIRE_CLAY_BRICKS = BLOCKS.registerSimpleBlock("fire_clay_bricks", BlockBehaviour.Properties.of());
    public static final DeferredBlock<Block> FIRE_CLAY_BRICKS_WALL = BLOCKS.registerSimpleBlock("fire_clay_bricks_wall", BlockBehaviour.Properties.of());
    public static final DeferredBlock<Block> BLAST_FURNACE_MAIN = BLOCKS.registerSimpleBlock("blast_furnace_hearth", BlockBehaviour.Properties.of());

    public static final DeferredItem<BlockItem> BLAST_FURNACE_MAIN_ITEM = ITEMS.registerSimpleBlockItem("blast_furnace_hearth", BLAST_FURNACE_MAIN);
    public static final DeferredItem<BlockItem> FIRE_CLAY_BRICKS_ITEM = ITEMS.registerSimpleBlockItem("fire_clay_bricks", FIRE_CLAY_BRICKS);
    public static final DeferredItem<BlockItem> FIRE_CLAY_BRICKS_WALL_ITEM = ITEMS.registerSimpleBlockItem("fire_clay_bricks_wall", FIRE_CLAY_BRICKS_WALL);
}
