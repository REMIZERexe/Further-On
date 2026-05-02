package com.remizerexe.further_on.multiblock;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

/**
 * Parsed representation of a multiblock JSON file.
 * Call buildStructure(capacityLayers) to get a validatable MultiblockStructure.
 */
public class JsonMultiblockDefinition {

    private final int minCapacityLayers;
    private final int maxCapacityLayers;
    private final Map<Character, MultiblockPredicate> legend;
    private final String[] baseLayer;
    private final String[] controllerLayer;
    private final String[] collarLayer;
    private final String[] capacityLayer;
    private final String[] topLayer;

    public JsonMultiblockDefinition(
            int minCapacityLayers, int maxCapacityLayers,
            Map<Character, MultiblockPredicate> legend,
            String[] baseLayer, String[] controllerLayer,
            String[] collarLayer, String[] capacityLayer,
            String[] topLayer
    ) {
        this.minCapacityLayers = minCapacityLayers;
        this.maxCapacityLayers = maxCapacityLayers;
        this.legend = legend;
        this.baseLayer = baseLayer;
        this.controllerLayer = controllerLayer;
        this.collarLayer = collarLayer;
        this.capacityLayer = capacityLayer;
        this.topLayer = topLayer;
    }

    public int getMinCapacityLayers() { return minCapacityLayers; }
    public int getMaxCapacityLayers() { return maxCapacityLayers; }

    /**
     * Builds a full MultiblockStructure for the given number of capacity layers.
     * The controller character C in the JSON marks the origin (0, 0, 0).
     */
    public MultiblockStructure buildStructure(int capacityLayers) {
        Map<BlockPos, MultiblockPredicate> pattern = new HashMap<>();

        // Find C in controller layer
        int originRow = 0, originCol = 0;
        outer:
        for (int row = 0; row < controllerLayer.length; row++) {
            for (int col = 0; col < controllerLayer[row].length(); col++) {
                if (controllerLayer[row].charAt(col) == 'C') {
                    originRow = row;
                    originCol = col;
                    break outer;
                }
            }
        }
        // Layer -1: base
        MultiblockJsonLoader.addLayerToPattern(
                baseLayer, -1, legend, pattern, originRow, originCol
        );

        // Layer 0: controller layer (y=0 is the controller itself)
        MultiblockJsonLoader.addLayerToPattern(
                controllerLayer, 0, legend, pattern, originRow, originCol
        );

        // Collar layer — always exactly one, sits right above controller
        MultiblockJsonLoader.addLayerToPattern(
                collarLayer, 1, legend, pattern, originRow, originCol
        );

        // Capacity layers: y = 2 to capacityLayers
        for (int i = 0; i < capacityLayers; i++) {
            MultiblockJsonLoader.addLayerToPattern(
                    capacityLayer, 2 + i, legend, pattern, originRow, originCol
            );
        }

        // Top layer
        MultiblockJsonLoader.addLayerToPattern(
                topLayer, 2 + capacityLayers, legend, pattern, originRow, originCol
        );

        return new MultiblockStructure(pattern);
    }
}