package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOMixingRecipeGen extends MixingRecipeGen {
    public GeneratedRecipe PANCAKES = create("pancakes", b -> b
            .require(net.minecraft.world.item.Items.WHEAT)
            .require(net.minecraft.world.item.Items.EGG)
            .require(net.minecraft.world.item.Items.MILK_BUCKET)
            .output(com.remizerexe.further_on.registry.FOItems.niko_pancakes)
    );

    public GeneratedRecipe SYRUP = create("syrup", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.spruce_resin_item)
            .require(net.minecraft.world.item.Items.SUGAR)
            .output(com.remizerexe.further_on.registry.FOItems.maple_syrup, 2)
    );

    public GeneratedRecipe DURALUMIN = create("duralumin", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.ALUMINIUM.get())
            .require(com.remizerexe.further_on.registry.FOItems.MAGNESIUM.get())
            .output(com.remizerexe.further_on.registry.FOItems.duralumin_ingot, 2)
            .requiresHeat(com.simibubi.create.content.processing.recipe.HeatCondition.HEATED)
    );

    public GeneratedRecipe RESIN_PROCESSING = create("resin_processing", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.spruce_resin_item)
            .require(net.minecraft.world.item.Items.WATER_BUCKET) // cheap substitute for fluid ingredient to keep datagen simple
            .output(com.remizerexe.further_on.registry.FOItems.latex, 2)
            .output(com.remizerexe.further_on.registry.FOItems.turpentine, 1)
    );

    public GeneratedRecipe SPONGY_IRON = create("spongy_iron", b -> b
            .require(net.minecraft.world.item.Items.RAW_IRON)
            .require(net.minecraft.tags.ItemTags.COALS)
            .output(com.remizerexe.further_on.registry.FOItems.spongy_iron, 1)
            .requiresHeat(com.simibubi.create.content.processing.recipe.HeatCondition.HEATED)
    );

    public GeneratedRecipe RAW_SILICON = create("raw_silicon", b -> b
            .require(net.minecraft.world.item.Items.QUARTZ)
            .require(net.minecraft.tags.ItemTags.COALS)
            .output(com.remizerexe.further_on.registry.FOItems.raw_silicon, 1)
            .requiresHeat(com.simibubi.create.content.processing.recipe.HeatCondition.SUPERHEATED) // Carbothermic reduction of silica
    );

    public GeneratedRecipe PURIFIED_SILICON = create("purified_silicon", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.raw_silicon.get())
            .require(com.remizerexe.further_on.registry.FOItems.turpentine.get())
            .output(com.remizerexe.further_on.registry.FOItems.purified_silicon, 1)
            .requiresHeat(com.simibubi.create.content.processing.recipe.HeatCondition.HEATED) // Siemens process purification
    );

    public GeneratedRecipe URANIUM_HEXAFLUORIDE = create("uranium_hexafluoride", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.yellowcake.get())
            .require(com.remizerexe.further_on.registry.FOItems.fluorite.get())
            .output(com.remizerexe.further_on.registry.FOItems.uranium_hexafluoride, 1)
            .requiresHeat(com.simibubi.create.content.processing.recipe.HeatCondition.HEATED) // UF6 conversion process
    );


    public GeneratedRecipe ALUMINA_DUST = create("alumina_dust", b -> b
            .require(com.remizerexe.further_on.registry.FOItems.BAUXITE_DUST.get())
            .require(net.minecraft.world.item.Items.WATER_BUCKET)
            .output(com.remizerexe.further_on.registry.FOItems.alumina_dust.get(), 1)
    );

    public FOMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }
}
