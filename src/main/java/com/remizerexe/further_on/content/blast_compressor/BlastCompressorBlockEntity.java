package com.remizerexe.further_on.content.blast_compressor;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import com.remizerexe.further_on.registry.FOItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BlastCompressorBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    
    public ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public BlastCompressorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // We can add Create behaviors here later if needed (e.g. for goggle tooltips)
    }

    public boolean interact(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        ItemStack inSlot = inventory.getStackInSlot(0);
        
        if (held.isEmpty() && !inSlot.isEmpty()) {
            player.setItemInHand(hand, inSlot);
            inventory.setStackInSlot(0, ItemStack.EMPTY);
            level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
            return true;
        } else if (!held.isEmpty()) {
            // Attempt to insert into the slot (handles empty slots and merging matching stacks)
            ItemStack remainder = inventory.insertItem(0, held.copy(), false);
            if (remainder.getCount() != held.getCount()) {
                player.setItemInHand(hand, remainder);
                level.playSound(null, worldPosition, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                return true;
            }
        }
        return false;
    }

    public void onExplosionHit() {
        if (level == null || level.isClientSide) return;

        ItemStack inSlot = inventory.getStackInSlot(0);
        if (inSlot.isEmpty()) return;
        
        // Wrap our inventory for the recipe lookup
        net.neoforged.neoforge.items.wrapper.RecipeWrapper wrapper = new net.neoforged.neoforge.items.wrapper.RecipeWrapper(inventory);

        // Find matching blast compression recipe
net.minecraft.world.item.crafting.RecipeType<com.remizerexe.further_on.content.blast_compressor.recipe.FOBlastCompressingRecipe> recipeType =
                com.remizerexe.further_on.registry.FORecipeTypes.BLAST_COMPRESSING.getType();
        var recipe = level.getRecipeManager().getRecipeFor(recipeType, wrapper, level);

        if (recipe.isPresent()) {
            // Apply the recipe to the entire stack!
            int amountToProcess = inventory.getStackInSlot(0).getCount();
            // Extract all inputs safely
            inventory.extractItem(0, amountToProcess, false);
            
            // Process each item individually to respect Create's percentage-based secondary outputs!
            for (int j = 0; j < amountToProcess; j++) {
                List<ItemStack> rolledResults = ((com.remizerexe.further_on.content.blast_compressor.recipe.FOBlastCompressingRecipe) recipe.get().value()).rollResults(level.random);
                for (ItemStack resultStack : rolledResults) {
                    if (resultStack.isEmpty()) continue;
                    
                    net.minecraft.world.entity.item.ItemEntity outputEntity = new net.minecraft.world.entity.item.ItemEntity(
                            level,
                            worldPosition.getX() + 0.5,
                            worldPosition.getY() + 1.2,
                            worldPosition.getZ() + 0.5,
                            resultStack.copy()
                    );
                    // Give it a little pop upwards
                    outputEntity.setDeltaMovement(0, 0.4, 0);
                    level.addFreshEntity(outputEntity);
                }
            }
            
            level.playSound(null, worldPosition, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 0.5f);
            
            // Spawn some cool particles indicating successful compression
            for (int i = 0; i < 20; i++) {
                double d0 = level.random.nextGaussian() * 0.02D;
                double d1 = level.random.nextGaussian() * 0.02D;
                double d2 = level.random.nextGaussian() * 0.02D;
                if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CRIT, 
                        worldPosition.getX() + 0.5, 
                        worldPosition.getY() + 1.0, 
                        worldPosition.getZ() + 0.5, 
                        1, d0, d1, d2, 0.1);
                }
            }
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ItemStack inSlot = inventory.getStackInSlot(0);
        
        // Add some nice formatting like standard Create machines
        tooltip.add(Component.literal(" ")
                .append(Component.literal("Blast Compressor").withStyle(net.minecraft.ChatFormatting.GOLD)));
                
        if (inSlot.isEmpty()) {
            tooltip.add(Component.literal("   ")
                    .append(Component.literal("Empty. Waiting for input.").withStyle(net.minecraft.ChatFormatting.GRAY)));
        } else {
            tooltip.add(Component.literal("   ")
                    .append(Component.literal("Loaded: ").withStyle(net.minecraft.ChatFormatting.GRAY))
                    .append(inSlot.getHoverName().copy().withStyle(net.minecraft.ChatFormatting.WHITE)));
            tooltip.add(Component.literal("   ")
                    .append(Component.literal("Status: Awaiting Explosion!").withStyle(net.minecraft.ChatFormatting.RED)));
        }
        return true;
    }
}
