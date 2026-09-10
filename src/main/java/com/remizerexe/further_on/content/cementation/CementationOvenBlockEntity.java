package com.remizerexe.further_on.content.cementation;

import com.remizerexe.further_on.content.cementation.recipe.CementationRecipe;
import com.remizerexe.further_on.multiblock.JsonMultiblockDefinition;
import com.remizerexe.further_on.multiblock.MultiblockControllerBE;
import com.remizerexe.further_on.multiblock.MultiblockJsonLoader;
import com.remizerexe.further_on.multiblock.MultiblockStructure;
import com.remizerexe.further_on.registry.FOBlocks;
import com.remizerexe.further_on.registry.FORecipeTypes;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CementationOvenBlockEntity extends MultiblockControllerBE implements IHaveGoggleInformation {

    private static final JsonMultiblockDefinition DEFINITION =
            MultiblockJsonLoader.load("further_on", "cementation_oven");

    private static final int STRUCTURE_CHECK_INTERVAL = 20;
    private static final int ABSORB_INTERVAL = 5;
    private static final int SYNC_INTERVAL = 20;

    public static final int MAX_INGOTS = 9;
    public static final int COKE_CAPACITY = 9;

    public static final float DOOR_OPEN_ANGLE = 100f;
    private static final float DOOR_SWING_PER_TICK = 8f;

    /** Raw ingots waiting to fire. */
    public final ItemStackHandler contents = new ItemStackHandler(1) {
        @Override public int getSlotLimit(int slot) { return MAX_INGOTS; }
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    /** Finished output waiting to be collected, one slot per recipe result. */
    public final ItemStackHandler outputs = new ItemStackHandler(CementationRecipe.MAX_OUTPUTS) {
        @Override public int getSlotLimit(int slot) { return Integer.MAX_VALUE; }
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    private int coke = 0;
    private boolean lit = false;
    private boolean finished = false;
    private int progress = 0;
    private int firingTicks = 0;

    private boolean doorOpen = false;
    private float doorAngle = 0f;
    private float prevDoorAngle = 0f;

    public CementationOvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // -------------------------------------------------------------------------
    // Multiblock
    // -------------------------------------------------------------------------

    @Override
    protected MultiblockStructure buildStructure(int capacityLayers) {
        return DEFINITION.buildStructure(capacityLayers);
    }

    /** The oven has a fixed height; there are no repeating layers. */
    @Override
    protected boolean isCapacityLayer(BlockPos pos) { return false; }

    @Override protected int minCapacityLayers() { return DEFINITION.getMinCapacityLayers(); }
    @Override protected int maxCapacityLayers() { return DEFINITION.getMaxCapacityLayers(); }

    @Override
    protected void onUnformed() { snuff(); }

    // -------------------------------------------------------------------------
    // Geometry (oven-relative: right, up, forward)
    // -------------------------------------------------------------------------

    /** Pit cells under the ring, back row to front row, left to right. */
    public List<BlockPos> pitPositions() {
        List<BlockPos> cells = new ArrayList<>(9);
        for (int forward = 2; forward >= 0; forward--)
            for (int right = -1; right <= 1; right++)
                cells.add(toWorld(right, -1, forward));
        return cells;
    }

    /** Bottom cell of the hollow column, directly behind the door window. */
    public BlockPos chimneyFloor() { return toWorld(0, 0, 1); }

    /** Cell just above the stairs crown, where smoke rises from. */
    public BlockPos chimneyTop() { return toWorld(0, 3, 1); }

    /** The pit centre and the hollow column above it. */
    public AABB absorbBounds() {
        return new AABB(toWorld(0, -1, 1)).minmax(new AABB(toWorld(0, 2, 1)));
    }

    // -------------------------------------------------------------------------
    // Ticking
    // -------------------------------------------------------------------------

    public void tick() {
        if (level == null || level.isClientSide() || !isFormed()) return;

        // Re-check the physical structure so a broken oven stops running.
        if (level.getGameTime() % STRUCTURE_CHECK_INTERVAL == 0) {
            revalidate();
            if (!isFormed()) return;
        }

        long time = level.getGameTime();
        boolean changed = false;
        if (!finished && !lit && time % ABSORB_INTERVAL == 0) changed |= absorbDroppedItems();

        if (lit && isSealed() && hasIngots() && !finished) {
            if (firingTicks <= 0) {
                firingTicks = findRecipe(level, contents.getStackInSlot(0))
                        .map(r -> r.value().getFiringTicks())
                        .orElse(CementationRecipe.DEFAULT_DURATION);
            }
            progress++;
            if (progress >= firingTicks) {
                completeBatch();
                changed = true;
            }
        }

        if (changed || time % SYNC_INTERVAL == 0) {
            setChanged();
            syncToClient();
        }
    }

    public void clientTick() {
        tickDoorAnimation();
        if (!isFormed() || !lit || level == null) return;
        BlockPos top = chimneyTop();
        double x = top.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6;
        double z = top.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.6;
        if (level.random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, top.getY() + 0.1, z, 0, 0.06, 0);
        }
        if (level.random.nextInt(6) == 0) {
            level.addParticle(ParticleTypes.FLAME, x, top.getY() - 0.4, z, 0, 0.03, 0);
        }
    }

    /** Pulls matching item entities out of the chimney. Returns true if anything was taken. */
    private boolean absorbDroppedItems() {
        boolean absorbed = false;
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, absorbBounds())) {
            ItemStack stack = entity.getItem();
            int taken = insert(stack);
            if (taken <= 0) continue;
            stack.shrink(taken);
            if (stack.isEmpty()) entity.discard();
            else entity.setItem(stack);
            absorbed = true;
        }
        if (absorbed) {
            level.playSound(null, chimneyFloor(), SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 0.6f, 0.9f);
        }
        return absorbed;
    }

    /**
     * Adds as much of the stack as fits: coke blocks up to nine, or recipe
     * ingots of a single type up to nine. Refused while lit or finished.
     *
     * @return items taken; the caller shrinks the source stack
     */
    public int insert(ItemStack stack) {
        if (lit || finished || stack.isEmpty()) return 0;

        if (stack.is(FOBlocks.COKE_BLOCK.asItem())) {
            int taken = Math.min(COKE_CAPACITY - coke, stack.getCount());
            if (taken <= 0) return 0;
            coke += taken;
            setChanged();
            return taken;
        }

        ItemStack current = contents.getStackInSlot(0);
        if (current.isEmpty()) {
            if (findRecipe(level, stack).isEmpty()) return 0;
        } else if (!ItemStack.isSameItemSameComponents(current, stack)) {
            return 0;
        }
        int taken = Math.min(MAX_INGOTS - current.getCount(), stack.getCount());
        if (taken <= 0) return 0;

        if (current.isEmpty()) contents.setStackInSlot(0, stack.copyWithCount(taken));
        else contents.setStackInSlot(0, current.copyWithCount(current.getCount() + taken));
        return taken;
    }

    /** Nine coke blocks and the door shut: the only state in which the batch burns down. */
    public boolean isSealed() {
        return hasFuel() && !doorOpen;
    }

    /** Nine coke blocks loaded, whether or not the door is shut. */
    public boolean hasFuel() {
        return coke >= COKE_CAPACITY;
    }

    /** True if the loaded ingots make up at least one full batch of their recipe. */
    public boolean hasFullBatch() {
        ItemStack input = contents.getStackInSlot(0);
        if (input.isEmpty() || level == null) return false;
        return findRecipe(level, input).map(r -> input.getCount() >= r.value().getInputCount()).orElse(false);
    }

    /** Ingots the loaded recipe wants per batch, or 0 if nothing is loaded. */
    private int requiredIngots() {
        ItemStack input = contents.getStackInSlot(0);
        if (input.isEmpty() || level == null) return 0;
        return findRecipe(level, input).map(r -> r.value().getInputCount()).orElse(0);
    }

    /**
     * Lights the oven. Needs the door open to reach the fire, a full coke
     * load and a full batch of ingots to fire. Server side.
     */
    public boolean tryIgnite() {
        if (!isFormed() || lit || finished || !doorOpen || !hasFuel() || !hasFullBatch()) return false;
        lit = true;
        setChanged();
        syncToClient();
        return true;
    }

    /** Why {@link #tryIgnite} would refuse, for the action bar. */
    private Component ignitionProblem() {
        if (lit) return Component.literal("Already lit");
        if (finished) return Component.literal("Already fired, collect the output");
        if (!doorOpen) return Component.literal("Open the door to light it");
        if (!hasIngots()) return Component.literal("Nothing to fire");
        if (!hasFullBatch()) return Component.literal("Needs " + requiredIngots() + " ingots for a batch");
        if (!hasFuel()) return Component.literal("Needs 9 coke blocks");
        return Component.literal("Cannot light");
    }

    private void snuff() {
        if (!lit) return;
        lit = false;
        setChanged();
        if (level != null && !level.isClientSide()) syncToClient();
    }

    private void completeBatch() {
        ItemStack input = contents.getStackInSlot(0);
        Optional<RecipeHolder<CementationRecipe>> recipe = findRecipe(level, input);
        lit = false;
        progress = 0;
        firingTicks = 0;
        if (recipe.isEmpty()) return;

        CementationRecipe value = recipe.get().value();
        int batches = input.getCount() / value.getInputCount();
        if (batches <= 0) return;

        for (int batch = 0; batch < batches; batch++) {
            for (ItemStack rolled : value.rollBatchOutputs(level.random)) addOutput(rolled);
        }
        int leftover = input.getCount() - batches * value.getInputCount();
        contents.setStackInSlot(0, leftover > 0 ? input.copyWithCount(leftover) : ItemStack.EMPTY);
        coke = 0;
        finished = true;

        BlockPos top = chimneyTop();
        level.playSound(null, top, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 0.6f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    top.getX() + 0.5, top.getY() + 0.5, top.getZ() + 0.5,
                    40, 0.4, 0.6, 0.4, 0.02);
        }
    }

    /** Merges a result into the output slots, stacking with a matching slot first. */
    private void addOutput(ItemStack stack) {
        if (stack.isEmpty()) return;
        for (int i = 0; i < outputs.getSlots(); i++) {
            ItemStack existing = outputs.getStackInSlot(i);
            if (ItemStack.isSameItemSameComponents(existing, stack)) {
                outputs.setStackInSlot(i, existing.copyWithCount(existing.getCount() + stack.getCount()));
                return;
            }
        }
        for (int i = 0; i < outputs.getSlots(); i++) {
            if (outputs.getStackInSlot(i).isEmpty()) {
                outputs.setStackInSlot(i, stack.copy());
                return;
            }
        }
        // No free slot: drop it rather than lose it.
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), stack.copy());
    }

    // -------------------------------------------------------------------------
    // Door
    // -------------------------------------------------------------------------

    public boolean isDoorOpen() { return doorOpen; }

    /** Server side: flips the door and pushes the new state to clients. */
    public void toggleDoor() {
        setDoorOpen(!doorOpen);
    }

    public void setDoorOpen(boolean open) {
        if (doorOpen == open) return;
        doorOpen = open;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition,
                    open ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE,
                    SoundSource.BLOCKS, 0.8f, 0.7f);
            syncToClient();
        }
    }

    /** Interpolated swing angle in degrees for the renderer. */
    public float getDoorAngle(float partialTick) {
        return prevDoorAngle + (doorAngle - prevDoorAngle) * partialTick;
    }

    /** Client side: eases the door towards its target each tick. */
    private void tickDoorAnimation() {
        prevDoorAngle = doorAngle;
        float target = doorOpen ? DOOR_OPEN_ANGLE : 0f;
        if (doorAngle < target) doorAngle = Math.min(target, doorAngle + DOOR_SWING_PER_TICK);
        else if (doorAngle > target) doorAngle = Math.max(target, doorAngle - DOOR_SWING_PER_TICK);
    }

    // -------------------------------------------------------------------------
    // Interaction
    // -------------------------------------------------------------------------

    /** True if right-clicking the oven with this item should reach {@link #useItemOn}. Checked on both sides. */
    public boolean acceptsItem(ItemStack stack) {
        return stack.is(Items.FLINT_AND_STEEL)
                || stack.is(FOBlocks.COKE_BLOCK.asItem())
                || (level != null && findRecipe(level, stack).isPresent());
    }

    /** Server side. Only called when {@link #acceptsItem} returned true. */
    public ItemInteractionResult useItemOn(ItemStack stack, Player player, InteractionHand hand) {
        if (stack.is(Items.FLINT_AND_STEEL)) {
            if (!tryIgnite()) {
                player.displayClientMessage(ignitionProblem(), true);
                // Consume regardless so vanilla never places fire on the oven.
                return ItemInteractionResult.CONSUME;
            }
            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            level.playSound(null, worldPosition, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
            return ItemInteractionResult.CONSUME;
        }

        boolean isCoke = stack.is(FOBlocks.COKE_BLOCK.asItem());
        int taken = insert(stack.copyWithCount(1));
        if (taken <= 0) return ItemInteractionResult.CONSUME;
        if (!player.getAbilities().instabuild) stack.shrink(taken);
        level.playSound(null, worldPosition,
                isCoke ? SoundEvents.GRAVEL_PLACE : SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 0.6f, 0.9f);
        syncToClient();
        return ItemInteractionResult.CONSUME;
    }

    /** Server side, empty hand, not sneaking, structure formed: collects the contents. */
    public InteractionResult useWithoutItem(Player player) {
        if (!takeContents(player)) {
            player.displayClientMessage(collectionProblem(), true);
        }
        return InteractionResult.CONSUME;
    }

    /** Contents can only come out through an open door, and never while lit. */
    public boolean canCollect() {
        return doorOpen && !lit && hasContents();
    }

    /** Why {@link #takeContents} would refuse, for the action bar. */
    private Component collectionProblem() {
        if (lit) return Component.literal("The oven is lit");
        if (!doorOpen) return Component.literal("Open the door first");
        return Component.literal("The oven is empty.");
    }

    /**
     * Hands the entire contents (raw or finished) to the player. Refused while
     * lit or with the door shut. Server side only.
     */
    public boolean takeContents(Player player) {
        if (!canCollect()) return false;
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(contents.getStackInSlot(0));
        contents.setStackInSlot(0, ItemStack.EMPTY);
        for (int i = 0; i < outputs.getSlots(); i++) {
            stacks.add(outputs.getStackInSlot(i));
            outputs.setStackInSlot(i, ItemStack.EMPTY);
        }
        finished = false;
        progress = 0;
        firingTicks = 0;

        for (ItemStack stack : stacks) {
            while (!stack.isEmpty()) {
                ItemStack part = stack.split(stack.getMaxStackSize());
                if (!player.getInventory().add(part)) player.drop(part, false);
            }
        }
        level.playSound(null, worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.7f, 1.0f);
        syncToClient();
        return true;
    }

    /** Drops everything held; used when the oven block is broken. */
    public void dropContents() {
        if (level == null) return;
        BlockPos p = worldPosition;
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(contents.getStackInSlot(0));
        for (int i = 0; i < outputs.getSlots(); i++) {
            stacks.add(outputs.getStackInSlot(i));
            outputs.setStackInSlot(i, ItemStack.EMPTY);
        }
        for (ItemStack stack : stacks) {
            while (!stack.isEmpty()) {
                Containers.dropItemStack(level, p.getX(), p.getY(), p.getZ(), stack.split(stack.getMaxStackSize()));
            }
        }
        if (coke > 0) {
            Containers.dropItemStack(level, p.getX(), p.getY(), p.getZ(), new ItemStack(FOBlocks.COKE_BLOCK.get(), coke));
        }
        contents.setStackInSlot(0, ItemStack.EMPTY);
        coke = 0;
        lit = false;
        finished = false;
        progress = 0;
        firingTicks = 0;
    }

    // -------------------------------------------------------------------------
    // Recipe lookup
    // -------------------------------------------------------------------------

    public static Optional<RecipeHolder<CementationRecipe>> findRecipe(Level level, ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();
        ItemStackHandler probe = new ItemStackHandler(1);
        probe.setStackInSlot(0, stack.copyWithCount(1));
        return level.getRecipeManager().getRecipeFor(
                FORecipeTypes.CEMENTATION.<RecipeWrapper, CementationRecipe>getType(),
                new RecipeWrapper(probe), level);
    }

    // -------------------------------------------------------------------------
    // Goggles
    // -------------------------------------------------------------------------

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean sneaking) {
        if (!isFormed()) {
            tooltip.add(Component.literal("Cementation Oven — Unformed").withStyle(ChatFormatting.RED));
            return true;
        }
        CreateLang.text("Cementation Oven").style(ChatFormatting.WHITE).forGoggles(tooltip);

        ItemStack stack = getContents();
        CreateLang.text("Ingots: ").style(ChatFormatting.GRAY)
                .add(stack.isEmpty()
                        ? CreateLang.text("none").style(ChatFormatting.DARK_GRAY)
                        : CreateLang.text(stack.getCount() + "x ").add(stack.getHoverName()).style(ChatFormatting.AQUA))
                .forGoggles(tooltip, 1);
        for (ItemStack output : getOutputs()) {
            CreateLang.text("Output: ").style(ChatFormatting.GRAY)
                    .add(CreateLang.text(output.getCount() + "x ").add(output.getHoverName()).style(ChatFormatting.GOLD))
                    .forGoggles(tooltip, 1);
        }
        CreateLang.text("Coke: ").style(ChatFormatting.GRAY)
                .add(CreateLang.text(coke + " / " + COKE_CAPACITY)
                        .style(hasFuel() ? ChatFormatting.GREEN : ChatFormatting.YELLOW))
                .forGoggles(tooltip, 1);
        CreateLang.text("Door: ").style(ChatFormatting.GRAY)
                .add(CreateLang.text(doorOpen ? "open" : "closed").style(doorOpen ? ChatFormatting.RED : ChatFormatting.GREEN))
                .forGoggles(tooltip, 1);
        CreateLang.text("Lit: ").style(ChatFormatting.GRAY)
                .add(CreateLang.text(lit ? "yes" : "no").style(lit ? ChatFormatting.GREEN : ChatFormatting.RED))
                .forGoggles(tooltip, 1);
        if (finished) {
            CreateLang.text("Batch finished, ready to collect").style(ChatFormatting.GOLD).forGoggles(tooltip, 1);
        } else if (firingTicks > 0) {
            CreateLang.text("Progress: ").style(ChatFormatting.GRAY)
                    .add(CreateLang.text((progress * 100 / firingTicks) + "%").style(ChatFormatting.AQUA))
                    .forGoggles(tooltip, 1);
        }
        return true;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** Raw ingots currently loaded. */
    public ItemStack getContents() { return contents.getStackInSlot(0); }
    public boolean hasIngots()     { return !getContents().isEmpty(); }

    /** Non-empty finished output stacks, in recipe order. */
    public List<ItemStack> getOutputs() {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < outputs.getSlots(); i++) {
            ItemStack s = outputs.getStackInSlot(i);
            if (!s.isEmpty()) list.add(s);
        }
        return list;
    }
    public boolean hasOutputs()    { return !getOutputs().isEmpty(); }

    /** Anything at all inside: raw ingots or finished output. */
    public boolean hasContents()   { return hasIngots() || hasOutputs(); }
    public int getCoke()           { return coke; }
    public boolean isLit()         { return lit; }
    public boolean isFinished()    { return finished; }
    public int getProgress()       { return progress; }
    public int getFiringTicks()    { return firingTicks; }

    // -------------------------------------------------------------------------
    // NBT
    // -------------------------------------------------------------------------

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Contents", contents.serializeNBT(registries));
        tag.put("Outputs", outputs.serializeNBT(registries));
        tag.putInt("Coke", coke);
        tag.putBoolean("Lit", lit);
        tag.putBoolean("Finished", finished);
        tag.putInt("Progress", progress);
        tag.putInt("FiringTicks", firingTicks);
        tag.putBoolean("DoorOpen", doorOpen);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Contents")) contents.deserializeNBT(registries, tag.getCompound("Contents"));
        if (tag.contains("Outputs")) outputs.deserializeNBT(registries, tag.getCompound("Outputs"));
        coke = tag.getInt("Coke");
        lit = tag.getBoolean("Lit");
        finished = tag.getBoolean("Finished");
        progress = tag.getInt("Progress");
        firingTicks = tag.getInt("FiringTicks");
        doorOpen = tag.getBoolean("DoorOpen");
    }
}
