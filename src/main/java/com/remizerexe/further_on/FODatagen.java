package com.remizerexe.further_on;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remizerexe.further_on.content.ponder.FOPonderPlugin;
import com.remizerexe.further_on.datagen.FOGeneratedEntriesProvider;
import com.remizerexe.further_on.datagen.FORecipeProvider;
import com.remizerexe.further_on.datagen.recipes.FOMechanicalCraftingRecipeGen;
import com.remizerexe.further_on.datagen.recipes.FOSequencedAssemblyRecipeGen;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static com.remizerexe.further_on.FurtherOn.MODID;
import static com.remizerexe.further_on.FurtherOn.REGISTRATE;

public class FODatagen {
    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains(MODID))
            addExtraRegistrateData();
    }

    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(MODID)) {
            return;
        }
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();


        generator.addProvider(event.includeServer(), new FOMechanicalCraftingRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new FOSequencedAssemblyRecipeGen(output, lookupProvider));
        generator.addProvider(event.includeServer(), new FOGeneratedEntriesProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new FORecipeProvider(output, lookupProvider).namedWrapper());

        System.out.println("Gathering data for Create: Further On!");
        System.out.println(event.includeServer());
        if (event.includeServer()) {
            FORecipeProvider.registerAllProcessing(generator, output, lookupProvider);
        }
    }
    private static void addExtraRegistrateData() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            provideDefaultLang("default", provider::add);
            providePonderLang(provider::add);
        });
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/further_on/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        PonderIndex.addPlugin(new FOPonderPlugin());
        PonderIndex.getLangAccess().provideLang(MODID, consumer);
    }
}
