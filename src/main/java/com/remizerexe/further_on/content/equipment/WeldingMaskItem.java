package com.remizerexe.further_on.content.equipment;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WeldingMaskItem extends Item implements Equipable {

    public WeldingMaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return this.swapWithEquipmentSlot(this, level, player, hand);
    }

    public static boolean isWearing(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof WeldingMaskItem;
    }

    /**
     * The visor rests open by default and is only lowered while the wearer
     * holds crouch. The sneak flag syncs both ways, so this works on either side.
     */
    public static boolean isVisorOpen(LivingEntity entity) {
        return !entity.isShiftKeyDown();
    }

    /**
     * Single source of truth for gameplay checks: the mask is worn with the
     * visor down, i.e. the wearer is crouching.
     */
    public static boolean isProtecting(LivingEntity entity) {
        return isWearing(entity) && !isVisorOpen(entity);
    }
}
