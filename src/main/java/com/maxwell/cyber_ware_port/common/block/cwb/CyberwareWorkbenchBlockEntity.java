package com.maxwell.cyber_ware_port.common.block.cwb;

import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import com.maxwell.cyber_ware_port.init.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CyberwareWorkbenchBlockEntity extends BlockEntity implements MenuProvider {

    public static final int INPUT_SLOT = 0;

    public static final int PAPER_SLOT = 1;

    public static final int BLUEPRINT_SLOT = 2;

    public static final int OUTPUT_SLOT_START = 3;

    public static final int OUTPUT_SLOT_END = 8;

    public static final int SPECIAL_OUTPUT_SLOT = 9;

    private static final int INVENTORY_SIZE = 10;
    public float animationProgress = 0.0f;
    public float prevAnimationProgress = 0.0f;
    private AssemblyRecipe cachedRecipe = null;
    private final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == BLUEPRINT_SLOT) {
                cachedRecipe = null;

            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot >= OUTPUT_SLOT_START) {
                return true;

            }
            return switch (slot) {
                case INPUT_SLOT -> stack.getItem() instanceof ICyberware;
                case PAPER_SLOT -> stack.is(Items.PAPER);
                case BLUEPRINT_SLOT -> stack.getItem() instanceof BlueprintItem;
                default -> false;

            };

        }
    };
    private final IItemHandler automationInputHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return INVENTORY_SIZE;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return itemHandler.getStackInSlot(slot);

        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return stack;
            if (stack.is(Items.PAPER)) {
                return itemHandler.insertItem(PAPER_SLOT, stack, simulate);

            }
            if (stack.getItem() instanceof BlueprintItem) {
                return itemHandler.insertItem(BLUEPRINT_SLOT, stack, simulate);

            }
            if (stack.getItem() instanceof ICyberware) {
                return itemHandler.insertItem(INPUT_SLOT, stack, simulate);

            }
            AssemblyRecipe activeRecipe = getActiveAssemblyRecipe();
            if (activeRecipe != null) {
                if (!isItemNeededForRecipe(activeRecipe, stack)) {
                    return stack;

                }
            }
            ItemStack remaining = stack.copy();
            for (int i = OUTPUT_SLOT_START;
                 i < SPECIAL_OUTPUT_SLOT;
                 i++) {
                remaining = itemHandler.insertItem(i, remaining, simulate);
                if (remaining.isEmpty()) {
                    return ItemStack.EMPTY;

                }
            }
            return remaining;

        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;

        }

        @Override
        public int getSlotLimit(int slot) {
            return itemHandler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }
    };
    private final IItemHandler automationOutputHandler = new RangedWrapper(itemHandler, OUTPUT_SLOT_START, INVENTORY_SIZE) {

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;

        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int absoluteSlot = slot + OUTPUT_SLOT_START;
            if (absoluteSlot == SPECIAL_OUTPUT_SLOT) {
                return super.extractItem(slot, amount, simulate);

            }
            AssemblyRecipe activeRecipe = getActiveAssemblyRecipe();
            if (activeRecipe != null) {
                ItemStack stackInSlot = itemHandler.getStackInSlot(absoluteSlot);
                if (!stackInSlot.isEmpty() && isItemNeededForRecipe(activeRecipe, stackInSlot)) {
                    return ItemStack.EMPTY;

                }
            }
            return super.extractItem(slot, amount, simulate);

        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyInputHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.empty();
    private int progress = 0;
    private boolean isCrafting = false;
    private int cooldown = 0;

    public CyberwareWorkbenchBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CYBERWARE_WORKBENCH.get(), pPos, pBlockState);

    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CyberwareWorkbenchBlockEntity pBlockEntity) {
        pBlockEntity.prevAnimationProgress = pBlockEntity.animationProgress;
        if (pBlockEntity.cooldown > 0) pBlockEntity.cooldown--;
        float target = pBlockEntity.isCrafting ? 1.0F : 0.0F;
        float speed = 0.5F;
        if (pBlockEntity.animationProgress < target) {
            pBlockEntity.animationProgress = Math.min(pBlockEntity.animationProgress + speed, target);

        } else if (pBlockEntity.animationProgress > target) {
            pBlockEntity.animationProgress = Math.max(pBlockEntity.animationProgress - speed, target);

        }
        if (!pLevel.isClientSide) {
            if (pLevel.hasNeighborSignal(pPos)) {
                pBlockEntity.startCrafting();
            }
            if (pBlockEntity.cooldown == 0) {
                if (pBlockEntity.isCrafting) {
                    pLevel.playSound(null, pPos, SoundEvents.ANVIL_LAND, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, 1.2F);
                    pBlockEntity.cooldown = 3;
                    pBlockEntity.craftItem();
                }
            }
            if (pBlockEntity.isCrafting && pBlockEntity.animationProgress >= 1.0F) {
                pBlockEntity.resetCrafting();
            }
        }
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

    public void startCrafting() {
        if (!this.isCrafting && this.canCraft()) {
            this.isCrafting = true;
            this.animationProgress = 0.0f;
            this.progress = 0;
            setChanged();
            notifyClient();

        }
    }

    private void resetCrafting() {
        this.isCrafting = false;
        this.progress = 0;
        this.cooldown = 1;
        setChanged();
        notifyClient();

    }

    private void notifyClient() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);

        }
    }

    private boolean checkOrConsumeIngredients(AssemblyRecipe recipe, boolean consume) {
        for (AssemblyRecipe.SizedIngredient req : recipe.getInputs()) {
            int needed = req.count();
            int found = 0;
            for (int i = OUTPUT_SLOT_START;
                 i < SPECIAL_OUTPUT_SLOT;
                 i++) {
                ItemStack stack = this.itemHandler.getStackInSlot(i);
                if (req.ingredient().test(stack)) {
                    int take = Math.min(stack.getCount(), needed - found);
                    if (consume) {
                        this.itemHandler.extractItem(i, take, false);

                    }
                    found += take;
                    if (found >= needed) break;

                }
            }
            if (found < needed) return false;

        }
        return true;

    }

    private boolean canCraft() {
        AssemblyRecipe recipe = getActiveAssemblyRecipe();
        if (recipe != null) {
            if (checkOrConsumeIngredients(recipe, false)) {
                ItemStack result = recipe.getResultItem(Objects.requireNonNull(this.level).registryAccess());
                ItemStack currentOutput = this.itemHandler.getStackInSlot(SPECIAL_OUTPUT_SLOT);
                if (currentOutput.isEmpty()) return true;
                return ItemStack.isSameItemSameTags(currentOutput, result) &&
                        currentOutput.getCount() + result.getCount() <= currentOutput.getMaxStackSize();

            }
            return false;

        }
        ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
        if (inputStack.isEmpty()) return false;
        ItemStack paperStack = this.itemHandler.getStackInSlot(PAPER_SLOT);
        if (paperStack.isEmpty() || !paperStack.is(Items.PAPER)) {
            return false;

        }
        SimpleContainer tempContainer = new SimpleContainer(1);
        tempContainer.setItem(0, inputStack);
        var recipeOpt = Objects.requireNonNull(this.level).getRecipeManager()
                .getRecipeFor(ModRecipes.ENGINEERING_TYPE.get(), tempContainer, this.level);
        if (recipeOpt.isPresent()) {
            for (int i = OUTPUT_SLOT_START;
                 i < SPECIAL_OUTPUT_SLOT;
                 i++) {
                if (this.itemHandler.getStackInSlot(i).isEmpty()) return true;

            }
        }
        return false;

    }

    private ItemStack mergeIntoOutput(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int i = OUTPUT_SLOT_START;
             i < SPECIAL_OUTPUT_SLOT;
             i++) {
            remainder = this.itemHandler.insertItem(i, remainder, false);
            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;

            }
        }
        return remainder;

    }

    private void craftItem() {
        AssemblyRecipe recipe = getActiveAssemblyRecipe();
        if (recipe != null) {
            if (checkOrConsumeIngredients(recipe, true)) {
                ItemStack result = recipe.getResultItem(Objects.requireNonNull(this.level).registryAccess()).copy();
                if (result.getItem() instanceof ICyberware cyberware) {
                    cyberware.setPristine(result, true);

                }
                this.itemHandler.insertItem(SPECIAL_OUTPUT_SLOT, result, false);
                this.itemHandler.extractItem(BLUEPRINT_SLOT, 1, false);

            }
            return;

        }
        ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
        if (inputStack.isEmpty()) return;
        SimpleContainer tempContainer = new SimpleContainer(1);
        tempContainer.setItem(0, inputStack);
        var recipeOpt = Objects.requireNonNull(this.level).getRecipeManager()
                .getRecipeFor(ModRecipes.ENGINEERING_TYPE.get(), tempContainer, this.level);
        if (recipeOpt.isPresent()) {
            EngineeringRecipe engRecipe = recipeOpt.get();
            List<ItemStack> results = engRecipe.rollOutputs(this.level.random);
            this.itemHandler.extractItem(INPUT_SLOT, 1, false);
            for (ItemStack result : results) {
                ItemStack remainder = mergeIntoOutput(result);
                if (!remainder.isEmpty()) {
                    net.minecraft.world.level.block.Block.popResource(this.level, this.worldPosition.above(), remainder);

                }
            }
            ItemStack paperStack = this.itemHandler.getStackInSlot(PAPER_SLOT);
            if (!paperStack.isEmpty() && paperStack.is(Items.PAPER)) {
                float chance = engRecipe.getBlueprintChance();
                if (this.level.random.nextFloat() < chance) {
                    ItemStack blueprint = BlueprintItem.createBlueprintFor(inputStack.getItem());
                    ItemStack remainder = mergeIntoOutput(blueprint);
                    if (remainder.isEmpty()) {
                        this.itemHandler.extractItem(PAPER_SLOT, 1, false);

                    }
                }
            }
        }
    }

    @Nullable
    private AssemblyRecipe getActiveAssemblyRecipe() {
        if (this.cachedRecipe != null) {
            return this.cachedRecipe;

        }
        if (this.level == null) return null;
        ItemStack blueprintStack = this.itemHandler.getStackInSlot(BLUEPRINT_SLOT);
        if (blueprintStack.isEmpty() || !(blueprintStack.getItem() instanceof BlueprintItem)) {
            return null;

        }
        Item targetItem = BlueprintItem.getTargetItem(blueprintStack);
        if (targetItem == null) return null;
        var recipes = this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ASSEMBLY_TYPE.get());
        for (AssemblyRecipe recipe : recipes) {
            if (recipe.getResultItem(this.level.registryAccess()).getItem() == targetItem) {
                this.cachedRecipe = recipe;
                return recipe;

            }
        }
        return null;

    }

    private boolean isItemNeededForRecipe(AssemblyRecipe recipe, ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (AssemblyRecipe.SizedIngredient input : recipe.getInputs()) {
            if (input.ingredient().test(stack)) {
                return true;

            }
        }
        return false;

    }

    public float getRenderProgress(float pPartialTick) {
        return this.prevAnimationProgress + (this.animationProgress - this.prevAnimationProgress) * pPartialTick;

    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cyber_ware_port.cyberware_workbench");

    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CyberwareWorkbenchMenu(pContainerId, pPlayerInventory, this);

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyItemHandler.cast();

            }
            if (side == Direction.DOWN) {
                return lazyOutputHandler.cast();

            }
            return lazyInputHandler.cast();

        }
        return super.getCapability(cap, side);

    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyInputHandler = LazyOptional.of(() -> automationInputHandler);
        lazyOutputHandler = LazyOptional.of(() -> automationOutputHandler);

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyInputHandler.invalidate();
        lazyOutputHandler.invalidate();

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("workbench.progress", this.progress);
        pTag.putBoolean("workbench.isCrafting", this.isCrafting);
        pTag.putInt("workbench.cooldown", this.cooldown);
        super.saveAdditional(pTag);

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        this.progress = pTag.getInt("workbench.progress");
        this.isCrafting = pTag.getBoolean("workbench.isCrafting");
        this.cooldown = pTag.getInt("workbench.cooldown");
        this.cachedRecipe = null;

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