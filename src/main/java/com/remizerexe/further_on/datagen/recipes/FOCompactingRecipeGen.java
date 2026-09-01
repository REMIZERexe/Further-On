package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOCompactingRecipeGen extends CompactingRecipeGen {
    public GeneratedRecipe CAST_CARBON_STEEL = create("cast_carbon_steel", b -> b
            .require(com.remizerexe.further_on.registry.FOFluids.MOLTEN_STEEL_STILL.get(), 90)
            .output(com.remizerexe.further_on.registry.FOItems.CARBON_STEEL)
    );

    public GeneratedRecipe CAST_CARBON_STEEL_BLOCK = create("cast_carbon_steel_block", b -> b
            .require(com.remizerexe.further_on.registry.FOFluids.MOLTEN_STEEL_STILL.get(), 810)
            .output(com.remizerexe.further_on.registry.FOBlocks.CARBON_STEEL_BLOCK)
    );

    public GeneratedRecipe CAST_STRUCTURAL_BEAM = create("cast_structural_beam", b -> b
            .require(com.remizerexe.further_on.registry.FOFluids.MOLTEN_STEEL_STILL.get(), 810)
            .require(com.remizerexe.further_on.registry.FOItems.FIRE_CLAY_BALL)
            .output(com.remizerexe.further_on.registry.FOBlocks.CAST_BEAM)
    );

    public GeneratedRecipe BIOFUEL_PELLET = create("biofuel_pellet", b -> b
            .require(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.tags.ItemTags.LEAVES))
            .require(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.tags.ItemTags.LEAVES))
            .require(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.tags.ItemTags.LEAVES))
            .require(net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.tags.ItemTags.LEAVES))
            .output(com.remizerexe.further_on.registry.FOItems.biofuel_pellet)
    );

    public GeneratedRecipe SILICON_BOULE = create("silicon_boule", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.purified_silicon.get())
            .output(com.remizerexe.further_on.registry.FOItems.silicon_boule.get())
            .requiresHeat(com.simibubi.create.content.processing.recipe.HeatCondition.SUPERHEATED) // Czochralski boule pulling process
    );

    public FOCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
