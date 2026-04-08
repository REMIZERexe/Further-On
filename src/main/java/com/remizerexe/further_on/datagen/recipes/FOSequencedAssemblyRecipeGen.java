package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {
    // Yet empty.

    public FOSequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
