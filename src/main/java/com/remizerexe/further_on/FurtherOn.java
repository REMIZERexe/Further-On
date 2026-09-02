package com.remizerexe.further_on;

import com.mojang.logging.LogUtils;
import com.remizerexe.further_on.content.equipment.WeldingMaskItem;
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

import static com.simibubi.create.content.equipment.goggles.GogglesItem.addIsWearingPredicate;

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
        FORecipeTypes.register();
        FOTabs.register();
        FOFluids.register(modEventBus);
        FOWorldgen.register(modEventBus);
        FOMenuTypes.register(modEventBus);
        FOPartialModels.init();

        FORegistries.register(modEventBus);
        FODataComponents.register(modEventBus);

        REGISTRATE.addRawLang("key.categories.further_on", "Further On");
        REGISTRATE.addRawLang("key.further_on.toggle_welding_mask_visor", "Toggle Welding Mask Visor");

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, FurtherOnConfig.SPEC);

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onExplosion);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onLivingDamage);
    }

    private void onLivingDamage(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (event.getSource().is(net.minecraft.world.damagesource.DamageTypes.HOT_FLOOR)) {
            for (net.minecraft.world.item.ItemStack armor : event.getEntity().getArmorSlots()) {
                if (armor.is(com.remizerexe.further_on.registry.FOItems.hazard_boots.get())) {
                    event.setCanceled(true); // Fully cancel the event so boots don't lose durability!
                    break;
                }
            }
        }
    }

    private void onExplosion(net.neoforged.neoforge.event.level.ExplosionEvent.Detonate event) {
        if (event.getLevel().isClientSide()) return;

        net.minecraft.world.phys.Vec3 center = event.getExplosion().center();
        // BUG FIX: Cross-mod nukes with massive radii (e.g. 100+) would cause this scanner 
        // to loop over millions of blocks in a single tick, lagging the server.
        // The Blast Compressor only needs to be triggered by local TNT, so we cap the scan radius at 8.0 blocks!
        float radius = Math.min(event.getExplosion().radius(), 8.0f);

        // Optimized scan: minimal power intensive. We only query the BlockState first, 
        // which is extremely fast, before doing heavy BlockEntity lookups.
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(center, center).inflate(radius);
        net.minecraft.core.BlockPos.betweenClosedStream(bounds).forEach(pos -> {
            if (center.distanceToSqr(pos.getCenter()) <= radius * radius) {
                // BUG FIX: Ensure we don't accidentally force-load chunks if the explosion happens on a chunk border!
                if (!event.getLevel().hasChunkAt(pos)) return;
                
                net.minecraft.world.level.block.state.BlockState state = event.getLevel().getBlockState(pos);
                if (state.is(com.remizerexe.further_on.registry.FOBlocks.BLAST_COMPRESSOR.get())) {
                    net.minecraft.world.level.block.entity.BlockEntity be = event.getLevel().getBlockEntity(pos);
                    if (be instanceof com.remizerexe.further_on.content.blast_compressor.BlastCompressorBlockEntity compressor) {
                        compressor.onExplosionHit();
                    }
                }
            }
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        addIsWearingPredicate(
                WeldingMaskItem::isProtecting);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static void onRegister(final RegisterEvent event) {
        FOContraptionTypes.prepare();
    }
}
