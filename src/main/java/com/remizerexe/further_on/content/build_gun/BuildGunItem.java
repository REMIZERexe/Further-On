package com.remizerexe.further_on.content.build_gun;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

public class BuildGunItem extends Item {

    public BuildGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);
        
        if (level.isClientSide()) {
            return InteractionResultHolder.success(gun);
        }
        
        // Placeholder math for the blueprint deployment logic
        // 1. Check offhand for a SchematicItem (from Create)
        // 2. Read schematic NBT and structure template bounds
        // 3. Find the exact [X, Y, Z] block mapping in the structure template
        // 4. Query the player's standard inventory AND NeoForge IItemHandler capabilities
        //    -> By checking IItemHandler on the player's equipped items, we natively 
        //       support pulling from Sophisticated Backpacks, Backpacked, and Curios!
        // 5. If block is found in backpack/inventory -> extract 1 -> place in world -> play Create cannon sound
        // 6. If not found -> skip to the next available block
        
        player.displayClientMessage(Component.literal("Build Gun Fired! (Scanning Backpacks & Inventory for blocks...)"), true);
        
        // Return consume so it swings the arm
        return InteractionResultHolder.consume(gun);
    }
}
