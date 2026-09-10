package com.remizerexe.further_on.multiblock;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultiblockJsonLoader {

    private static final Gson GSON = new Gson();

    /**
     * Loads a multiblock structure definition from:
     * assets/further_on/multiblocks/<name>.json
     *
     * Returns a JsonMultiblockDefinition which the controller BE uses
     * to dynamically build a MultiblockStructure at validation time.
     */
    public static JsonMultiblockDefinition load(String modid, String name) {
        String path = "/assets/" + modid + "/multiblocks/" + name + ".json";
        try (InputStream is = MultiblockJsonLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException("Multiblock JSON not found: " + path);
            }
            JsonObject root = GSON.fromJson(
                    new InputStreamReader(is, StandardCharsets.UTF_8),
                    JsonObject.class
            );
            return parse(root);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load multiblock JSON: " + path, e);
        }
    }

    // -------------------------------------------------------------------------
    // Parsing
    // -------------------------------------------------------------------------

    public static JsonMultiblockDefinition parse(JsonObject root) {
        int minLayers = root.has("min_capacity_layers")
                ? root.get("min_capacity_layers").getAsInt() : 1;
        int maxLayers = root.has("max_capacity_layers")
                ? root.get("max_capacity_layers").getAsInt() : 20;

        // Parse legend: char -> predicate
        Map<Character, MultiblockPredicate> legend = parseLegend(
                root.getAsJsonObject("legend")
        );

        // Parse named layers
        JsonObject layersJson = root.getAsJsonObject("layers");
        String[][] baseLayers    = parseBaseLayers(layersJson);
        String[] controllerLayer = parseLayer(layersJson, "controller");
        String[] collarLayer = parseLayer(layersJson, "collar");
        String[] capacityLayer   = parseLayer(layersJson, "capacity");
        String[] topLayer        = parseLayer(layersJson, "top");

        return new JsonMultiblockDefinition(
                minLayers, maxLayers,
                legend,
                baseLayers, controllerLayer,
                collarLayer,
                capacityLayer, topLayer
        );
    }

    private static Map<Character, MultiblockPredicate> parseLegend(JsonObject legendJson) {
        Map<Character, MultiblockPredicate> legend = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : legendJson.entrySet()) {
            char key = entry.getKey().charAt(0);
            JsonObject blockDef = entry.getValue().getAsJsonObject();

            String blockId = blockDef.get("block").getAsString();
            if (blockId.equals("*")) {
                legend.put(key, MultiblockPredicate.any());
                continue;
            }
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
            if (block == null || block == Blocks.AIR && !blockId.equals("minecraft:air")) {
                throw new IllegalStateException("Unknown block in multiblock legend: " + blockId);
            }

            Map<String, String> stateProps = new HashMap<>();
            if (blockDef.has("state")) {
                JsonObject stateJson = blockDef.getAsJsonObject("state");
                for (Map.Entry<String, JsonElement> prop : stateJson.entrySet()) {
                    stateProps.put(prop.getKey(), prop.getValue().getAsString());
                }
            }

            legend.put(key, MultiblockPredicate.ofWithState(block, stateProps));
        }
        return legend;
    }

    /**
     * "base" is either one layer ({@code ["...", "..."]}) or several listed
     * bottom-up ({@code [["..."], ["..."]]}), like the rest of the file; the
     * last listed sits directly under the controller.
     */
    private static String[][] parseBaseLayers(JsonObject layersJson) {
        if (!layersJson.has("base")) return new String[0][];
        JsonArray base = layersJson.getAsJsonArray("base");
        if (base.isEmpty()) return new String[0][];
        if (base.get(0).isJsonArray()) return GSON.fromJson(base, String[][].class);
        return new String[][] { GSON.fromJson(base, String[].class) };
    }

    private static String[] parseLayer(JsonObject layersJson, String key) {
        if (!layersJson.has(key)) return new String[0];
        return GSON.fromJson(layersJson.get(key), String[].class);
    }

    // -------------------------------------------------------------------------
    // Pattern building from a layer definition
    // -------------------------------------------------------------------------

    /**
     * Converts a string-array layer into BlockPos -> predicate entries.
     * The controller position (C) is always treated as the origin (0, y, 0).
     * Rows run along Z, columns along X.
     */
    static void addLayerToPattern(
            String[] rows,
            int y,
            Map<Character, MultiblockPredicate> legend,
            Map<BlockPos, MultiblockPredicate> pattern,
            int originRow,
            int originCol
    ) {
        for (int row = 0; row < rows.length; row++) {
            String line = rows[row];
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                if (c == 'C') continue;
                MultiblockPredicate predicate = legend.get(c);
                if (predicate == null) continue;

                int right   = col - originCol; // X: columns relative to C
                int forward = row - originRow; // Z: rows relative to C
                pattern.put(new BlockPos(right, y, forward), predicate);
            }
        }
    }
}