package com.remizerexe.further_on.datagen.recipes;

import com.remizerexe.further_on.registry.FOItems;
import com.simibubi.create.api.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOSequencedAssemblyRecipeGen extends SequencedAssemblyRecipeGen {

    /**
     * Blister steel is worked into carbon steel by pressing it three times on a depot or belt,
     * in the same way a precision mechanism is assembled.
     */
    public GeneratedRecipe CARBON_STEEL = create("carbon_steel", b -> b
            .require(FOItems.BLISTER_STEEL.get())
            .transitionTo(FOItems.UNFINISHED_STEEL.get())
            .addOutput(FOItems.CARBON_STEEL.get(), 1)
            .loops(3)
            .addStep(PressingRecipe::new, rb -> rb)
    );

    public FOSequencedAssemblyRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
