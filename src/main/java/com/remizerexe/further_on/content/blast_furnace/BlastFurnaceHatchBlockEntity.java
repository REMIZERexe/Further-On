package com.remizerexe.further_on.content.blast_furnace;

import com.remizerexe.further_on.multiblock.MultiblockComponentBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Block entity for the blast furnace base center block.
 * Delegates slag extraction to the hearth controller BE via capability.
 */
public class BlastFurnaceHatchBlockEntity extends MultiblockComponentBE {

    public BlastFurnaceHatchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public ItemStackHandler getSlagInventory() {
        BlastFurnaceHearthBlockEntity hearth =
                getController(BlastFurnaceHearthBlockEntity.class);
        if (hearth == null || !hearth.isFormed()) return null;
        return hearth.slagInventory;
    }

}