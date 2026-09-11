package com.remizerexe.further_on.content.cementation;

import com.simibubi.create.AllDataComponents;
import net.createmod.catnip.theme.Color;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Transitional item for the blister steel -> carbon steel sequenced assembly.
 * Behaves like Create's {@code SequencedAssemblyItem} (progress bar driven by the
 * sequenced assembly data component) but keeps the default stack size so partially
 * worked ingots at the same step stack together.
 */
public class UnfinishedSteelItem extends Item {

    public UnfinishedSteelItem(Properties properties) {
        super(properties);
    }

    public float getProgress(ItemStack stack) {
        if (!stack.has(AllDataComponents.SEQUENCED_ASSEMBLY))
            return 0;
        return stack.get(AllDataComponents.SEQUENCED_ASSEMBLY).progress();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(getProgress(stack) * 13.0f);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Color.mixColors(0xFFC074, 0xFF46FFE0, getProgress(stack));
    }
}
