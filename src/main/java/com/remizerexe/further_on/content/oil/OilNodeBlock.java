package com.remizerexe.further_on.content.oil;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Infinite oil source block. Three variants: poor, normal, rich.
 * Extraction rate is determined by which variant is placed.
 * The block itself is inert — extraction requires a pump jack (TODO).
 */
public class OilNodeBlock extends Block {

    public enum Richness { POOR, NORMAL, RICH }

    private final Richness richness;

    public OilNodeBlock(Richness richness, Properties properties) {
        super(properties);
        this.richness = richness;
    }

    public Richness getRichness() { return richness; }

    /** mB of oil produced per pump jack operation. */
    public int getYieldPerOperation() {
        return switch (richness) {
            case POOR   -> 50;
            case NORMAL -> 150;
            case RICH   -> 400;
        };
    }
}