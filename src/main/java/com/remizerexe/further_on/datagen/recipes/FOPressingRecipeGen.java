package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.PressingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOPressingRecipeGen extends PressingRecipeGen {
    
    public GeneratedRecipe SPONGY_IRON_PRESSING = create("spongy_iron_pressing", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.spongy_iron.get())
            .output(com.remizerexe.further_on.registry.FOItems.CARBON_STEEL.get())
    );

    public FOPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
