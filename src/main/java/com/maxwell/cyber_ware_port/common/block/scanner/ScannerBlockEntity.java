package com.maxwell.cyber_ware_port.common.block.scanner;

import com.maxwell.cyber_ware_port.api.event.CyberwareEvents;
import com.maxwell.cyber_ware_port.common.container.ScannerMenu;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
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
                case SLOT_INPUT -> stack.getItem() instanceof ICyberware;
                case SLOT_OUTPUT -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };
    private final IItemHandler paperInputHandler = new RangedWrapper(itemHandler, SLOT_PAPER, SLOT_PAPER + 1) {
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };
    private final IItemHandler componentInputHandler = new RangedWrapper(itemHandler, SLOT_INPUT, SLOT_INPUT + 1) {
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };
    private final IItemHandler outputHandler = new RangedWrapper(itemHandler, SLOT_OUTPUT, SLOT_OUTPUT + 1) {
        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    };
    private final IItemHandler genericInputHandler = new RangedWrapper(itemHandler, 0, 2) {
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    };
    private LazyOptional<IItemHandler> lazyPaperHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyComponentHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyGenericInputHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
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
                if (pEntity.progress >= MAX_PROGRESS) {
                    pEntity.progress = 0;
                }
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
            setChanged(pLevel, pPos, pState);
            if (pEntity.progress >= MAX_PROGRESS) {
                pEntity.craftItem();
                pEntity.progress = 0;
                if (!pEntity.hasRecipe()) {
                    pEntity.isWorking = false;
                    pEntity.syncToClient();
                }
            }
        } else {
            pEntity.resetProgress();
            if (pEntity.isWorking) {
                pEntity.isWorking = false;
                pEntity.syncToClient();
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyPaperHandler = LazyOptional.of(() -> paperInputHandler);
        lazyComponentHandler = LazyOptional.of(() -> componentInputHandler);
        lazyOutputHandler = LazyOptional.of(() -> outputHandler);
        lazyGenericInputHandler = LazyOptional.of(() -> genericInputHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyPaperHandler.invalidate();
        lazyComponentHandler.invalidate();
        lazyOutputHandler.invalidate();
        lazyGenericInputHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyItemHandler.cast();
            }
            Direction facing = this.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            Direction right = facing.getCounterClockWise();
            Direction left = facing.getClockWise();
            Direction back = facing.getOpposite();
            if (side == right) {
                return lazyOutputHandler.cast();
            }
            if (side == left) {
                return lazyComponentHandler.cast();
            }
            if (side == back) {
                return lazyPaperHandler.cast();
            }
            return lazyGenericInputHandler.cast();
        }
        return super.getCapability(cap, side);
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
        boolean hasInput = paperStack.is(Items.PAPER) && inputStack.getItem() instanceof ICyberware;
        if (!hasInput) return false;
        return outputStack.isEmpty();
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        if (!hasRecipe()) return;
        ItemStack inputStack = itemHandler.getStackInSlot(SLOT_INPUT);
        CyberwareEvents.Scan.Complete event = new CyberwareEvents.Scan.Complete(this, inputStack, 0.5f);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
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
    public Component getDisplayName() {
        return Component.translatable("block.cyber_ware_port.scanner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ScannerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("scanner.progress", progress);
        pTag.putBoolean("scanner.isWorking", isWorking);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("scanner.progress");
        isWorking = pTag.getBoolean("scanner.isWorking");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0;
             i < itemHandler.getSlots();
             i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.load(pkt.getTag());
        }
    }
}