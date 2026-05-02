package com.remizerexe.further_on.multiblock;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;

@FunctionalInterface
public interface MultiblockPredicate {

    boolean test(BlockGetter level, BlockPos pos, BlockState state);

    static MultiblockPredicate of(Block block){
        return (level, pos, state) -> state.is(block);
    }
    static MultiblockPredicate of(TagKey<Block> tag) {
        return (level, pos, state) -> state.is(tag);
    }
    static MultiblockPredicate anyOf(Block... blocks){
        return ((level, pos, state) -> {
            for (Block block : blocks){
                if (state.is(block)) return true;
            }
            return false;
        });
    }
    static MultiblockPredicate any() {
        return (level, pos, state) -> true;
    }

    /**
     * Matches a block and optionally checks specific blockstate properties.
     * properties map can be empty to match any state of the block.
     */
    static MultiblockPredicate ofWithState(Block block, Map<String, String> properties) {
        if (properties.isEmpty()) return of(block);
        return (level, pos, state) -> {
            if (!state.is(block)) return false;
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                // Find the property by name and check its value
                for (net.minecraft.world.level.block.state.properties.Property<?> prop
                        : state.getProperties()) {
                    if (prop.getName().equals(entry.getKey())) {
                        String actual = state.getValue(prop).toString().toLowerCase();
                        if (!actual.equals(entry.getValue().toLowerCase())) return false;
                    }
                }
            }
            return true;
        };
    }
}
