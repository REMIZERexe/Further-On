package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.CuttingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOCuttingRecipeGen extends CuttingRecipeGen {

    public GeneratedRecipe COPPER_WIRE = create("copper_wire", b -> b
            .require(net.minecraft.world.item.Items.COPPER_INGOT)
            .output(com.remizerexe.further_on.registry.FOItems.copper_wire.get(), 2)
            .duration(50)
    );

    public GeneratedRecipe GOLD_WIRE = create("gold_wire", b -> b
            .require(net.minecraft.world.item.Items.GOLD_INGOT)
            .output(com.remizerexe.further_on.registry.FOItems.gold_wire.get(), 2)
            .duration(50)
    );

    public GeneratedRecipe SILVER_WIRE = create("silver_wire", b -> b
            .require(net.minecraft.tags.ItemTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "ingots/silver")))
            .output(com.remizerexe.further_on.registry.FOItems.silver_wire.get(), 2)
            .duration(50)
    );

    public GeneratedRecipe SILICON_WAFER = create("silicon_wafer", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.silicon_boule.get())
            .output(com.remizerexe.further_on.registry.FOItems.silicon_wafer.get(), 4)
            .duration(100)
    );

    public FOCuttingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
