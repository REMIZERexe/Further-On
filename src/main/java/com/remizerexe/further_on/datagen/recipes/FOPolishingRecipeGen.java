package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.PolishingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOPolishingRecipeGen extends PolishingRecipeGen {
    // Yet empty.

    public GeneratedRecipe POLISHED_SILICON_WAFER = create("polished_silicon_wafer", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.silicon_wafer.get())
            .output(com.remizerexe.further_on.registry.FOItems.polished_silicon_wafer.get())
    );

    public FOPolishingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
