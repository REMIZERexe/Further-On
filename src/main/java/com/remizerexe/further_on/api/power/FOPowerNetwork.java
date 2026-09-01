package com.remizerexe.further_on.api.power;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * The skeleton backend for the Independent Power System graph.
 * 
 * Instead of simple adjacency (like Mekanism cables), this will 
 * act as a global ticking graph that calculates Pathing and Resistance 
 * between all connected nodes (Generators, Transformers, Relays, Machines).
 */
public class FOPowerNetwork {
    private final Set<BlockPos> nodes = new HashSet<>();
    private final VoltageTier networkTier;
    private long totalCapacity;
    private long totalStored;

    public FOPowerNetwork(VoltageTier operatingTier) {
        this.networkTier = operatingTier;
    }

    public VoltageTier getTier() {
        return networkTier;
    }

    public void addNode(BlockPos pos, IFurtherEnergyStorage storage) {
        if (storage.getVoltageTier() != this.networkTier) {
            // Overstressing the network triggers an explosion!
            storage.blowUp();
            return;
        }
        nodes.add(pos);
    }

    public void removeNode(BlockPos pos) {
        nodes.remove(pos);
    }

    /**
     * Called every tick by the LevelTickEvent to balance power across all nodes.
     */
    public void tickNetwork(Level level) {
        if (nodes.isEmpty()) return;
        
        // TODO: A* Pathfinding to calculate resistance drops based on CableMaterial
        // TODO: Distribute power evenly across IFurtherEnergyStorage capabilities
        // TODO: Emit wire buzzing sounds if network throughput is near max
    }
    
    /**
     * Checks if the network is exceeding safe transfer limits.
     */
    public boolean isOverstressed(long currentThroughput) {
        return currentThroughput > networkTier.getMaxTransfer();
    }
}
