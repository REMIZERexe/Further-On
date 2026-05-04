package com.remizerexe.further_on;

import com.mojang.logging.LogUtils;
import com.remizerexe.further_on.registry.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(FurtherOn.MODID)
public class FurtherOn {
    public static final String MODID = "further_on";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item))))
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null); // The default is the search tab.
                                                                      // If we *don't* do this, things get
                                                                      // put there twice and the game crashes
    public static final Logger LOGGER = LogUtils.getLogger();

    public FurtherOn(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(FurtherOn::onRegister);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(EventPriority.HIGHEST, FODatagen::gatherDataHighPriority);
        modEventBus.addListener(EventPriority.LOWEST, FODatagen::gatherData);

        REGISTRATE.registerEventListeners(modEventBus);

        FOBlocks.register();
        FOItems.register();
        FOBlockEntities.register();
        FOTabs.register();
        FOFluids.register(modEventBus);
        FOWorldgen.register(modEventBus);
        FOMenuTypes.register(modEventBus);
        FOPartialModels.init();

        FORegistries.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, FurtherOnConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Yet empty.
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static void onRegister(final RegisterEvent event) {
        FOContraptionTypes.prepare();
    }
}
