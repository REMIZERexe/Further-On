package com.remizerexe.further_on.content.oil;

import com.mojang.serialization.Codec;
import com.remizerexe.further_on.FurtherOn;
import com.remizerexe.further_on.registry.FOBlocks;
import com.remizerexe.further_on.registry.FOFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class OilClusterFeature extends Feature<NoneFeatureConfiguration> {

    public OilClusterFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Find surface at origin
        BlockPos surface = findSurface(level, origin);
        if (surface == null) return false;

        // Don't generate cluster if origin is in water
        if (level.getBlockState(surface.below()).getFluidState()
                .is(net.minecraft.world.level.material.Fluids.WATER)) {
            return false;
        }

        // Place 2-4 puddles near each other
        int puddleCount = 2 + random.nextInt(3);
        BlockPos clumpCenter = surface;

        for (int i = 0; i < puddleCount; i++) {
            int offsetX = random.nextInt(41) - 20;
            int offsetZ = random.nextInt(41) - 20;
            BlockPos puddleOrigin = surface.offset(offsetX, 0, offsetZ);
            BlockPos puddleSurface = findSurface(level, puddleOrigin);
            if (puddleSurface != null) {
                // Skip if the surface or the block below is water
                BlockState surfaceState = level.getBlockState(puddleSurface.below());
                if (surfaceState.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER)) {
                    continue;
                }
                placePuddle(level, puddleSurface, random);
            }
        }

        // Place one pocket underneath the clump center
        placePocket(level, clumpCenter, random);

        return true;
    }

    // -------------------------------------------------------------------------
// Puddle
// -------------------------------------------------------------------------

    private void placePuddle(WorldGenLevel level, BlockPos surface, RandomSource random) {
        surface = surface.below(1);
        int radius = 2 + random.nextInt(3);
        float squish = 0.6f + random.nextFloat() * 0.8f;
        float skewX = (random.nextFloat() - 0.5f) * 0.4f;

        BlockState oil = FOFluids.OIL_FLUID_BLOCK.get().defaultBlockState();
        BlockState mud = Blocks.MUD.defaultBlockState();

        for (int x = -radius - 1; x <= radius + 1; x++) {
            for (int z = -radius - 1; z <= radius + 1; z++) {
                double dx = x + skewX * z;
                double dz = z * squish;
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > radius) continue;

                BlockPos surfaceCheck = surface.offset(x, 1, z);
                BlockState above = level.getBlockState(surfaceCheck);
                if (above.is(net.minecraft.tags.BlockTags.LOGS)
                        || above.is(net.minecraft.tags.BlockTags.LEAVES)) continue;

                int colSeed = x * 341 + z * 773;
                float depthNoise = (((colSeed ^ (colSeed >> 7)) & 0xFF) / 255f);
                int depth = (int) (1 + (radius - dist) * (0.5f + depthNoise * 0.8f));

                for (int y = -depth; y <= 0; y++) {
                    BlockPos pos = surface.offset(x, y, z);
                    if (!level.ensureCanWrite(pos)) continue;
                    BlockState current = level.getBlockState(pos);
                    if (canReplace(current)) {
                        level.setBlock(pos, oil, 3);
                    }
                }
            }
        }

        // ── Boue sur les bords ───────────────────────────────────────────────────
        int mudRadius = radius + 2;
        for (int x = -mudRadius; x <= mudRadius; x++) {
            for (int z = -mudRadius; z <= mudRadius; z++) {
                double dx = x + skewX * z;
                double dz = z * squish;
                double dist = Math.sqrt(dx * dx + dz * dz);

                // Seulement dans l'anneau entre radius et mudRadius
                if (dist <= radius || dist > mudRadius) continue;

                // Sparse — environ 1 bloc sur 3
                if (random.nextInt(3) != 0) continue;

                // Dans la passe boue, remplace le bloc pos par :
                BlockPos mudPos = surface.offset(x, 0, z);

                // Descend jusqu'au premier bloc solide
                for (int drop = 0; drop < 4; drop++) {
                    BlockState below = level.getBlockState(mudPos.below());
                    if (below.isAir() || below.liquid() || below.is(net.minecraft.tags.BlockTags.REPLACEABLE)) {
                        mudPos = mudPos.below();
                    } else {
                        break;
                    }
                }

                if (!level.ensureCanWrite(mudPos)) continue;
                BlockState current = level.getBlockState(mudPos);
                if (current.isAir() || current.liquid()) continue;
                if (current.is(net.minecraft.tags.BlockTags.REPLACEABLE)) continue;
                if (current.is(net.minecraft.tags.BlockTags.FLOWERS)) continue;
                if (current.is(Blocks.SNOW) || current.is(Blocks.SNOW_BLOCK)) continue;
                if (!canReplace(current)) continue;
                level.setBlock(mudPos, mud, 3);
            }
        }
    }

// -------------------------------------------------------------------------
// Pocket
// -------------------------------------------------------------------------

    private void placePocket(WorldGenLevel level, BlockPos surface, RandomSource random) {
        int pocketY = -20 + random.nextInt(30); // -20 à +10
        BlockPos center = new BlockPos(surface.getX(), pocketY, surface.getZ());

        // Rayon variable + étirement vertical aléatoire
        int radiusH = 6 + random.nextInt(7);  // 6-12 horizontal
        int radiusV = 3 + random.nextInt(5);  // 3-7 vertical
        float skewX = (random.nextFloat() - 0.5f) * 3f;
        float skewZ = (random.nextFloat() - 0.5f) * 3f;

        BlockState oil = FOFluids.OIL_FLUID_BLOCK.get().defaultBlockState();

        for (int x = -radiusH; x <= radiusH; x++) {
            for (int y = -radiusV; y <= radiusV; y++) {
                for (int z = -radiusH; z <= radiusH; z++) {
                    double dx = x + skewX * (y / (float) radiusV);
                    double dz = z + skewZ * (y / (float) radiusV);
                    double dist = Math.sqrt(
                            (dx * dx) / (radiusH * radiusH) +
                                    (y * y) / (radiusV * radiusV) +
                                    (dz * dz) / (radiusH * radiusH)
                    );
                    if (dist > 1.0) continue;

                    BlockPos pos = center.offset(x, y, z);
                    if (!level.ensureCanWrite(pos)) continue;

                    BlockState current = level.getBlockState(pos);
                    if (current.is(Blocks.STONE) || current.is(Blocks.DEEPSLATE)
                            || current.is(Blocks.TUFF) || current.is(Blocks.ANDESITE)
                            || current.is(Blocks.DIORITE) || current.is(Blocks.GRANITE)
                            || current.isAir()) {
                        level.setBlock(pos, oil, 3);
                    }
                }
            }
        }

        // Node cluster — taille et position variables
        int nodeRadius = 1 + random.nextInt(3); // 1-3
        int nodeOffsetX = random.nextInt(radiusH / 2) - radiusH / 4;
        int nodeOffsetZ = random.nextInt(radiusH / 2) - radiusH / 4;
        BlockPos nodeCenter = center
                .offset(nodeOffsetX, -radiusV / 2, nodeOffsetZ);

        for (int x = -nodeRadius; x <= nodeRadius; x++) {
            for (int y = -nodeRadius; y <= nodeRadius; y++) {
                for (int z = -nodeRadius; z <= nodeRadius; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    if (dist > nodeRadius) continue;

                    BlockPos pos = nodeCenter.offset(x, y, z);
                    if (!level.ensureCanWrite(pos)) continue;

                    if (level.getBlockState(pos).getFluidState()
                            .is(FOFluids.OIL_STILL.get())) {
                        level.setBlock(pos, pickNode(random), 3);
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private BlockPos findSurface(WorldGenLevel level, BlockPos origin) {
        // Utilise la heightmap pour trouver la vraie surface
        int surfaceY = level.getHeight(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG,
                origin.getX(), origin.getZ()
        );

        if (surfaceY <= 0) return null;

        BlockPos surface = new BlockPos(origin.getX(), surfaceY - 1, origin.getZ());
        BlockState found = level.getBlockState(surface);

        if (found.is(net.minecraft.tags.BlockTags.LOGS)
                || found.is(net.minecraft.tags.BlockTags.LEAVES)) {
            return null;
        }

        return surface.above();
    }

private boolean canReplace(BlockState state) {
    if (state.is(Blocks.BEDROCK)) return false;
    if (state.is(Blocks.OBSIDIAN)) return false;
    if (state.isAir()) return false; // ← ne remplace pas l'air
    if (state.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER)) return false;
    if (state.getFluidState().is(net.minecraft.world.level.material.Fluids.LAVA)) return false;
    return true;
}

    private BlockState pickNode(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 50) return FOBlocks.OIL_NODE_POOR.get().defaultBlockState();
        if (roll < 85) return FOBlocks.OIL_NODE_NORMAL.get().defaultBlockState();
        return FOBlocks.OIL_NODE_RICH.get().defaultBlockState();
    }
}