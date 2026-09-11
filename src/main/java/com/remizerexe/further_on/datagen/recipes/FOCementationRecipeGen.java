package com.remizerexe.further_on.datagen.recipes;

import com.remizerexe.further_on.content.cementation.recipe.CementationRecipe;
import com.remizerexe.further_on.registry.FOItems;
import com.remizerexe.further_on.registry.FORecipeTypes;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider.GeneratedRecipe;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

/**
 * Batches fired in the cementation furnace. {@code duration} is the firing
 * time in ticks. Each ingredient entry is one ingot of the batch, so nine
 * {@code require} calls mean the oven must hold nine ingots; the outputs are
 * produced once per full batch.
 */
public class FOCementationRecipeGen extends ProcessingRecipeGen<ProcessingRecipeParams, CementationRecipe, StandardProcessingRecipe.Builder<CementationRecipe>> {

    public GeneratedRecipe BLISTER_STEEL = create("blister_steel_from_iron", b -> {
        for (int i = 0; i < CementationRecipe.BATCH_SIZE; i++) b.require(Items.IRON_INGOT);
        return b
                .output(FOItems.BLISTER_STEEL.get(), 45)
                .output(FOItems.ASH.get(), 30)
                .duration(2400);
    });

    public FOCementationRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return FORecipeTypes.CEMENTATION;
    }

    @Override
    protected StandardProcessingRecipe.Builder<CementationRecipe> getBuilder(ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(CementationRecipe::new, id);
    }
}
