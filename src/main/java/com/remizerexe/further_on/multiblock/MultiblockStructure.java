package com.remizerexe.further_on.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds a multiblock structure pattern and validates it against the world.
 *
 * The pattern is a map of relative BlockPos offsets to MultiblockPredicates.
 * Offsets are defined in a facing-relative coordinate system:
 *   +X = right of the controller (when looking from behind)
 *   +Y = up
 *   +Z = in front of the controller (the direction it faces)
 *
 * The pattern is always authored assuming the controller faces SOUTH.
 * toWorldPos() translates offsets to absolute world coordinates for any facing.
 *
 * Instances are built by JsonMultiblockDefinition.buildStructure().
 */
public class MultiblockStructure {

    private final Map<BlockPos, MultiblockPredicate> pattern;

    public MultiblockStructure(Map<BlockPos, MultiblockPredicate> pattern) {
        this.pattern = pattern;
    }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    /**
     * Checks every position in the pattern against the world.
     * Returns true only if all predicates pass.
     */
    public boolean validate(Level level, BlockPos controllerPos, Direction facing) {
        for (Map.Entry<BlockPos, MultiblockPredicate> entry : pattern.entrySet()) {
            BlockPos worldPos = toWorldPos(controllerPos, entry.getKey(), facing);
            BlockState state  = level.getBlockState(worldPos);
            if (!entry.getValue().test(level, worldPos, state)) {
                return false;
            }
        }
        return true;
    }

    // -------------------------------------------------------------------------
    // World position conversion
    // -------------------------------------------------------------------------

    /**
     * Converts a facing-relative offset to an absolute world position.
     *
     * Coordinate mapping per facing (pattern authored for SOUTH):
     *   SOUTH: +X=east,  +Z=south  (identity)
     *   NORTH: +X=west,  +Z=north  (180°)
     *   EAST:  +X=south, +Z=east   (90° CW)
     *   WEST:  +X=north, +Z=west   (90° CCW)
     */
    private BlockPos toWorldPos(BlockPos origin, BlockPos offset, Direction facing) {
        int right   = offset.getX();
        int up      = offset.getY();
        int forward = offset.getZ();

        return switch (facing) {
            case SOUTH -> origin.offset( right,  up,  forward);
            case NORTH -> origin.offset(-right,  up, -forward);
            case EAST  -> origin.offset( forward, up, -right);
            case WEST  -> origin.offset(-forward, up,  right);
            default    -> origin.offset( right,  up,  forward);
        };
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * Returns the list of absolute world positions for all blocks in the pattern.
     * Used by the controller BE to store formed positions for client-side effects.
     */
    public List<BlockPos> getWorldPositions(BlockPos controllerPos, Direction facing) {
        List<BlockPos> positions = new ArrayList<>();
        for (BlockPos offset : pattern.keySet()) {
            positions.add(toWorldPos(controllerPos, offset, facing));
        }
        return positions;
    }
}