package com.remizerexe.further_on.content.equipment;

import com.remizerexe.further_on.registry.FODataComponents;
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

    /** Whether the visor has been deliberately toggled open on this mask. */
    public static boolean isVisorOpen(ItemStack stack) {
        return stack.getOrDefault(FODataComponents.VISOR_OPEN.get(), false);
    }

    /** Visor physically up: toggled open, or a temporary crouch peek. */
    public static boolean isVisorUp(LivingEntity entity) {
        return isVisorOpen(entity.getItemBySlot(EquipmentSlot.HEAD)) || entity.isCrouching();
    }

    /**
     * Single source of truth for gameplay checks: the mask is worn with the
     * visor down. Works on both sides (the component and crouch state sync).
     */
    public static boolean isProtecting(LivingEntity entity) {
        return isWearing(entity) && !isVisorUp(entity);
    }

    public static void toggleVisor(LivingEntity entity) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (head.getItem() instanceof WeldingMaskItem)
            head.set(FODataComponents.VISOR_OPEN.get(), !isVisorOpen(head));
    }
}
