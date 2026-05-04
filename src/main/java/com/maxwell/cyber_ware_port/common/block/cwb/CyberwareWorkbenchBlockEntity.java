package com.maxwell.cyber_ware_port.common.block.cwb;

import com.maxwell.cyber_ware_port.api.event.CyberwareEvents;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.component_box.ComponentBoxBlockEntity;
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
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
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
        float speed = 0.1F;
        if (pBlockEntity.animationProgress < target)
            pBlockEntity.animationProgress = Math.min(pBlockEntity.animationProgress + speed, target);
        else if (pBlockEntity.animationProgress > target)
            pBlockEntity.animationProgress = Math.max(pBlockEntity.animationProgress - speed, target);
        if (!pLevel.isClientSide()) {
            if (pLevel.hasNeighborSignal(pPos)) pBlockEntity.startCrafting();
            if (pBlockEntity.cooldown == 0 && pBlockEntity.isCrafting) {
                pLevel.playSound(null, pPos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, 1.2F);
                pBlockEntity.cooldown = 10;
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

    public boolean isCrafting() {
        return isCrafting;
    }

    private ItemStack getStack(int slot) {
        return itemHandler.getResource(slot).toStack((int) itemHandler.getAmountAsLong(slot));
    }

    public void drops() {
        if (this.level == null) return;
        for (int i = 0; i < itemHandler.size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty())
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
        }
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
        try (Transaction tx = consume ? Transaction.openRoot() : null) {
            for (AssemblyRecipe.SizedIngredient req : recipe.getInputs()) {
                int needed = req.count();
                int found = 0;
                for (int i = OUTPUT_SLOT_START; i < SPECIAL_OUTPUT_SLOT; i++) {
                    ItemStack stack = getStack(i);
                    if (req.ingredient().test(stack)) {
                        int take = Math.min(stack.getCount(), needed - found);
                        if (consume) {
                            this.itemHandler.extract(i, ItemResource.of(stack), take, tx);
                        }
                        found += take;
                        if (found >= needed) break;
                    }
                }
                if (found < needed) return false;
            }
            if (tx != null) tx.commit();
        }
        return true;
    }

    private boolean canCraft() {
        AssemblyRecipe recipe = getActiveAssemblyRecipe();
        if (recipe != null) {
            if (checkOrConsumeIngredients(recipe, false)) {
                ItemStack result = recipe.getResultItem(this.level.registryAccess());
                ItemStack currentOutput = getStack(SPECIAL_OUTPUT_SLOT);
                if (currentOutput.isEmpty()) return true;
                return ItemStack.isSameItem(currentOutput, result) && currentOutput.getCount() + result.getCount() <= currentOutput.getMaxStackSize();
            }
            return false;
        }
        ItemStack inputStack = getStack(INPUT_SLOT);
        if (inputStack.isEmpty()) return false;
        if (this.level instanceof ServerLevel serverLevel) {
            RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();
            var recipeOpt = recipeManager.getRecipeFor(
                    ModRecipes.ENGINEERING_TYPE.get(),
                    new SingleRecipeInput(inputStack),
                    serverLevel
            );
            if (recipeOpt.isPresent()) {
                for (int i = OUTPUT_SLOT_START; i < SPECIAL_OUTPUT_SLOT; i++)
                    if (this.itemHandler.getResource(i).isEmpty()) return true;
            }
        }
        return false;
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
                try (Transaction tx = Transaction.openRoot()) {
                    this.itemHandler.insert(SPECIAL_OUTPUT_SLOT, ItemResource.of(result), result.getCount(), tx);
                    if (CyberwareConfig.CONSUME_BLUEPRINT.get()) {
                        ItemStack bp = getStack(BLUEPRINT_SLOT);
                        this.itemHandler.extract(BLUEPRINT_SLOT, ItemResource.of(bp), 1, tx);
                    }
                    tx.commit();
                }
            }
            return;
        }
        ItemStack inputStack = getStack(INPUT_SLOT);
        if (inputStack.isEmpty()) return;
        if (this.level instanceof ServerLevel serverLevel) {
            RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();
            var recipeOpt = recipeManager.getRecipeFor(
                    ModRecipes.ENGINEERING_TYPE.get(),
                    new SingleRecipeInput(inputStack),
                    serverLevel
            );
            if (recipeOpt.isPresent()) {
                EngineeringRecipe engRecipe = recipeOpt.get().value();
                float baseChance = engRecipe.blueprintChance();
                CyberwareEvents.Salvage.Pre preEvent = new CyberwareEvents.Salvage.Pre(this, inputStack, baseChance);
                if (NeoForge.EVENT_BUS.post(preEvent).isCanceled()) return;
                List<ItemStack> results = engRecipe.rollOutputs(this.level.getRandom());
                CyberwareEvents.Salvage.Post postEvent = new CyberwareEvents.Salvage.Post(this, inputStack, results);
                NeoForge.EVENT_BUS.post(postEvent);
                try (Transaction tx = Transaction.openRoot()) {
                    this.itemHandler.extract(INPUT_SLOT, ItemResource.of(inputStack), 1, tx);
                    for (ItemStack result : postEvent.getOutputs()) {
                        ItemStack remainder = mergeIntoOutput(result, tx);
                        if (!remainder.isEmpty()) Block.popResource(this.level, this.worldPosition.above(), remainder);
                    }
                    ItemStack paperStack = getStack(PAPER_SLOT);
                    if (paperStack.is(Items.PAPER) && this.level.getRandom().nextFloat() < preEvent.getBlueprintChance()) {
                        ItemStack blueprint = BlueprintItem.createBlueprintFor(inputStack.getItem());
                        if (mergeIntoOutput(blueprint, tx).isEmpty()) {
                            this.itemHandler.extract(PAPER_SLOT, ItemResource.of(paperStack), 1, tx);
                        }
                    }
                    tx.commit();
                }
            }
        }
    }

    @Nullable
    private AssemblyRecipe getActiveAssemblyRecipe() {
        if (this.cachedRecipe != null) return this.cachedRecipe;
        if (this.level instanceof ServerLevel serverLevel) {
            ItemStack blueprintStack = getStack(BLUEPRINT_SLOT);
            if (blueprintStack.isEmpty() || !(blueprintStack.getItem() instanceof BlueprintItem)) return null;
            Item targetItem = BlueprintItem.getTargetItem(blueprintStack);
            if (targetItem == null) return null;
            RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();
            for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
                if (holder.value().getType() == ModRecipes.ASSEMBLY_TYPE.get()) {
                    RecipeHolder<AssemblyRecipe> assemblyHolder = (RecipeHolder<AssemblyRecipe>) holder;
                    if (assemblyHolder.value().getResultItem(this.level.registryAccess()).getItem() == targetItem) {
                        this.cachedRecipe = assemblyHolder.value();
                        return this.cachedRecipe;
                    }
                }
            }
        }
        return null;
    }

    private ItemStack mergeIntoOutput(ItemStack stack, Transaction tx) {
        int remaining = stack.getCount();
        ItemResource resource = ItemResource.of(stack);
        for (int i = OUTPUT_SLOT_START; i < SPECIAL_OUTPUT_SLOT; i++) {
            int inserted = this.itemHandler.insert(i, resource, remaining, tx);
            remaining -= inserted;
            if (remaining <= 0) return ItemStack.EMPTY;
        }
        BlockPos center = this.worldPosition;
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    BlockPos pos = center.offset(x, y, z);
                    if (level.getBlockEntity(pos) instanceof ComponentBoxBlockEntity box) {
                        var handler = box.getItemHandler();
                        for (int i = 0; i < handler.size(); i++) {
                            int inserted = handler.insert(i, resource, remaining, tx);
                            remaining -= inserted;
                            if (remaining <= 0) return ItemStack.EMPTY;
                        }
                    }
                }
            }
        }
        return stack.copyWithCount(remaining);
    }

    private boolean isItemNeededForRecipe(AssemblyRecipe recipe, ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (AssemblyRecipe.SizedIngredient input : recipe.getInputs()) if (input.ingredient().test(stack)) return true;
        return false;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.cyber_ware_port.cyberware_workbench");
    }    private final ItemStacksResourceHandler itemHandler = new ItemStacksResourceHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            if (index == BLUEPRINT_SLOT) {
                cachedRecipe = null;
            }
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            ItemStack stack = resource.toStack();
            if (slot >= OUTPUT_SLOT_START) return true;
            return switch (slot) {
                case INPUT_SLOT -> CyberwareAPI.isCyberware(stack);
                case PAPER_SLOT -> stack.is(Items.PAPER);
                case BLUEPRINT_SLOT -> stack.getItem() instanceof BlueprintItem;
                default -> false;
            };
        }

        @Override
        public int insert(int slot, ItemResource resource, int amount, TransactionContext tx) {
            ItemStack stack = resource.toStack();
            if (slot == PAPER_SLOT && !stack.is(Items.PAPER)) return 0;
            if (slot == BLUEPRINT_SLOT && !(stack.getItem() instanceof BlueprintItem)) return 0;
            if (slot == INPUT_SLOT && !CyberwareAPI.isCyberware(stack)) return 0;
            if (slot >= OUTPUT_SLOT_START && slot < SPECIAL_OUTPUT_SLOT) {
                AssemblyRecipe activeRecipe = getActiveAssemblyRecipe();
                if (activeRecipe != null && !isItemNeededForRecipe(activeRecipe, stack)) return 0;
            }
            return super.insert(slot, resource, amount, tx);
        }
    };

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CyberwareWorkbenchMenu(pContainerId, pPlayerInventory, this);
    }

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.itemHandler.serialize(output.child("inventory"));
        output.putInt("workbench.progress", this.progress);
        output.putBoolean("workbench.isCrafting", this.isCrafting);
        output.putInt("workbench.cooldown", this.cooldown);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemHandler.deserialize(input.childOrEmpty("inventory"));
        this.progress = input.getIntOr("workbench.progress", 0);
        this.isCrafting = input.getBooleanOr("workbench.isCrafting", false);
        this.cooldown = input.getIntOr("workbench.cooldown", 0);
        this.cachedRecipe = null;
    }

    @Override
    public void onDataPacket(Connection net, ValueInput valueInput) {
        this.loadAdditional(valueInput);
        super.onDataPacket(net, valueInput);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return super.getUpdateTag(pRegistries);
    }



}