package com.maxwell.cyber_ware_port.common.block.scanner;

import com.maxwell.cyber_ware_port.api.event.CyberwareEvents;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.container.ScannerMenu;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScannerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_PAPER = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int MAX_PROGRESS = 2400;
    private static final int SLOT_COUNT = 3;
    protected final ContainerData data;
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case SLOT_PAPER -> stack.is(Items.PAPER);
                case SLOT_INPUT -> CyberwareAPI.getCyberware(stack) != null;
                case SLOT_OUTPUT -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final IItemHandlerModifiable exposedHandler = new IItemHandlerModifiable() {
        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            itemHandler.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlots() {
            return SLOT_COUNT;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return itemHandler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return stack;
            if (slot == SLOT_PAPER && stack.is(Items.PAPER)) return itemHandler.insertItem(SLOT_PAPER, stack, simulate);
            if (slot == SLOT_INPUT && CyberwareAPI.getCyberware(stack) != null) return itemHandler.insertItem(SLOT_INPUT, stack, simulate);
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == SLOT_OUTPUT ? itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return itemHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return itemHandler.isItemValid(slot, stack);
        }
    };

    private int progress = 0;
    private boolean isWorking = false;

    public ScannerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SCANNER.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ScannerBlockEntity.this.progress;
                    case 1 -> MAX_PROGRESS;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                if (pIndex == 0) ScannerBlockEntity.this.progress = pValue;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, ScannerBlockEntity pEntity) {
        if (pLevel.isClientSide()) {
            if (pEntity.isWorking) {
                pEntity.progress++;
                if (pEntity.progress >= MAX_PROGRESS) pEntity.progress = 0;
            } else {
                pEntity.progress = 0;
            }
            return;
        }
        if (pEntity.hasRecipe()) {
            pEntity.progress++;
            if (!pEntity.isWorking) {
                pEntity.isWorking = true;
                pEntity.syncToClient();
            }
            if (pLevel.getGameTime() % 20 == 0) {
                pLevel.playSound(null, pPos, SoundEvents.CONDUIT_AMBIENT, SoundSource.BLOCKS, 0.8F, 1.2F);
                pLevel.playSound(null, pPos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.5F, 1.5F);
            }
            setChanged(pLevel, pPos, pState);
            if (pEntity.progress >= MAX_PROGRESS) {
                pEntity.craftItem();
                pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 1.0F, 1.2F);
                pEntity.progress = 0;
                if (!pEntity.hasRecipe()) {
                    pEntity.isWorking = false;
                    pEntity.syncToClient();
                }
            }
        } else {
            pEntity.progress = 0;
            if (pEntity.isWorking) {
                pEntity.isWorking = false;
                pEntity.syncToClient();
            }
        }
    }

    private void syncToClient() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public boolean isWorking() {
        return this.isWorking;
    }

    public float getProgress() {
        return (float) this.progress;
    }

    private boolean hasRecipe() {
        ItemStack paperStack = itemHandler.getStackInSlot(SLOT_PAPER);
        ItemStack inputStack = itemHandler.getStackInSlot(SLOT_INPUT);
        ItemStack outputStack = itemHandler.getStackInSlot(SLOT_OUTPUT);
        return paperStack.is(Items.PAPER) && CyberwareAPI.getCyberware(inputStack) != null && outputStack.isEmpty();
    }

    private void craftItem() {
        if (!hasRecipe()) return;
        ItemStack inputStack = itemHandler.getStackInSlot(SLOT_INPUT);
        CyberwareEvents.Scan.Complete event = new CyberwareEvents.Scan.Complete(this, inputStack, 0.5f);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) return;
        if (this.level.random.nextFloat() < event.getChance()) {
            ItemStack blueprint = BlueprintItem.createBlueprintFor(inputStack.getItem());
            itemHandler.setStackInSlot(SLOT_OUTPUT, blueprint);
        }
        if (event.shouldConsumeItem()) {
            itemHandler.extractItem(SLOT_PAPER, 1, false);
            itemHandler.extractItem(SLOT_INPUT, 1, false);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.cyber_ware_port.scanner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ScannerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("scanner.progress", progress);
        pTag.putBoolean("scanner.isWorking", isWorking);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("inventory")) {
            itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        }
        progress = pTag.getInt("scanner.progress");
        isWorking = pTag.getBoolean("scanner.isWorking");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public IItemHandlerModifiable getExposedHandler() {
        return exposedHandler;
    }
}