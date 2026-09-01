package com.remizerexe.further_on.content.equipment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;

public class HazardBootsItem extends ArmorItem {

    public HazardBootsItem(Properties properties) {
        // Using CHAIN as a base holder for balanced mid-game armor stats
        super(ArmorMaterials.CHAIN, Type.BOOTS, properties);
    }

    /**
     * Native NeoForge method to allow walking on powder snow.
     * Normally reserved for Leather Boots, but our Hazard Boots are thick enough!
     */
    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return true;
    }
}
