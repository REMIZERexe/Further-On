package com.remizerexe.further_on.registry;

import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.remizerexe.further_on.FurtherOn.MODID;

public class FOMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<BlastFurnaceHearthMenu>> BLAST_FURNACE_HEARTH =
            MENU_TYPES.register("blast_furnace_hearth",
                    () -> IMenuTypeExtension.create(BlastFurnaceHearthMenu::new));

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}