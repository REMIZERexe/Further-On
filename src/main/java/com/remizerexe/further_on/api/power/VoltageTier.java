package com.remizerexe.further_on.api.power;

/**
 * Defines the strict Voltage Tiers for the Further On power grid.
 * Any mismatch in voltage (e.g., passing HV into an LV machine without a Transformer) 
 * should result in catastrophic failure (explosions).
 */
public enum VoltageTier {
    LV(256, 0.0f, "Low Voltage"),      // Safe to touch
    MV(1024, 2.0f, "Medium Voltage"),  // Will shock you
    HV(4096, 6.0f, "High Voltage"),    // Deadly
    UHV(16384, 14.0f, "Ultra High Voltage"); // Instant Vaporization
    
    private final int maxTransfer; // Max FE/t that can flow through this tier
    private final float shockDamage; // Damage dealt per tick if an entity touches an uninsulated wire
    private final String displayName;

    VoltageTier(int maxTransfer, float shockDamage, String displayName) {
        this.maxTransfer = maxTransfer;
        this.shockDamage = shockDamage;
        this.displayName = displayName;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public float getShockDamage() {
        return shockDamage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isLethal() {
        return this.shockDamage > 0;
    }
}
