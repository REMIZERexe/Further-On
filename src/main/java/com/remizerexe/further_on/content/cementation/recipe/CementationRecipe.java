package com.remizerexe.further_on.content.cementation.recipe;

import com.remizerexe.further_on.registry.FORecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe fired in the cementation furnace: a batch of one ingot type packed
 * in coke becomes the listed outputs. Every ingredient entry stands for one
 * ingot of the batch (all entries match the same item), so a recipe with nine
 * ingredients needs nine ingots in the oven and yields its outputs once per
 * full batch. {@code processingTime} is the firing time in ticks.
 */
public class CementationRecipe extends StandardProcessingRecipe<RecipeWrapper> {

    public static final int DEFAULT_DURATION = 2400;
    /** Ingots per batch for the standard recipes; matches the oven's ingot capacity. */
    public static final int BATCH_SIZE = 9;
    public static final int MAX_OUTPUTS = 4;

    public CementationRecipe(ProcessingRecipeParams params) {
        super(FORecipeTypes.CEMENTATION, params);
    }

    /** Ticks the sealed, heated furnace must run before the batch converts. */
    public int getFiringTicks() {
        int duration = getProcessingDuration();
        return duration > 0 ? duration : DEFAULT_DURATION;
    }

    /** Ingots the oven must hold for one batch. */
    public int getInputCount() {
        return getIngredients().size();
    }

    /** Output stacks for one full batch, in recipe order. Chance outputs are rolled. */
    public List<ItemStack> rollBatchOutputs(net.minecraft.util.RandomSource random) {
        List<ItemStack> outputs = new ArrayList<>();
        for (ProcessingOutput output : getRollableResults()) {
            ItemStack rolled = output.rollOutput(random);
            if (!rolled.isEmpty()) outputs.add(rolled);
        }
        return outputs;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    protected int getMaxInputCount() {
        return BATCH_SIZE;
    }

    @Override
    protected int getMaxOutputCount() {
        return MAX_OUTPUTS;
    }

    /** Matches on item type only; the oven checks the batch count itself. */
    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        if (inv.isEmpty()) return false;
        return getIngredients().get(0).test(inv.getItem(0));
    }
}
