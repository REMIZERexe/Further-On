package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.HauntingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOHauntingRecipeGen extends HauntingRecipeGen {
    // Yet empty.

    public GeneratedRecipe FLUORITE = create("fluorite", b -> b
            .require(net.minecraft.world.item.Items.QUARTZ)
            .output(com.remizerexe.further_on.registry.FOItems.fluorite.get())
    );

    public FOHauntingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
