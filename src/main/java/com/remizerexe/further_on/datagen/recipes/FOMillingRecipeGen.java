package com.remizerexe.further_on.datagen.recipes;

import com.remizerexe.further_on.FurtherOn;
import com.remizerexe.further_on.registry.FOItems;
import com.simibubi.create.api.data.recipe.MillingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOMillingRecipeGen extends MillingRecipeGen {
    // Yet empty.

    public FOMillingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }

    public final GeneratedRecipe LIMESTONE_MILLING = create(FurtherOn.asResource("milling/limestone_dust"),
            b -> b.duration(100)
                    .require(Items.CALCITE)
                    .output(FOItems.LIMESTONE_DUST.get())
    );
}
