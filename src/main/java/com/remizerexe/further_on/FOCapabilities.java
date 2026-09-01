package com.remizerexe.further_on;

import com.remizerexe.further_on.content.blast_furnace.BlastFurnaceHearthBlockEntity;
import com.remizerexe.further_on.registry.FOBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

import static com.remizerexe.further_on.FurtherOn.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class FOCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Hearth — steel output on front face
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FOBlockEntities.BLAST_FURNACE_HEARTH.get(),
                (be, face) -> {
                    if (face == null) return null;
                    return be.getInventoryForFace(face);
                }
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                FOBlockEntities.BLAST_FURNACE_HATCH.get(),
                (be, face) -> {
                    if (face != net.minecraft.core.Direction.DOWN) return null;
                    ItemStackHandler slag = be.getSlagInventory();
                    return slag;
                }
        );
    }
}