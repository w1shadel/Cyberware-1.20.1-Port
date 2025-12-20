package com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon;

import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberBlock;
import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Container.RobosurgeonMenu;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareSlotType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import com.Maxwell.cyber_ware_port.Common.Network.A_PacketHandler;
import com.Maxwell.cyber_ware_port.Common.Network.SyncSurgeryProgressPacket;
import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RobosurgeonBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOTS_PER_PART = BodyRegionEnum.SLOTS_PER_PART;
    public static final int SLOT_EYES = BodyRegionEnum.EYES.getStartSlot();
    public static final int SLOT_BRAIN = BodyRegionEnum.BRAIN.getStartSlot();
    public static final int SLOT_HEART = BodyRegionEnum.HEART.getStartSlot();
    public static final int SLOT_LUNGS = BodyRegionEnum.LUNGS.getStartSlot();
    public static final int SLOT_STOMACH = BodyRegionEnum.STOMACH.getStartSlot();
    public static final int SLOT_SKIN = BodyRegionEnum.SKIN.getStartSlot();
    public static final int SLOT_MUSCLE = BodyRegionEnum.MUSCLE.getStartSlot();
    public static final int SLOT_BONES = BodyRegionEnum.BONES.getStartSlot();
    public static final int SLOT_ARMS = BodyRegionEnum.ARMS.getStartSlot();
    public static final int SLOT_HANDS = BodyRegionEnum.HANDS.getStartSlot();
    public static final int SLOT_LEGS = BodyRegionEnum.LEGS.getStartSlot();
    public static final int SLOT_BOOTS = BodyRegionEnum.BOOTS.getStartSlot();
    public static final int TOTAL_SLOTS = BodyRegionEnum.getTotalSlots();
    protected final ContainerData data;
    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return stack;
            if (!isItemValid(slot, stack)) {
                return stack;
            }
            if (stack.getItem() instanceof ICyberware incomingItem) {
                int maxInstall = incomingItem.getMaxInstallAmount(stack);
                if (maxInstall <= 0) maxInstall = 1;
                int currentlyInstalledTotal = 0;
                for (int i = 0; i < getSlots(); i++) {
                    ItemStack inSlot = getStackInSlot(i);
                    if (!inSlot.isEmpty() && inSlot.getItem() == stack.getItem()) {
                        currentlyInstalledTotal += inSlot.getCount();
                    }
                }
                int globalRemaining = maxInstall - currentlyInstalledTotal;
                if (globalRemaining <= 0) {
                    return stack;
                }
                if (stack.getCount() > globalRemaining) {
                    ItemStack toInsert = stack.copy();
                    toInsert.setCount(globalRemaining);
                    ItemStack remainderFromInsert = super.insertItem(slot, toInsert, simulate);
                    int accepted = toInsert.getCount() - remainderFromInsert.getCount();
                    ItemStack result = stack.copy();
                    result.setCount(stack.getCount() - accepted);
                    return result;
                }
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            ItemStack currentStack = this.getStackInSlot(slot);
            if (!currentStack.isEmpty() && currentStack.hasTag() && currentStack.getTag().getBoolean("cyberware_ghost")) {
                return false;
            }
            if (!(stack.getItem() instanceof ICyberware incomingItem)) {
                return false;
            }
            CyberwareSlotType itemType = CyberwareSlotType.fromId(incomingItem.getSlot(stack));
            CyberwareSlotType targetType = CyberwareSlotType.fromId(slot);
            if (itemType != targetType || itemType == CyberwareSlotType.UNKNOWN) {
                return false;
            }
            int maxInstall = incomingItem.getMaxInstallAmount(stack);
            if (maxInstall <= 0) maxInstall = 1;
            int installedCount = 0;
            for (int j = 0; j < this.getSlots(); j++) {
                if (j == slot) continue;
                ItemStack tableStack = this.getStackInSlot(j);
                if (!tableStack.isEmpty() && tableStack.getItem() == stack.getItem()) {
                    installedCount += tableStack.getCount();
                }
            }
            if (installedCount >= maxInstall) {
                return false;
            }
            for (int j = 0; j < this.getSlots(); j++) {
                if (j == slot) continue;
                ItemStack otherStack = this.getStackInSlot(j);
                if (!otherStack.isEmpty()) {
                    if (incomingItem.isIncompatible(stack, otherStack)) {
                        return false;
                    }
                    if (otherStack.getItem() instanceof ICyberware otherCW) {
                        if (otherCW.isIncompatible(otherStack, stack)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private int progress = 0;
    private int maxProgress = 100;

    public RobosurgeonBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.ROBO_SURGEON.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> RobosurgeonBlockEntity.this.progress;
                    case 1 -> RobosurgeonBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> RobosurgeonBlockEntity.this.progress = pValue;
                    case 1 -> RobosurgeonBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RobosurgeonBlockEntity entity) {
        if (level.isClientSide) return;
        BlockPos chamberPos = entity.findChamberPos();
        if (chamberPos == null) {
            entity.resetProgress();
            return;
        }
        BlockEntity be = level.getBlockEntity(chamberPos);
        if (!(be instanceof SurgeryChamberBlockEntity chamber)) {
            entity.resetProgress();
            return;
        }
        if (chamber.isOpen()) {
            if (entity.progress > 0) {
                entity.resetProgress();
                A_PacketHandler.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> (ServerPlayer) entity.findPatient(chamberPos)),
                        new SyncSurgeryProgressPacket(0, entity.maxProgress)
                );
            }
            return;
        }
        LivingEntity patient = entity.findPatient(chamberPos);
        if (patient == null || !(patient instanceof ServerPlayer serverPlayer)) {
            if (entity.progress > 0) {
                entity.resetProgress();
                chamber.setDoorState(true);
            }
            return;
        }
        boolean needs = entity.needsSurgery(serverPlayer);
        boolean requirementsMet = entity.checkRequirements(serverPlayer);
        if (needs && requirementsMet) {
            entity.progress++;
            setChanged(level, pos, state);
            A_PacketHandler.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new SyncSurgeryProgressPacket(entity.progress, entity.maxProgress)
            );
            if (entity.progress % 20 == 0) {
                serverPlayer.hurt(level.damageSources().magic(), 1.0f);
                level.playSound(null, chamberPos, SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 0.5f, 1.0f);
            }
            if (entity.progress >= entity.maxProgress) {
                entity.performSurgery(serverPlayer);
                entity.resetProgress();
                A_PacketHandler.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new SyncSurgeryProgressPacket(0, entity.maxProgress)
                );
                chamber.setDoorState(true);
            }
        } else {
            if (entity.progress > 0) {
                entity.resetProgress();
                A_PacketHandler.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new SyncSurgeryProgressPacket(0, entity.maxProgress)
                );
                chamber.setDoorState(true);
            }
        }
    }

    private boolean needsSurgery(ServerPlayer player) {
        var cap = player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY);
        if (!cap.isPresent()) return false;
        ItemStackHandler playerBody = cap.resolve().get().getInstalledCyberware();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            ItemStack tableItem = itemHandler.getStackInSlot(i);
            ItemStack bodyItem = playerBody.getStackInSlot(i);
            if (!tableItem.isEmpty()) {
                if (tableItem.hasTag() && tableItem.getTag().getBoolean("cyberware_ghost")) {
                    continue;
                }
                if (!ItemStack.matches(tableItem, bodyItem)) {
                    return true;
                }
            } else {
                if (!bodyItem.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private BlockPos findChamberPos() {
        BlockPos below = this.worldPosition.below();
        BlockState state = level.getBlockState(below);
        if (state.getBlock() instanceof SurgeryChamberBlock) {
            if (state.getValue(SurgeryChamberBlock.HALF) == DoubleBlockHalf.UPPER) {
                return below.below();
            }
            return below;
        }
        return null;
    }

    private LivingEntity findPatient(BlockPos chamberPos) {
        double x = chamberPos.getX();
        double y = chamberPos.getY();
        double z = chamberPos.getZ();
        AABB box = new AABB(
                x + 0.3, y + 0.1, z + 0.3,
                x + 0.7, y + 1.9, z + 0.7
        );
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box);
        if (!entities.isEmpty()) {
            return entities.get(0);
        }
        return null;
    }

    public void populateGhostItems(ServerPlayer player) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
            ItemStackHandler playerBody = userData.getInstalledCyberware();
            boolean changed = false;
            for (int i = 0; i < TOTAL_SLOTS; i++) {
                ItemStack bodyStack = playerBody.getStackInSlot(i);
                ItemStack tableStack = itemHandler.getStackInSlot(i);
                if (!tableStack.isEmpty() && tableStack.hasTag() && tableStack.getTag().getBoolean("cyberware_ghost")) {
                    if (bodyStack.isEmpty()) {
                        itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        changed = true;
                    } else {
                        ItemStack expectedGhost = bodyStack.copy();
                        expectedGhost.getOrCreateTag().putBoolean("cyberware_ghost", true);
                        if (!ItemStack.matches(tableStack, expectedGhost)) {
                            itemHandler.setStackInSlot(i, expectedGhost);
                            changed = true;
                        }
                    }
                } else if (tableStack.isEmpty() && !bodyStack.isEmpty()) {
                    ItemStack ghostStack = bodyStack.copy();
                    ghostStack.getOrCreateTag().putBoolean("cyberware_ghost", true);
                    itemHandler.setStackInSlot(i, ghostStack);
                    changed = true;
                }
            }
            if (changed) {
                setChanged();
                if (level != null) level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            }
        });
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    private boolean checkRequirements(ServerPlayer player) {
        var cap = player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY);
        if (!cap.isPresent()) return false;
        ItemStackHandler playerBody = cap.resolve().get().getInstalledCyberware();
        java.util.List<ItemStack> futureBody = new java.util.ArrayList<>();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            ItemStack tableStack = itemHandler.getStackInSlot(i);
            ItemStack bodyStack = playerBody.getStackInSlot(i);
            if (tableStack.isEmpty()) {
                futureBody.add(ItemStack.EMPTY);
            } else if (tableStack.hasTag() && tableStack.getTag().getBoolean("cyberware_ghost")) {
                futureBody.add(bodyStack);
            } else {
                futureBody.add(tableStack);
            }
        }
        for (ItemStack stack : futureBody) {
            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cw) {
                java.util.Set<net.minecraft.world.item.Item> reqs = cw.getPrerequisites(stack);
                for (net.minecraft.world.item.Item reqItem : reqs) {
                    boolean found = false;
                    for (ItemStack checkStack : futureBody) {
                        if (!checkStack.isEmpty() && checkStack.getItem() == reqItem) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) return false;
                }
            }
        }
        return true;
    }

    public void performSurgery(ServerPlayer player) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
            ItemStackHandler playerBody = userData.getInstalledCyberware();
            boolean soundPlayed = false;
            for (int i = 0; i < TOTAL_SLOTS; i++) {
                ItemStack tableStack = this.itemHandler.getStackInSlot(i);
                ItemStack bodyStack = playerBody.getStackInSlot(i);
                if (!tableStack.isEmpty() && tableStack.hasTag() && tableStack.getTag().getBoolean("cyberware_ghost")) {
                    continue;
                }
                if (tableStack.isEmpty()) {
                    if (!bodyStack.isEmpty()) {
                        ItemStack removed = bodyStack.copy();
                        playerBody.setStackInSlot(i, ItemStack.EMPTY);
                        if (!player.getInventory().add(removed)) {
                            player.drop(removed, false);
                        }
                        soundPlayed = true;
                    }
                } else {
                    ItemStack extractedNew = this.itemHandler.extractItem(i, 64, false);
                    ItemStack oldBodyItem = bodyStack.copy();
                    playerBody.setStackInSlot(i, extractedNew);
                    if (!oldBodyItem.isEmpty()) {
                        if (!player.getInventory().add(oldBodyItem)) {
                            player.drop(oldBodyItem, false);
                        }
                    }
                    soundPlayed = true;
                }
            }
            if (soundPlayed) {
                userData.recalculateCapacity(player);
                userData.syncToClient(player);
                player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_HURT, SoundSource.PLAYERS, 1.0f, 1.0f);
                if (userData.getTolerance() <= 0) {
                    DamageSource surgeryDeath = new DamageSource(
                            player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)
                    ) {
                        @Override
                        public Component getLocalizedDeathMessage(LivingEntity entity) {
                            return Component.translatable("death.attack.cyberware.surgery", entity.getDisplayName());
                        }
                    };
                    player.hurt(surgeryDeath, Float.MAX_VALUE);
                }
                populateGhostItems(player);
            }
        });
    }

    private void resetProgress() {
        this.progress = 0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.cyber_ware_port.robosurgeon");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            this.populateGhostItems(serverPlayer);
            level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
        }
        return new RobosurgeonMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("robosurgeon.progress", progress);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("robosurgeon.progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty() && (!stack.hasTag() || !stack.getTag().getBoolean("cyberware_ghost"))) {
                inventory.setItem(i, stack);
            }
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}