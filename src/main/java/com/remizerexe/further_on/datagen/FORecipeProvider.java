package com.remizerexe.further_on.datagen;

import com.remizerexe.further_on.datagen.recipes.*;
import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FORecipeProvider extends RecipeProvider {
    static final List<ProcessingRecipeGen<?, ?, ?>> GENERATORS = new ArrayList<>();

    public FORecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        super.buildRecipes(recipeOutput);
    }

    public static void registerAllProcessing(DataGenerator gen, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        GENERATORS.add(new FOCompactingRecipeGen(output, registries));
        GENERATORS.add(new FOCrushingRecipeGen(output, registries));
        GENERATORS.add(new FOCuttingRecipeGen(output, registries));
        GENERATORS.add(new FODeployingRecipeGen(output, registries));
        GENERATORS.add(new FOEmptyingRecipeGen(output, registries));
        GENERATORS.add(new FOFillingRecipeGen(output, registries));
        GENERATORS.add(new FOHauntingRecipeGen(output, registries));
        GENERATORS.add(new FOItemApplicationRecipeGen(output, registries));
        GENERATORS.add(new FOMillingRecipeGen(output, registries));
        GENERATORS.add(new FOMixingRecipeGen(output, registries));
        GENERATORS.add(new FOPolishingRecipeGen(output, registries));
        GENERATORS.add(new FOPressingRecipeGen(output, registries));
        GENERATORS.add(new FOWashingRecipeGen(output, registries));

        gen.addProvider(true, new DataProvider() {
            @Override
            public @NotNull String getName() {
                return "Create: Further On's Processing Recipes";
            }

            @Override
            public @NotNull CompletableFuture<?> run(@NotNull CachedOutput dc) {
                return CompletableFuture.allOf(GENERATORS.stream().map(gen -> gen.run(dc)).toArray(CompletableFuture[]::new));
            }
        });
    }

    public DataProvider namedWrapper() {
        return new DataProvider() {
            @Override
            public CompletableFuture<?> run(CachedOutput cachedOutput) {
                return FORecipeProvider.this.run(cachedOutput);
            }

            @Override
            public String getName() {
                return "Create: Further On's Standard Recipes";
            }
        };
    }
}
