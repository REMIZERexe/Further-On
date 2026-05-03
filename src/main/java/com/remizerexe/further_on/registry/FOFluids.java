package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.content.oil.OilFluid;
import com.remizerexe.further_on.content.oil.OilFluidBlock;
import com.remizerexe.further_on.content.blast_furnace.MoltenSteelFluidBlock;
import com.remizerexe.further_on.content.blast_furnace.MoltenSteelFluid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Consumer;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOFluids {

    // -------------------------------------------------------------------------
    // Registers — déclarés en premier
    // -------------------------------------------------------------------------

    public static final DeferredRegister<Fluid>     FLUIDS      =
            DeferredRegister.create(net.minecraft.core.registries.Registries.FLUID, MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, MODID);
    public static final DeferredRegister<Block>     FLUID_BLOCKS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK, MODID);
    public static final DeferredRegister<Item>      FLUID_ITEMS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.ITEM, MODID);

    // -------------------------------------------------------------------------
    // Oil
    // -------------------------------------------------------------------------

    public static final DeferredHolder<FluidType, FluidType> OIL_TYPE =
            FLUID_TYPES.register("oil", () -> new FluidType(
                    FluidType.Properties.create()
                            .density(900).viscosity(6000).temperature(300)
                            .canSwim(false).canDrown(false).supportsBoating(false)
            ) {
                @Override @OnlyIn(Dist.CLIENT)
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation STILL   = ResourceLocation.fromNamespaceAndPath(MODID, "block/oil_still");
                        private static final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath(MODID, "block/oil_flowing");
                        private static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(MODID, "block/oil_overlay");
                        @Override public ResourceLocation getStillTexture()   { return STILL; }
                        @Override public ResourceLocation getFlowingTexture() { return FLOWING; }
                        @Override public ResourceLocation getOverlayTexture() { return OVERLAY; }
                        @Override public int getTintColor() { return 0xFF1A1A1A; }
                    });
                }
            });

    public static final DeferredHolder<Fluid, FlowingFluid> OIL_STILL =
            FLUIDS.register("oil", () -> new OilFluid.Still(makeOilProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> OIL_FLOWING =
            FLUIDS.register("flowing_oil", () -> new OilFluid.Flowing(makeOilProperties()));

    public static final DeferredHolder<Block, OilFluidBlock> OIL_FLUID_BLOCK =
            FLUID_BLOCKS.register("oil", () -> new OilFluidBlock(
                    FOFluids.OIL_STILL,
                    BlockBehaviour.Properties.of()
                            .noCollission().strength(100f).noLootTable()
                            .liquid().replaceable()
            ));

    public static final DeferredHolder<Item, BucketItem> OIL_BUCKET =
            FLUID_ITEMS.register("oil_bucket", () -> new BucketItem(
                    FOFluids.OIL_STILL.get(),
                    new Item.Properties().stacksTo(1)
            ));

    private static BaseFlowingFluid.Properties makeOilProperties() {
        return new BaseFlowingFluid.Properties(
                OIL_TYPE,
                () -> FOFluids.OIL_STILL.get(),
                () -> FOFluids.OIL_FLOWING.get()
        ).bucket(() -> FOFluids.OIL_BUCKET.get())
                .block(() -> FOFluids.OIL_FLUID_BLOCK.get());
    }

    // -------------------------------------------------------------------------
    // Molten Steel
    // -------------------------------------------------------------------------

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_STEEL_TYPE =
            FLUID_TYPES.register("molten_steel", () -> new FluidType(
                    FluidType.Properties.create()
                            .density(2500).viscosity(8000).temperature(1500)
                            .canSwim(false).canDrown(false).supportsBoating(false)
                            .lightLevel(12)
            ) {
                @Override @OnlyIn(Dist.CLIENT)
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        private static final ResourceLocation STILL   = ResourceLocation.fromNamespaceAndPath(MODID, "block/molten_steel_still");
                        private static final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath(MODID, "block/molten_steel_flowing");
                        private static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(MODID, "block/molten_steel_overlay");
                        @Override public ResourceLocation getStillTexture()   { return STILL; }
                        @Override public ResourceLocation getFlowingTexture() { return FLOWING; }
                        @Override public ResourceLocation getOverlayTexture() { return OVERLAY; }
                        @Override public int getTintColor() { return 0xFFE8A030; }
                    });
                }
            });

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_STEEL_STILL =
            FLUIDS.register("molten_steel", () ->
                    new MoltenSteelFluid.Still(makeMoltenSteelProperties()));

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_STEEL_FLOWING =
            FLUIDS.register("flowing_molten_steel", () ->
                    new MoltenSteelFluid.Flowing(makeMoltenSteelProperties()));

    public static final DeferredHolder<Block, MoltenSteelFluidBlock> MOLTEN_STEEL_FLUID_BLOCK =
            FLUID_BLOCKS.register("molten_steel", () -> new MoltenSteelFluidBlock(
                    FOFluids.MOLTEN_STEEL_STILL,
                    BlockBehaviour.Properties.of()
                            .noCollission().strength(100f).noLootTable()
                            .liquid().replaceable().lightLevel(s -> 12)
            ));

    public static final DeferredHolder<Item, BucketItem> MOLTEN_STEEL_BUCKET =
            FLUID_ITEMS.register("molten_steel_bucket", () -> new BucketItem(
                    FOFluids.MOLTEN_STEEL_STILL.get(),
                    new Item.Properties().stacksTo(1)
            ));

    private static BaseFlowingFluid.Properties makeMoltenSteelProperties() {
        return new BaseFlowingFluid.Properties(
                MOLTEN_STEEL_TYPE,
                () -> FOFluids.MOLTEN_STEEL_STILL.get(),
                () -> FOFluids.MOLTEN_STEEL_FLOWING.get()
        ).bucket(() -> FOFluids.MOLTEN_STEEL_BUCKET.get())
                .block(() -> FOFluids.MOLTEN_STEEL_FLUID_BLOCK.get());
    }

    // -------------------------------------------------------------------------
    // Register
    // -------------------------------------------------------------------------

    public static void register(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        FLUID_BLOCKS.register(modEventBus);
        FLUID_ITEMS.register(modEventBus);
    }
}