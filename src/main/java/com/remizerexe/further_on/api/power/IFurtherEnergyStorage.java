package com.remizerexe.further_on.api.power;

import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Our custom wrapper around Forge Energy (FE).
 * This ensures machines can process standard FE, but are restricted by Voltage Tiers!
 */
public interface IFurtherEnergyStorage extends IEnergyStorage {
    
    /**
     * Gets the current voltage tier this machine/wire is operating at.
     */
    VoltageTier getVoltageTier();

    /**
     * Overload the machine! 
     * Called when a Transformer steps down incorrectly or HV is pushed into an LV node.
     */
    void blowUp();

    /**
     * Tries to receive power safely. 
     * If incoming voltage is strictly higher than what this node supports, blow up!
     */
    default int receivePowerSafely(int maxReceive, VoltageTier incomingTier, boolean simulate) {
        if (incomingTier.getMaxTransfer() > getVoltageTier().getMaxTransfer()) {
            if (!simulate) {
                blowUp();
            }
            return 0; // Boom.
        }
        return receiveEnergy(maxReceive, simulate);
    }
}
