package com.maxwell.cyber_ware_port.common.block.cwb;

import com.maxwell.cyber_ware_port.api.event.CyberwareEvents;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import com.maxwell.cyber_ware_port.init.ModRecipes;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
            if (slot >= OUTPUT_SLOT_START) return true;
            return switch (slot) {
                case INPUT_SLOT -> CyberwareAPI.isCyberware(stack);
                case PAPER_SLOT -> stack.is(Items.PAPER);
                case BLUEPRINT_SLOT -> stack.getItem() instanceof BlueprintItem;
                default -> false;
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
            return INVENTORY_SIZE;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return itemHandler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) return stack;
            if (slot == PAPER_SLOT && stack.is(Items.PAPER)) return itemHandler.insertItem(PAPER_SLOT, stack, simulate);
            if (slot == BLUEPRINT_SLOT && stack.getItem() instanceof BlueprintItem)
                return itemHandler.insertItem(BLUEPRINT_SLOT, stack, simulate);
            if (slot == INPUT_SLOT && CyberwareAPI.getCyberware(stack) != null)
                return itemHandler.insertItem(INPUT_SLOT, stack, simulate);
            if (slot >= OUTPUT_SLOT_START && slot < SPECIAL_OUTPUT_SLOT) {
                AssemblyRecipe activeRecipe = getActiveAssemblyRecipe();
                if (activeRecipe != null && isItemNeededForRecipe(activeRecipe, stack))
                    return itemHandler.insertItem(slot, stack, simulate);
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return itemHandler.extractItem(slot, amount, simulate);
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
    private boolean isCrafting = false;
    private int cooldown = 0;

    public CyberwareWorkbenchBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CYBERWARE_WORKBENCH.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CyberwareWorkbenchBlockEntity pBlockEntity) {
        pBlockEntity.prevAnimationProgress = pBlockEntity.animationProgress;
        if (pBlockEntity.cooldown > 0) pBlockEntity.cooldown--;
        float target = pBlockEntity.isCrafting ? 1.0F : 0.0F;
        if (pBlockEntity.animationProgress < target)
            pBlockEntity.animationProgress = Math.min(pBlockEntity.animationProgress + 0.5F, target);
        else if (pBlockEntity.animationProgress > target)
            pBlockEntity.animationProgress = Math.max(pBlockEntity.animationProgress - 0.5F, target);
        if (!pLevel.isClientSide) {
            if (pLevel.hasNeighborSignal(pPos)) pBlockEntity.startCrafting();
            if (pBlockEntity.cooldown == 0 && pBlockEntity.isCrafting) {
                pLevel.playSound(null, pPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, 1.2F);
                pBlockEntity.cooldown = 3;
                pBlockEntity.craftItem();
            }
            if (pBlockEntity.isCrafting && pLevel.getGameTime() % 40 == 0) {
                pLevel.playSound(null, pPos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.2F, 1.2F);
                pLevel.playSound(null, pPos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 0.3F, 1.5F);
            }
            if (pBlockEntity.isCrafting && pBlockEntity.animationProgress >= 1.0F) {
                pLevel.playSound(null, pPos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.5F, 1.2F);
                pBlockEntity.resetCrafting();
            }
        }
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) inventory.setItem(i, itemHandler.getStackInSlot(i));
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
        if (this.level != null)
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    private boolean checkOrConsumeIngredients(AssemblyRecipe recipe, boolean consume) {
        for (AssemblyRecipe.SizedIngredient req : recipe.getInputs()) {
            int needed = req.count();
            int found = 0;
            for (int i = OUTPUT_SLOT_START; i < SPECIAL_OUTPUT_SLOT; i++) {
                ItemStack stack = this.itemHandler.getStackInSlot(i);
                if (req.ingredient().test(stack)) {
                    int take = Math.min(stack.getCount(), needed - found);
                    if (consume) this.itemHandler.extractItem(i, take, false);
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
                ItemStack result = recipe.getResultItem(this.level.registryAccess());
                ItemStack currentOutput = this.itemHandler.getStackInSlot(SPECIAL_OUTPUT_SLOT);
                if (currentOutput.isEmpty()) return true;
                return ItemStack.isSameItem(currentOutput, result) && currentOutput.getCount() + result.getCount() <= currentOutput.getMaxStackSize();
            }
            return false;
        }
        ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
        if (inputStack.isEmpty()) return false;
        var recipeOpt = this.level.getRecipeManager().getRecipeFor(ModRecipes.ENGINEERING_TYPE.get(), new SingleRecipeInput(inputStack), this.level);
        if (recipeOpt.isPresent()) {
            for (int i = OUTPUT_SLOT_START; i < SPECIAL_OUTPUT_SLOT; i++)
                if (this.itemHandler.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    private ItemStack mergeIntoOutput(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int i = OUTPUT_SLOT_START; i < SPECIAL_OUTPUT_SLOT; i++) {
            remainder = this.itemHandler.insertItem(i, remainder, false);
            if (remainder.isEmpty()) return ItemStack.EMPTY;
        }
        return remainder;
    }

    public float getRenderProgress(float pPartialTick) {
        return net.minecraft.util.Mth.lerp(pPartialTick, this.prevAnimationProgress, this.animationProgress);
    }

    private void craftItem() {
        AssemblyRecipe recipe = getActiveAssemblyRecipe();
        if (recipe != null) {
            if (checkOrConsumeIngredients(recipe, true)) {
                ItemStack result = recipe.getResultItem(this.level.registryAccess()).copy();
                ICyberware cw = CyberwareAPI.getCyberware(result);
                if (cw != null) cw.setPristine(result, true);
                this.itemHandler.insertItem(SPECIAL_OUTPUT_SLOT, result, false);
                if (CyberwareConfig.CONSUME_BLUEPRINT.get()) this.itemHandler.extractItem(BLUEPRINT_SLOT, 1, false);
            }
            return;
        }
        ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
        if (inputStack.isEmpty()) return;
        var recipeOpt = this.level.getRecipeManager().getRecipeFor(ModRecipes.ENGINEERING_TYPE.get(), new SingleRecipeInput(inputStack), this.level);
        if (recipeOpt.isPresent()) {
            EngineeringRecipe engRecipe = recipeOpt.get().value();
            float baseChance = engRecipe.getBlueprintChance();
            CyberwareEvents.Salvage.Pre preEvent = new CyberwareEvents.Salvage.Pre(this, inputStack, baseChance);
            if (NeoForge.EVENT_BUS.post(preEvent).isCanceled()) return;
            List<ItemStack> results = engRecipe.rollOutputs(this.level.random);
            CyberwareEvents.Salvage.Post postEvent = new CyberwareEvents.Salvage.Post(this, inputStack, results);
            NeoForge.EVENT_BUS.post(postEvent);
            this.itemHandler.extractItem(INPUT_SLOT, 1, false);
            for (ItemStack result : postEvent.getOutputs()) {
                ItemStack remainder = mergeIntoOutput(result);
                if (!remainder.isEmpty()) Block.popResource(this.level, this.worldPosition.above(), remainder);
            }
            ItemStack paperStack = this.itemHandler.getStackInSlot(PAPER_SLOT);
            if (paperStack.is(Items.PAPER) && this.level.random.nextFloat() < preEvent.getBlueprintChance()) {
                ItemStack blueprint = BlueprintItem.createBlueprintFor(inputStack.getItem());
                if (mergeIntoOutput(blueprint).isEmpty()) this.itemHandler.extractItem(PAPER_SLOT, 1, false);
            }
        }
    }

    @Nullable
    private AssemblyRecipe getActiveAssemblyRecipe() {
        if (this.cachedRecipe != null) return this.cachedRecipe;
        if (this.level == null) return null;
        ItemStack blueprintStack = this.itemHandler.getStackInSlot(BLUEPRINT_SLOT);
        if (blueprintStack.isEmpty() || !(blueprintStack.getItem() instanceof BlueprintItem)) return null;
        Item targetItem = BlueprintItem.getTargetItem(blueprintStack);
        if (targetItem == null) return null;
        for (RecipeHolder<AssemblyRecipe> holder : this.level.getRecipeManager().getAllRecipesFor(ModRecipes.ASSEMBLY_TYPE.get())) {
            if (holder.value().getResultItem(this.level.registryAccess()).getItem() == targetItem) {
                this.cachedRecipe = holder.value();
                return this.cachedRecipe;
            }
        }
        return null;
    }

    private boolean isItemNeededForRecipe(AssemblyRecipe recipe, ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (AssemblyRecipe.SizedIngredient input : recipe.getInputs()) if (input.ingredient().test(stack)) return true;
        return false;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.cyber_ware_port.cyberware_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CyberwareWorkbenchMenu(pContainerId, pPlayerInventory, this);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("workbench.progress", this.progress);
        pTag.putBoolean("workbench.isCrafting", this.isCrafting);
        pTag.putInt("workbench.cooldown", this.cooldown);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("inventory")) itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        this.progress = pTag.getInt("workbench.progress");
        this.isCrafting = pTag.getBoolean("workbench.isCrafting");
        this.cooldown = pTag.getInt("workbench.cooldown");
        this.cachedRecipe = null;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }
}