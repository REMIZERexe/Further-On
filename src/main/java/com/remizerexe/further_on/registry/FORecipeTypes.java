package com.remizerexe.further_on.registry;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import com.remizerexe.further_on.FurtherOn;
import com.remizerexe.further_on.content.blast_compressor.recipe.FOBlastCompressingRecipe;

import java.util.function.Supplier;

public enum FORecipeTypes implements IRecipeTypeInfo {
    BLAST_COMPRESSING(FOBlastCompressingRecipe::new);

    private final ResourceLocation id;
    private final Supplier<RecipeSerializer<?>> serializerObject;
    private final Supplier<RecipeType<?>> typeObject;

    FORecipeTypes(ProcessingRecipeFactory<?> processingFactory) {
        this.id = FurtherOn.asResource(net.createmod.catnip.lang.Lang.asId(name()));
        this.serializerObject = FORegistries.RECIPE_SERIALIZERS.register(id.getPath(), () -> new ProcessingRecipeSerializer<>(processingFactory));
        this.typeObject = FORegistries.RECIPE_TYPES.register(id.getPath(), () -> RecipeType.simple(id));
    }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() { return (T) serializerObject.get(); }

    @Override
    public <T extends RecipeType<?>> T getType() { return (T) typeObject.get(); }

    public static void register() {}
}
