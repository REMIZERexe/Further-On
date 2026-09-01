package com.remizerexe.further_on.content.power;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.remizerexe.further_on.api.power.CableMaterial;
import com.remizerexe.further_on.api.power.VoltageTier;

import java.util.List;

public class LiveWireBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private final CableMaterial material;

    public LiveWireBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        // Defaulting to copper for the sketchy live wire prototype
        this.material = CableMaterial.COPPER; 
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        // Required by SmartBlockEntity
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal(" ")
                .append(Component.literal("Power Grid Connection").withStyle(net.minecraft.ChatFormatting.GOLD)));
        
        tooltip.add(Component.literal("   ")
                .append(Component.literal("Material: ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal(material.name()).withStyle(net.minecraft.ChatFormatting.AQUA)));
                
        tooltip.add(Component.literal("   ")
                .append(Component.literal("Max Safe Voltage: ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal(material.getMaxSafeVoltage().getDisplayName()).withStyle(net.minecraft.ChatFormatting.YELLOW)));
                
        tooltip.add(Component.literal("   ")
                .append(Component.literal("Internal Resistance: ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal(material.getResistance() + " Ohms").withStyle(net.minecraft.ChatFormatting.RED)));
                
        // In the future we will dynamically display the current FE throughput here!
        tooltip.add(Component.literal("   ")
                .append(Component.literal("Network Status: ").withStyle(net.minecraft.ChatFormatting.GRAY))
                .append(Component.literal("Stable").withStyle(net.minecraft.ChatFormatting.GREEN)));
        
        return true;
    }
}
