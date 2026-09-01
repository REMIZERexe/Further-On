package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOCrushingRecipeGen extends CrushingRecipeGen {
    public GeneratedRecipe BAUXITE = create(com.remizerexe.further_on.registry.FOBlocks.BAUXITE, b -> b
            .duration(250)
            .output(com.remizerexe.further_on.registry.FOItems.BAUXITE_DUST, 2)
            .output(1.0f, com.remizerexe.further_on.registry.FOItems.BAUXITE_DUST, 1)
            .output(0.5f, net.minecraft.world.item.Items.CLAY_BALL, 1)
    );

    public GeneratedRecipe FELDSPAR = create(com.remizerexe.further_on.registry.FOItems.feldspar, b -> b
            .duration(250)
            .output(com.remizerexe.further_on.registry.FOItems.BAUXITE_DUST, 1)
            .output(0.5f, com.remizerexe.further_on.registry.FOItems.BAUXITE_DUST, 1)
            .output(0.25f, net.minecraft.world.item.Items.SAND, 1)
    );

    public GeneratedRecipe BAUXITE_ORE = create(com.remizerexe.further_on.registry.FOBlocks.BAUXITE.get(), b -> b
            .duration(300)
            .output(com.remizerexe.further_on.registry.FOItems.BAUXITE_DUST.get(), 1)
            .output(0.5f, com.remizerexe.further_on.registry.FOItems.BAUXITE_DUST.get(), 1)
    );

    public GeneratedRecipe COAL = create(net.minecraft.world.item.Items.COAL, b -> b
            .duration(250)
            .output(com.remizerexe.further_on.registry.FOItems.COKE, 1)
            .output(0.5f, com.remizerexe.further_on.registry.FOItems.coal_tar, 1)
            .output(0.1f, com.remizerexe.further_on.registry.FOItems.coal_tar, 1)
    );

    public GeneratedRecipe RAW_URANIUM = create(com.remizerexe.further_on.registry.FOItems.RAW_URANIUM.get(), b -> b
            .duration(300)
            .output(com.remizerexe.further_on.registry.FOItems.crushed_raw_uranium.get(), 1)
            .output(0.3f, com.remizerexe.further_on.registry.FOItems.crushed_raw_uranium.get(), 1)
    );

    public GeneratedRecipe CRUSHED_URANIUM = create(com.remizerexe.further_on.registry.FOItems.crushed_raw_uranium.get(), b -> b
            .duration(300)
            .output(com.remizerexe.further_on.registry.FOItems.yellowcake.get(), 1)
            .output(0.2f, com.remizerexe.further_on.registry.FOItems.yellowcake.get(), 1)
    );

    public GeneratedRecipe RAW_GRAPHITE = create(com.remizerexe.further_on.registry.FOItems.raw_graphite.get(), b -> b
            .duration(250)
            .output(com.remizerexe.further_on.registry.FOItems.graphite_dust.get(), 1)
            .output(0.5f, com.remizerexe.further_on.registry.FOItems.graphite_dust.get(), 1)
    );

    public GeneratedRecipe UF6_CENTRIFUGE = create(com.remizerexe.further_on.registry.FOItems.uranium_hexafluoride.get(), b -> b
            .duration(500)
            .output(com.remizerexe.further_on.registry.FOItems.enriched_uranium_pellet.get(), 1)
            .output(0.1f, com.remizerexe.further_on.registry.FOItems.enriched_uranium_pellet.get(), 1) // Gas centrifuge cascade enrichment
    );

    public FOCrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
