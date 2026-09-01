package com.remizerexe.further_on.content.blast_compressor.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import com.remizerexe.further_on.registry.FORecipeTypes;

public class FOBlastCompressingRecipe extends ProcessingRecipe<RecipeWrapper> {

    public FOBlastCompressingRecipe(ProcessingRecipeParams params) {
        super(FORecipeTypes.BLAST_COMPRESSING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1; // It holds 1 item to be compressed
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        if (inv.isEmpty()) return false;
        return ingredients.get(0).test(inv.getItem(0));
    }
}
