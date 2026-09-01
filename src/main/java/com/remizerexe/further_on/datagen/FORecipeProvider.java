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
        
        // The annoying background work: registering all the basic block <-> ingot recipes
        metalCompacting(recipeOutput, com.remizerexe.further_on.registry.FOItems.CARBON_STEEL.get(), com.remizerexe.further_on.registry.FOBlocks.CARBON_STEEL_BLOCK.get(), "carbon_steel");
        metalCompacting(recipeOutput, com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get(), com.remizerexe.further_on.registry.FOBlocks.STRUCTURAL_STEEL_BLOCK.get(), "structural_steel");
        metalCompacting(recipeOutput, com.remizerexe.further_on.registry.FOItems.STAINLESS_STEEL.get(), com.remizerexe.further_on.registry.FOBlocks.STAINLESS_STEEL_BLOCK.get(), "stainless_steel");
        metalCompacting(recipeOutput, com.remizerexe.further_on.registry.FOItems.MAGNESIUM.get(), com.remizerexe.further_on.registry.FOBlocks.MAGNESIUM_BLOCK.get(), "magnesium");
        metalCompacting(recipeOutput, com.remizerexe.further_on.registry.FOItems.ALUMINIUM.get(), com.remizerexe.further_on.registry.FOBlocks.ALUMINIUM_BLOCK.get(), "aluminium");
        metalCompacting(recipeOutput, com.remizerexe.further_on.registry.FOItems.ZIRCONIUM.get(), com.remizerexe.further_on.registry.FOBlocks.ZIRCONIUM_BLOCK.get(), "zirconium");
        metalCompacting(recipeOutput, com.remizerexe.further_on.registry.FOItems.COKE.get(), com.remizerexe.further_on.registry.FOBlocks.COKE_BLOCK.get(), "coke");
        metalCompacting(recipeOutput, com.remizerexe.further_on.registry.FOItems.URANIUM.get(), com.remizerexe.further_on.registry.FOBlocks.URANIUM_BLOCK.get(), "uranium");

        // Complex ore processing (Blasting/Smelting)
        oreSmeltingAndBlasting(recipeOutput, java.util.List.of(com.remizerexe.further_on.registry.FOItems.alumina_dust.get()), com.remizerexe.further_on.registry.FOItems.ALUMINIUM.get(), 0.7f, 200, "aluminium");
        oreSmeltingAndBlasting(recipeOutput, java.util.List.of(com.remizerexe.further_on.registry.FOItems.graphite_dust.get()), com.remizerexe.further_on.registry.FOItems.GRAPHITE.get(), 0.7f, 200, "graphite");
        oreSmeltingAndBlasting(recipeOutput, java.util.List.of(com.remizerexe.further_on.registry.FOItems.enriched_uranium_pellet.get()), com.remizerexe.further_on.registry.FOItems.URANIUM.get(), 1.0f, 200, "uranium");
        
        // Fire Clay processing loop
        net.minecraft.data.recipes.SimpleCookingRecipeBuilder.smelting(net.minecraft.world.item.crafting.Ingredient.of(com.remizerexe.further_on.registry.FOItems.FIRE_CLAY_BALL.get()), net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.FIRE_CLAY_BRICK.get(), 0.3f, 200)
                .unlockedBy("has_item", has(com.remizerexe.further_on.registry.FOItems.FIRE_CLAY_BALL.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":fire_clay_brick_smelting");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, com.remizerexe.further_on.registry.FOBlocks.FIRE_CLAY_BRICKS.get())
                .pattern("##")
                .pattern("##")
                .define('#', com.remizerexe.further_on.registry.FOItems.FIRE_CLAY_BRICK.get())
                .unlockedBy("has_item", has(com.remizerexe.further_on.registry.FOItems.FIRE_CLAY_BRICK.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":fire_clay_bricks_crafting");

        // Electrical Components
        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.copper_coil.get())
                .pattern(" W ")
                .pattern("WIW")
                .pattern(" W ")
                .define('W', com.remizerexe.further_on.registry.FOItems.copper_wire.get())
                .define('I', net.minecraft.world.item.Items.IRON_INGOT)
                .unlockedBy("has_copper_wire", has(com.remizerexe.further_on.registry.FOItems.copper_wire.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":copper_coil");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.permanent_magnet.get())
                .pattern(" R ")
                .pattern("RIR")
                .pattern(" R ")
                .define('R', net.minecraft.world.item.Items.REDSTONE)
                .define('I', net.minecraft.world.item.Items.IRON_INGOT)
                .unlockedBy("has_redstone", has(net.minecraft.world.item.Items.REDSTONE))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":permanent_magnet");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.stator_core.get())
                .pattern(" M ")
                .pattern("MIM")
                .pattern(" M ")
                .define('M', com.remizerexe.further_on.registry.FOItems.permanent_magnet.get())
                .define('I', net.minecraft.world.item.Items.IRON_BLOCK)
                .unlockedBy("has_magnet", has(com.remizerexe.further_on.registry.FOItems.permanent_magnet.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":stator_core");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.rotor_core.get())
                .pattern(" C ")
                .pattern("CIC")
                .pattern(" C ")
                .define('C', com.remizerexe.further_on.registry.FOItems.copper_coil.get())
                .define('I', net.minecraft.world.item.Items.IRON_BLOCK)
                .unlockedBy("has_coil", has(com.remizerexe.further_on.registry.FOItems.copper_coil.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":rotor_core");

        // Substation & Logic Blocks
        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.REDSTONE, com.remizerexe.further_on.registry.FOBlocks.circuit_breaker.get())
                .pattern(" S ")
                .pattern("SCS")
                .pattern(" R ")
                .define('S', com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get())
                .define('C', com.remizerexe.further_on.registry.FOItems.copper_coil.get())
                .define('R', net.minecraft.world.item.Items.REDSTONE)
                .unlockedBy("has_steel", has(com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":circuit_breaker");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.REDSTONE, com.remizerexe.further_on.registry.FOBlocks.large_switch.get())
                .pattern(" L ")
                .pattern(" S ")
                .pattern("SSS")
                .define('L', net.minecraft.world.item.Items.LEVER)
                .define('S', com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get())
                .unlockedBy("has_steel", has(com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":large_switch");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.REDSTONE, com.remizerexe.further_on.registry.FOBlocks.logic_panel.get())
                .pattern("SSS")
                .pattern("RRR")
                .pattern("SSS")
                .define('S', com.remizerexe.further_on.registry.FOItems.STAINLESS_STEEL.get())
                .define('R', net.minecraft.world.item.Items.REDSTONE)
                .unlockedBy("has_steel", has(com.remizerexe.further_on.registry.FOItems.STAINLESS_STEEL.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":logic_panel");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOBlocks.hyper_blaze_burner.get())
                .pattern(" C ")
                .pattern("CBC")
                .pattern(" C ")
                .define('C', com.remizerexe.further_on.registry.FOBlocks.CARBON_STEEL_BLOCK.get())
                .define('B', com.simibubi.create.AllBlocks.EMPTY_BLAZE_BURNER.get())
                .unlockedBy("has_burner", has(com.simibubi.create.AllBlocks.EMPTY_BLAZE_BURNER.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":hyper_blaze_burner");

        // Nuclear Crafting
        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.uranium_rod.get())
                .pattern(" U ")
                .pattern(" S ")
                .pattern(" U ")
                .define('U', com.remizerexe.further_on.registry.FOItems.URANIUM.get())
                .define('S', com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get())
                .unlockedBy("has_uranium", has(com.remizerexe.further_on.registry.FOItems.URANIUM.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":uranium_rod");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.control_rod.get())
                .pattern(" Z ")
                .pattern(" S ")
                .pattern(" Z ")
                .define('Z', com.remizerexe.further_on.registry.FOItems.ZIRCONIUM.get())
                .define('S', com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get())
                .unlockedBy("has_zirconium", has(com.remizerexe.further_on.registry.FOItems.ZIRCONIUM.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":control_rod");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOBlocks.reactor_casing.get(), 4)
                .pattern("ZSZ")
                .pattern("SCS")
                .pattern("ZSZ")
                .define('Z', com.remizerexe.further_on.registry.FOBlocks.ZIRCONIUM_BLOCK.get())
                .define('S', com.remizerexe.further_on.registry.FOBlocks.STRUCTURAL_STEEL_BLOCK.get())
                .define('C', com.remizerexe.further_on.registry.FOItems.copper_coil.get())
                .unlockedBy("has_zirconium", has(com.remizerexe.further_on.registry.FOBlocks.ZIRCONIUM_BLOCK.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":reactor_casing");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOBlocks.reactor_glass.get(), 2)
                .pattern(" G ")
                .pattern("GCG")
                .pattern(" G ")
                .define('G', net.minecraft.world.item.Items.GLASS)
                .define('C', com.remizerexe.further_on.registry.FOBlocks.reactor_casing.get())
                .unlockedBy("has_casing", has(com.remizerexe.further_on.registry.FOBlocks.reactor_casing.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":reactor_glass");

        // Gas Storage & Logistics
        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOBlocks.airtight_gas_tank.get())
                .pattern(" S ")
                .pattern("SGS")
                .pattern(" S ")
                .define('S', com.remizerexe.further_on.registry.FOItems.STAINLESS_STEEL.get())
                .define('G', net.minecraft.world.item.Items.GLASS)
                .unlockedBy("has_steel", has(com.remizerexe.further_on.registry.FOItems.STAINLESS_STEEL.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":airtight_gas_tank");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOBlocks.turbine_pump.get())
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" T ")
                .define('S', com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get())
                .define('P', com.simibubi.create.AllBlocks.MECHANICAL_PUMP.get())
                .define('T', com.remizerexe.further_on.registry.FOItems.tungsten_ingot.get())
                .unlockedBy("has_pump", has(com.simibubi.create.AllBlocks.MECHANICAL_PUMP.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":turbine_pump");

        // Electrical Network
        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOBlocks.transformer_casing.get())
                .pattern(" S ")
                .pattern(" C ")
                .pattern(" S ")
                .define('S', com.remizerexe.further_on.registry.FOItems.STAINLESS_STEEL.get())
                .define('C', com.remizerexe.further_on.registry.FOItems.copper_coil.get())
                .unlockedBy("has_coil", has(com.remizerexe.further_on.registry.FOItems.copper_coil.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":transformer_casing");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOBlocks.large_electric_motor.get())
                .pattern(" R ")
                .pattern("SCS")
                .pattern(" M ")
                .define('R', com.remizerexe.further_on.registry.FOItems.rotor_core.get())
                .define('S', com.remizerexe.further_on.registry.FOItems.stator_core.get())
                .define('C', com.remizerexe.further_on.registry.FOItems.copper_coil.get())
                .define('M', com.remizerexe.further_on.registry.FOItems.STRUCTURAL_STEEL.get())
                .unlockedBy("has_rotor", has(com.remizerexe.further_on.registry.FOItems.rotor_core.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":large_electric_motor");

        net.minecraft.data.recipes.SingleItemRecipeBuilder.stonecutting(net.minecraft.world.item.crafting.Ingredient.of(com.remizerexe.further_on.registry.FOBlocks.FIRE_CLAY_BRICKS.get()), net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, com.remizerexe.further_on.registry.FOBlocks.FIRE_CLAY_BRICK_STAIRS.get(), 1)
                .unlockedBy("has_item", has(com.remizerexe.further_on.registry.FOBlocks.FIRE_CLAY_BRICKS.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":fire_clay_brick_stairs_stonecutting");

        net.minecraft.data.recipes.SingleItemRecipeBuilder.stonecutting(net.minecraft.world.item.crafting.Ingredient.of(com.remizerexe.further_on.registry.FOBlocks.FIRE_CLAY_BRICKS.get()), net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, com.remizerexe.further_on.registry.FOBlocks.FIRE_CLAY_BRICK_WALL.get(), 1)
                .unlockedBy("has_item", has(com.remizerexe.further_on.registry.FOBlocks.FIRE_CLAY_BRICKS.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":fire_clay_brick_wall_stonecutting");

        // Electronics
        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.capacitor.get(), 2)
                .pattern(" C ")
                .pattern(" S ")
                .pattern(" R ")
                .define('C', com.remizerexe.further_on.registry.FOItems.copper_wire.get())
                .define('S', com.remizerexe.further_on.registry.FOItems.polished_silicon_wafer.get())
                .define('R', net.minecraft.world.item.Items.REDSTONE)
                .unlockedBy("has_wafer", has(com.remizerexe.further_on.registry.FOItems.polished_silicon_wafer.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":capacitor");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.transistor.get(), 2)
                .pattern(" G ")
                .pattern(" S ")
                .pattern(" R ")
                .define('G', com.remizerexe.further_on.registry.FOItems.gold_wire.get())
                .define('S', com.remizerexe.further_on.registry.FOItems.polished_silicon_wafer.get())
                .define('R', net.minecraft.world.item.Items.REDSTONE)
                .unlockedBy("has_wafer", has(com.remizerexe.further_on.registry.FOItems.polished_silicon_wafer.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":transistor");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.MISC, com.remizerexe.further_on.registry.FOItems.printed_circuit_board.get())
                .pattern("GCG")
                .pattern("SWS")
                .pattern("WTW")
                .define('G', com.remizerexe.further_on.registry.FOItems.gold_wire.get())
                .define('C', com.remizerexe.further_on.registry.FOItems.capacitor.get())
                .define('S', com.remizerexe.further_on.registry.FOItems.polished_silicon_wafer.get())
                .define('W', com.remizerexe.further_on.registry.FOItems.copper_wire.get())
                .define('T', com.remizerexe.further_on.registry.FOItems.transistor.get())
                .unlockedBy("has_transistor", has(com.remizerexe.further_on.registry.FOItems.transistor.get()))
                .save(recipeOutput, com.remizerexe.further_on.FurtherOn.MODID + ":printed_circuit_board");
    }

    private void oreSmeltingAndBlasting(RecipeOutput output, java.util.List<net.minecraft.world.level.ItemLike> inputs, net.minecraft.world.level.ItemLike result, float experience, int cookingTime, String group) {
        for (net.minecraft.world.level.ItemLike input : inputs) {
            String name = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(input.asItem()).getPath();
            net.minecraft.data.recipes.SimpleCookingRecipeBuilder.smelting(net.minecraft.world.item.crafting.Ingredient.of(input), net.minecraft.data.recipes.RecipeCategory.MISC, result, experience, cookingTime)
                    .unlockedBy("has_item", has(input))
                    .save(output, com.remizerexe.further_on.FurtherOn.MODID + ":" + name + "_smelting");
            net.minecraft.data.recipes.SimpleCookingRecipeBuilder.blasting(net.minecraft.world.item.crafting.Ingredient.of(input), net.minecraft.data.recipes.RecipeCategory.MISC, result, experience, cookingTime / 2)
                    .unlockedBy("has_item", has(input))
                    .save(output, com.remizerexe.further_on.FurtherOn.MODID + ":" + name + "_blasting");
        }
    }

    private void metalCompacting(RecipeOutput output, net.minecraft.world.level.ItemLike item, net.minecraft.world.level.ItemLike block, String name) {
        net.minecraft.data.recipes.ShapelessRecipeBuilder.shapeless(net.minecraft.data.recipes.RecipeCategory.MISC, item, 9)
                .requires(block)
                .unlockedBy("has_block", has(block))
                .save(output, com.remizerexe.further_on.FurtherOn.MODID + ":" + name + "_from_block");

        net.minecraft.data.recipes.ShapedRecipeBuilder.shaped(net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS, block)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', item)
                .unlockedBy("has_item", has(item))
                .save(output, com.remizerexe.further_on.FurtherOn.MODID + ":" + name + "_block");
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
        GENERATORS.add(new com.remizerexe.further_on.datagen.recipes.FOBlastCompressingRecipeGen(output, registries));

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
