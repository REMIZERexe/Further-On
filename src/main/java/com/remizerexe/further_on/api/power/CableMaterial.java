package com.remizerexe.further_on.api.power;

/**
 * Defines the physical properties of the conductive materials used in cables.
 */
public enum CableMaterial {
    COPPER(1.5f, VoltageTier.LV),
    GOLD(0.8f, VoltageTier.MV),
    SILVER(0.2f, VoltageTier.HV),
    SUPERCONDUCTOR(0.0f, VoltageTier.UHV); // Cryo-cooled late game wiring
    
    private final float resistanceOhms; // Determines power loss over distance
    private final VoltageTier maxSafeVoltage; // If exceeded, the wire burns up

    CableMaterial(float resistanceOhms, VoltageTier maxSafeVoltage) {
        this.resistanceOhms = resistanceOhms;
        this.maxSafeVoltage = maxSafeVoltage;
    }

    public float getResistance() {
        return resistanceOhms;
    }

    public VoltageTier getMaxSafeVoltage() {
        return maxSafeVoltage;
    }

    /**
     * Calculates the power loss across a specific distance (in blocks) using a simplified resistance model.
     * P_loss = I^2 * R. We return a multiplier for efficiency.
     */
    public float calculateEfficiency(int blockDistance) {
        if (resistanceOhms == 0.0f) return 1.0f; // Superconductors are perfect
        
        // Simple linear drop-off for gameplay purposes. 
        // 1.0 = 100% efficient, 0.0 = completely lost to heat
        float loss = (blockDistance * resistanceOhms) / 1000.0f;
        return Math.max(0.0f, 1.0f - loss);
    }
}
