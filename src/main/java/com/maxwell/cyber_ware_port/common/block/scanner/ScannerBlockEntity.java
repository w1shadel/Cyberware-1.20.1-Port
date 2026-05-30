package com.maxwell.cyber_ware_port.common.block.scanner;

import com.maxwell.cyber_ware_port.api.event.CyberwareEvents;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.container.ScannerMenu;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction; 
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.ResourceHandler; 
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScannerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_PAPER = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int MAX_PROGRESS = 2400;
    private static final int SLOT_COUNT = 3;
    protected final ContainerData data;

    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return switch (index) {
                case SLOT_PAPER -> resource.is(Items.PAPER);
                case SLOT_INPUT -> CyberwareAPI.getCyberware(resource.toStack()) != null;
                case SLOT_OUTPUT -> false;
                default -> super.isValid(index, resource);
            };
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (!isValid(index, resource)) return 0;
            return super.insert(index, resource, amount, transaction);
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

    public ItemStack getStack(int index) {
        return itemHandler.getResource(index).toStack(itemHandler.getAmountAsInt(index));
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
        ItemStack paperStack = getStack(SLOT_PAPER);
        ItemStack inputStack = getStack(SLOT_INPUT);
        ItemStack outputStack = getStack(SLOT_OUTPUT);
        return paperStack.is(Items.PAPER) && CyberwareAPI.getCyberware(inputStack) != null && outputStack.isEmpty();
    }

    private void craftItem() {
        if (!hasRecipe()) return;
        ItemStack inputStack = getStack(SLOT_INPUT);
        CyberwareEvents.Scan.Complete event = new CyberwareEvents.Scan.Complete(this, inputStack, 0.5f);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) return;
        if (this.level.getRandom().nextFloat() < event.getChance()) {
            ItemStack blueprint = BlueprintItem.createBlueprintFor(inputStack.getItem());
            itemHandler.set(SLOT_OUTPUT, ItemResource.of(blueprint), blueprint.getCount());
        }
        if (event.shouldConsumeItem()) {

            try (Transaction tx = Transaction.openRoot()) {
                itemHandler.extract(SLOT_PAPER, ItemResource.of(Items.PAPER), 1, tx);
                itemHandler.extract(SLOT_INPUT, ItemResource.of(inputStack), 1, tx);
                tx.commit();
            }
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.itemHandler.serialize(output.child("inventory"));
        output.putInt("progress", this.progress);
        output.putBoolean("scanner.isWorking", isWorking);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemHandler.deserialize(input.childOrEmpty("inventory"));
        this.progress = input.getIntOr("progress", 0);
        this.isWorking = input.getBooleanOr("scanner.isWorking", false);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for (int i = 0; i < itemHandler.size(); i++) {
            inventory.setItem(i, getStack(i));
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

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
    }

    public ResourceHandler<ItemResource> getSidedHandler(@Nullable Direction side) {
        if (side == null) return this.itemHandler;

        if (side == Direction.UP) {

            return new SidedResourceHandler(this.itemHandler, new int[]{SLOT_PAPER}, new int[]{});
        }
        if (side == Direction.DOWN) {

            return new SidedResourceHandler(this.itemHandler, new int[]{}, new int[]{SLOT_OUTPUT});
        }

        return new SidedResourceHandler(this.itemHandler, new int[]{SLOT_INPUT}, new int[]{});
    }

    private class SidedResourceHandler implements ResourceHandler<ItemResource> {
        private final ItemStacksResourceHandler parent;
        private final int[] insertSlots;
        private final int[] extractSlots;

        public SidedResourceHandler(ItemStacksResourceHandler parent, int[] insertSlots, int[] extractSlots) {
            this.parent = parent;
            this.insertSlots = insertSlots;
            this.extractSlots = extractSlots;
        }

        @Override
        public int size() { return parent.size(); }
        @Override
        public ItemResource getResource(int index) { return parent.getResource(index); }
        @Override
        public long getAmountAsLong(int index) { return parent.getAmountAsLong(index); }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            if (!isSlotInArray(index, insertSlots) && !isSlotInArray(index, extractSlots)) return 0;
            return parent.getCapacityAsLong(index, resource);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return parent.isValid(index, resource);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (isSlotInArray(index, insertSlots)) {
                return parent.insert(index, resource, amount, transaction);
            }
            return 0;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (isSlotInArray(index, extractSlots)) {
                return parent.extract(index, resource, amount, transaction);
            }
            return 0;
        }

        private boolean isSlotInArray(int index, int[] slots) {
            for (int s : slots) if (s == index) return true;
            return false;
        }
    }
}