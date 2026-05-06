package com.remizerexe.further_on.multiblock;

import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Map;

@FunctionalInterface
public interface MultiblockPredicate {

    boolean test(BlockGetter level, BlockPos pos, BlockState state);

    default String describe() { return "unknown"; }

    static MultiblockPredicate of(Block block) {
        return new MultiblockPredicate() {
            @Override public boolean test(BlockGetter level, BlockPos pos, BlockState state) {
                return state.is(block);
            }
            @Override public String describe() {
                return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).toString();
            }
        };
    }

    static MultiblockPredicate ofWithState(Block block, Map<String, String> properties) {
        if (properties.isEmpty()) return of(block);
        return new MultiblockPredicate() {
            @Override
            public boolean test(BlockGetter level, BlockPos pos, BlockState state) {
                if (!state.is(block)) return false;
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    for (var prop : state.getProperties()) {
                        if (prop.getName().equals(entry.getKey())) {
                            String actual = state.getValue(prop).toString().toLowerCase();
                            if (!actual.equals(entry.getValue().toLowerCase())) return false;
                        }
                    }
                }
                return true;
            }

            @Override
            public MultiblockPredicate withFacing(Direction controllerFacing) {
                if (!properties.containsKey("facing")) return this;
                // Rotate the facing property relative to controller facing
                Direction jsonFacing = Direction.byName(properties.get("facing"));
                if (jsonFacing == null) return this;
                Direction rotated = rotateFacing(jsonFacing, controllerFacing);
                Map<String, String> rotatedProps = new java.util.HashMap<>(properties);
                rotatedProps.put("facing", rotated.getSerializedName());
                return ofWithState(block, rotatedProps);
            }

            @Override public String describe() {
                return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block) + " " + properties;
            }
        };
    }

    default MultiblockPredicate withFacing(Direction controllerFacing) { return this; }

    static Direction rotateFacing(Direction jsonFacing, Direction controllerFacing) {
        // JSON is authored for SOUTH controller
        // Rotate jsonFacing by the same rotation that maps SOUTH to controllerFacing
        return switch (controllerFacing) {
            case SOUTH -> jsonFacing.getOpposite();
            case NORTH -> jsonFacing;
            case EAST  -> jsonFacing.getClockWise();
            case WEST  -> jsonFacing.getCounterClockWise();
            default    -> jsonFacing;
        };
    }

    // garde anyOf et any() existants
}
