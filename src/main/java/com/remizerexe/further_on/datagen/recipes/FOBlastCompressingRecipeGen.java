package com.remizerexe.further_on.datagen.recipes;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.api.data.recipe.BaseRecipeProvider.GeneratedRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import com.remizerexe.further_on.registry.FORecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.remizerexe.further_on.content.blast_compressor.recipe.FOBlastCompressingRecipe;
import java.util.concurrent.CompletableFuture;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOBlastCompressingRecipeGen extends ProcessingRecipeGen<ProcessingRecipeParams, FOBlastCompressingRecipe, StandardProcessingRecipe.Builder<FOBlastCompressingRecipe>> {
    
    public GeneratedRecipe BEDROCK_ALLOY = create("bedrock_alloy_from_obsidian", b -> b
            .require(net.minecraft.world.item.Items.OBSIDIAN)
            // Can add more requirements if we want multi-item compression, but let's stick to 1:1 for the block for now
            .output(com.remizerexe.further_on.registry.FOItems.bedrock_alloy_ingot.get())
    );

    public GeneratedRecipe DIAMOND_FROM_COAL = create("diamond_from_coal_block", b -> b
            .require(net.minecraft.world.item.Items.COAL_BLOCK)
            .output(net.minecraft.world.item.Items.DIAMOND)
    );

    public FOBlastCompressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, MODID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return FORecipeTypes.BLAST_COMPRESSING;
    }

    @Override
    protected StandardProcessingRecipe.Builder<FOBlastCompressingRecipe> getBuilder(net.minecraft.resources.ResourceLocation id) {
        return new StandardProcessingRecipe.Builder<>(FOBlastCompressingRecipe::new, id);
    }
}
