package com.remizerexe.further_on.api.power;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import com.remizerexe.further_on.FurtherOn;

public class PowerDamageHandler {
    
    // Custom Damage Type for Electrocution
    public static final ResourceKey<net.minecraft.world.damagesource.DamageType> ELECTROCUTION = 
        ResourceKey.create(Registries.DAMAGE_TYPE, FurtherOn.asResource("electrocution"));

    /**
     * Attempts to electrocute an entity that steps on or touches an uninsulated wire.
     * Takes rubber boots into account!
     */
    public static void shockEntity(Entity entity, VoltageTier voltage, Level level) {
        if (!(entity instanceof LivingEntity living)) return;
        
        // BUG FIX: Creative and Spectator players should completely ignore wire shocks, 
        // otherwise they still get hit with the Weakness debuff even if hurt() cancels the damage!
        if (living instanceof net.minecraft.world.entity.player.Player player && (player.isCreative() || player.isSpectator())) return;

        if (!voltage.isLethal() || level.isClientSide()) return;

        // Check for rubber boots (insulation). If they have insulated boots, they don't get shocked from stepping.
        boolean isInsulated = false;
        
        for (ItemStack armor : living.getArmorSlots()) {
            if (armor.is(com.remizerexe.further_on.registry.FOItems.hazard_boots.get())) {
                isInsulated = true;
                break;
            }
        }

        if (!isInsulated) {
            // Apply the shock
            // BUG FIX: Custom damage types require a generated datapack in 1.21+. 
            // Calling getHolderOrThrow on an unregistered type instantly crashes the server.
            // Using the native LIGHTNING_BOLT damage type guarantees stability and still bypasses armor appropriately.
            DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(net.minecraft.world.damagesource.DamageTypes.LIGHTNING_BOLT));
            living.hurt(source, voltage.getShockDamage());
            
            // Apply a nasty slowness or weakness effect here because they just got zapped
            living.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 60, 1));
        }
    }
}
