package com.remizerexe.further_on.multiblock;

import com.remizerexe.further_on.FurtherOn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all multiblock controller block entities.
 *
 * Handles:
 * - Structure validation (via subclass-provided MultiblockStructure)
 * - Formed/unformed state tracking and NBT persistence
 * - Client sync via vanilla block entity packet system
 *
 * Subclasses must implement:
 * - buildStructure(int capacityLayers) — define the pattern
 * - isCapacityLayer(BlockPos)          — detect repeating middle layers
 *
 * Subclasses may override:
 * - onFormed(int capacityLayers)       — called on unformed → formed transition
 * - onUnformed()                       — called on formed → unformed transition
 * - minCapacityLayers()                — default 1
 * - maxCapacityLayers()                — default 20
 */
public abstract class MultiblockControllerBE extends BlockEntity {

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    /** Tracks whether the structure is currently formed and the controller position. */
    protected MultiblockState multiblockState = new MultiblockState();

    /** Number of capacity (repeating middle) layers currently counted. */
    protected int capacityLayers = 0;

    /** World positions of all blocks in the formed structure, synced to client. */
    protected List<BlockPos> formedPositions = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public MultiblockControllerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // -------------------------------------------------------------------------
    // Abstract API — subclasses define these
    // -------------------------------------------------------------------------

    /**
     * Returns the full MultiblockStructure pattern for the given number of
     * capacity layers. Called during validation.
     */
    protected abstract MultiblockStructure buildStructure(int capacityLayers);

    /**
     * Returns true if the block at the given center position (directly above
     * the controller, offset by facing) qualifies as a capacity layer.
     * Used by countCapacityLayers() to determine structure height.
     */
    protected abstract boolean isCapacityLayer(BlockPos pos);

    protected int minCapacityLayers() { return 1; }
    protected int maxCapacityLayers() { return 20; }

    /** Called once when the structure transitions from unformed → formed. */
    protected void onFormed(int capacityLayers) { }

    /** Called once when the structure transitions from formed → unformed. */
    protected void onUnformed() { }

    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    /**
     * Validates the multiblock structure in the world.
     * Counts capacity layers, builds the pattern, checks every predicate.
     * Updates formed state and triggers onFormed/onUnformed as needed.
     * Syncs state to the client after every call.
     */
    public void revalidate() {
        if (level == null || level.isClientSide()) return;

        boolean wasFormed = multiblockState.isFormed();
        int count = countCapacityLayers();

        FurtherOn.LOGGER.warn("=== REVALIDATE === controller={} facing={} capacityLayers={}",
                worldPosition, getFacing(), count);

        if (count < minCapacityLayers() || count > maxCapacityLayers()) {
            FurtherOn.LOGGER.warn("  FAIL: capacity layers out of bounds ({} not in [{},{}])",
                    count, minCapacityLayers(), maxCapacityLayers());
            multiblockState.setFormed(false);
            formedPositions = new ArrayList<>();
            if (wasFormed) onUnformed();
            setChanged();
            syncToClient();
            return;
        }

        Direction facing = getFacing();
        MultiblockStructure structure = buildStructure(count);
        boolean nowFormed = structure.validate(level, worldPosition, facing);

        // Store world positions for client-side use (e.g. effects)
        formedPositions = nowFormed
                ? structure.getWorldPositions(worldPosition, facing)
                : new ArrayList<>();

        multiblockState.setFormed(nowFormed);
        multiblockState.setControllerPos(worldPosition);

        if (!wasFormed && nowFormed) {
            capacityLayers = count;
            onFormed(count);
        } else if (wasFormed && !nowFormed) {
            capacityLayers = 0;
            onUnformed();
        }

        setChanged();
        syncToClient();
    }

    /**
     * Scans upward from 2 blocks above the controller and counts consecutive
     * capacity layers, stopping at the first block that fails isCapacityLayer().
     */
    private int countCapacityLayers() {
        int count = 0;
        for (int i = 0; i < maxCapacityLayers(); i++) {
            BlockPos layerCenter = worldPosition.above(2 + i);
            boolean isLayer = isCapacityLayer(layerCenter);
            com.remizerexe.further_on.FurtherOn.LOGGER.warn(
                    "  capacityLayer check y+{} pos={} result={}", 2+i, layerCenter, isLayer
            );
            if (isLayer) {
                count++;
            } else {
                break;
            }
        }
        com.remizerexe.further_on.FurtherOn.LOGGER.warn("  total capacity layers: {}", count);
        return count;
    }

    /**
     * Returns the horizontal facing direction from the block state.
     * Falls back to NORTH if the block has no HORIZONTAL_FACING property.
     */
    public Direction getFacing() {
        BlockState state = getBlockState();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        return Direction.NORTH;
    }

    public boolean isFormed()        { return multiblockState.isFormed(); }
    public int getCapacityLayers()   { return capacityLayers; }

    // -------------------------------------------------------------------------
    // Client sync
    // -------------------------------------------------------------------------

    /**
     * Notifies the client that this block entity's data has changed.
     * Uses ServerLevel.blockChanged() which triggers getUpdatePacket()
     * on the client side.
     */
    protected void syncToClient() {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            net.minecraft.network.protocol.Packet<?> packet = getUpdatePacket();
            if (packet != null) {
                serverLevel.players().forEach(player -> {
                    if (player.distanceToSqr(worldPosition.getX(),
                            worldPosition.getY(), worldPosition.getZ()) < 64 * 64) {
                        player.connection.send(packet);
                    }
                });
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    // -------------------------------------------------------------------------
    // NBT
    // -------------------------------------------------------------------------

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("MultiblockState", multiblockState.save());
        tag.putInt("CapacityLayers", capacityLayers);

        // Save formed positions so the client can use them without re-scanning
        ListTag posList = new ListTag();
        for (BlockPos pos : formedPositions) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", pos.getX());
            posTag.putInt("y", pos.getY());
            posTag.putInt("z", pos.getZ());
            posList.add(posTag);
        }
        tag.put("FormedPositions", posList);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("MultiblockState")) {
            multiblockState = MultiblockState.load(tag.getCompound("MultiblockState"));
        }
        capacityLayers = tag.getInt("CapacityLayers");

        formedPositions = new ArrayList<>();
        if (tag.contains("FormedPositions")) {
            ListTag posList = tag.getList("FormedPositions", Tag.TAG_COMPOUND);
            for (int i = 0; i < posList.size(); i++) {
                CompoundTag posTag = posList.getCompound(i);
                formedPositions.add(new BlockPos(
                        posTag.getInt("x"),
                        posTag.getInt("y"),
                        posTag.getInt("z")
                ));
            }
        }
    }


}