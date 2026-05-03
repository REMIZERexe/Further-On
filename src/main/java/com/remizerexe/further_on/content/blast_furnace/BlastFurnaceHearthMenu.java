package com.remizerexe.further_on.content.blast_furnace;

import com.remizerexe.further_on.registry.FOMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BlastFurnaceHearthMenu extends AbstractContainerMenu {

    private final BlastFurnaceHearthBlockEntity blockEntity;
    private final ContainerData data;

    // Indices dans le ContainerData
    private static final int IDX_ACCUMULATED  = 0;
    private static final int IDX_MAX_LAYERS   = 1;
    private static final int IDX_STEEL_AMOUNT = 2;
    private static final int IDX_STEEL_CAP    = 3;
    private static final int IDX_RPM          = 4;
    private static final int IDX_PROGRESS     = 5;
    private static final int DATA_COUNT       = 6;

    // Constructeur réseau (client)
    public BlastFurnaceHearthMenu(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv, (BlastFurnaceHearthBlockEntity)
                playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    // Constructeur serveur
    public BlastFurnaceHearthMenu(int windowId, Inventory playerInv,
                                  BlastFurnaceHearthBlockEntity be) {
        super(FOMenuTypes.BLAST_FURNACE_HEARTH.get(), windowId);
        this.blockEntity = be;

        // ContainerData — sync auto serveur → client
        if (be != null) {
            this.data = new ContainerData() {
                @Override
                public int get(int index) {
                    return switch (index) {
                        case IDX_ACCUMULATED  -> be.getAccumulatedLayers();
                        case IDX_MAX_LAYERS   -> (be.getCapacityLayers()+2) * 8;
                        case IDX_STEEL_AMOUNT -> be.outputInventory.getStackInSlot(0).getCount();
                        case IDX_STEEL_CAP    -> be.outputInventory.getSlotLimit(0);
                        case IDX_RPM          -> be.getCurrentRPM();
                        case IDX_PROGRESS     -> (int)(be.getProcessingProgress() * 100);
                        default -> 0;
                    };
                }
                @Override
                public void set(int index, int value) {}
                @Override
                public int getCount() { return DATA_COUNT; }
            };
        } else {
            this.data = new SimpleContainerData(DATA_COUNT);
        }

        addDataSlots(data);

        // Slot slag
        if (be != null)
            addSlot(new SlotItemHandler(be.slagInventory, 0, 8, 26));

        // Inventaire joueur
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInv, col + row * 9 + 9,
                        8 + col * 18, 130 + row * 18));

        // Hotbar
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInv, col, 8 + col * 18, 188));
    }

    // Getters — lisent le ContainerData (synced)
    public int getAccumulatedLayers() { return data.get(IDX_ACCUMULATED); }
    public int getMaxLayers()         { return data.get(IDX_MAX_LAYERS); }
    public int getSteelAmount()       { return data.get(IDX_STEEL_AMOUNT); }
    public int getSteelCapacity()     { return data.get(IDX_STEEL_CAP); }
    public int getCurrentRPM()        { return data.get(IDX_RPM); }
    public int getProgressPercent()   { return data.get(IDX_PROGRESS); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && blockEntity.isFormed();
    }
}