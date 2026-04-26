package com.maxwell.cyber_ware_port.client.screen.cwb;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberwareWorkbenchBlockEntity;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.common.network.ComponentChangePagePacket;
import com.maxwell.cyber_ware_port.common.network.ComponentToggleExtendTabPacket;
import com.maxwell.cyber_ware_port.common.network.StartWorkbenchCraftingPacket;
import com.maxwell.cyber_ware_port.init.ModRecipes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CyberwareWorkbenchScreen extends AbstractContainerScreen<CyberwareWorkbenchMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/engineering.png");
    private static final Identifier COMPONENT_BOX_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/component_box.png");
    private static final Identifier BLUEPRINT_PANEL_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/blueprint_chest.png");
    private ItemStack cachedBlueprint = ItemStack.EMPTY;
    private List<AssemblyRecipe.SizedIngredient> cachedIngredients = null;
    private float slideProgress = 1.0f;
    private Button toggleButton, prevButton, nextButton, prevBlueprintBtn, nextBlueprintBtn;

    public CyberwareWorkbenchScreen(CyberwareWorkbenchMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.prevButton = Button.builder(Component.literal("<"), (btn) -> ClientPacketDistributor.sendToServer(new ComponentChangePagePacket(-1, 0))).bounds(0, 0, 15, 20).build();
        this.nextButton = Button.builder(Component.literal(">"), (btn) -> ClientPacketDistributor.sendToServer(new ComponentChangePagePacket(1, 0))).bounds(0, 0, 15, 20).build();
        this.prevBlueprintBtn = Button.builder(Component.literal("<"), (btn) -> ClientPacketDistributor.sendToServer(new ComponentChangePagePacket(-1, 1))).bounds(0, 0, 15, 20).build();
        this.nextBlueprintBtn = Button.builder(Component.literal(">"), (btn) -> ClientPacketDistributor.sendToServer(new ComponentChangePagePacket(1, 1))).bounds(0, 0, 15, 20).build();
        this.addRenderableWidget(prevButton);
        this.addRenderableWidget(nextButton);
        this.addRenderableWidget(prevBlueprintBtn);
        this.addRenderableWidget(nextBlueprintBtn);
        this.toggleButton = Button.builder(Component.literal("≡"), (btn) -> {
            boolean newState = !this.menu.isExtendedOpen;
            ClientPacketDistributor.sendToServer(new ComponentToggleExtendTabPacket(newState));
            this.menu.isExtendedOpen = newState;
        }).bounds(this.leftPos + 5, this.topPos - 10, 12, 12).build();
        if (!this.menu.hasExtendedInventory && !this.menu.hasBlueprintLibrary) this.toggleButton.visible = false;
        this.addRenderableWidget(toggleButton);
        this.addRenderableWidget(new AbstractWidget(this.leftPos + 40, this.topPos + 35, 18, 18, Component.empty()) {
            @Override
            protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                ItemStack inputStack = menu.getSlot(CyberwareWorkbenchBlockEntity.INPUT_SLOT).getItem();
                ItemStack blueprintStack = menu.getSlot(CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT).getItem();
                ItemStack paperStack = menu.getSlot(CyberwareWorkbenchBlockEntity.PAPER_SLOT).getItem();
                if (this.isHovered && !inputStack.isEmpty()) {
                    graphics.fill(0, 0, this.width, this.height, 0x50FFFFFF);
                }
                if (this.isHovered) {
                    List<Component> tooltip = new ArrayList<>();
                    if (!blueprintStack.isEmpty()) {
                        tooltip.add(Component.translatable("gui.cyber_ware_port.assemble").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN));
                    } else if (!inputStack.isEmpty()) {
                        tooltip.add(Component.translatable("gui.cyber_ware_port.deconstruct").withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
                        if (minecraft != null && minecraft.level != null) {
                            var recipeRegistry = minecraft.level.registryAccess().lookupOrThrow(Registries.RECIPE);
                            Optional<RecipeHolder<EngineeringRecipe>> recipeOpt = recipeRegistry.listElements()
                                    .filter(holder -> {
                                        Recipe<?> recipe = holder.value();
                                        return recipe instanceof EngineeringRecipe engineeringRecipe &&
                                                engineeringRecipe.getType() == ModRecipes.ENGINEERING_TYPE.get() &&
                                                engineeringRecipe.matches(new SingleRecipeInput(inputStack), minecraft.level);
                                    })
                                    .map(holder -> {
                                        return new RecipeHolder<>((ResourceKey<Recipe<?>>) holder.key(), (EngineeringRecipe) holder.value());
                                    })
                                    .findFirst();
                            if (recipeOpt.isPresent()) {
                                float chance = paperStack.is(Items.PAPER) ? recipeOpt.get().value().blueprintChance() : 0.0f;
                                tooltip.add(Component.translatable("gui.cyber_ware_port.blueprint_chance", String.format("%.0f", chance * 100)).withStyle(ChatFormatting.GRAY));
                            }
                        }
                    }
                    if (!tooltip.isEmpty()) {
                        graphics.setTooltipForNextFrame(Minecraft.getInstance().font, tooltip, Optional.empty(), ItemStack.EMPTY, mouseX, mouseY);
                    }
                }
            }

            @Override
            public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
                ClientPacketDistributor.sendToServer(new StartWorkbenchCraftingPacket());
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput output) {
                this.defaultButtonNarrationText(output);
            }
        });
        updateButtons();
    }

    /**
     * GUI背景とサイドパネル、ゴーストアイテムの抽出。
     * ここは leftPos/topPos に translate 済み (相対座標 0,0 起点)。
     */
    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (this.menu.hasExtendedInventory && slideProgress > 0.01f) {
            int drawX = -(int) (61 * slideProgress) + 2;
            graphics.blit(RenderPipelines.GUI_TEXTURED, COMPONENT_BOX_TEXTURE, drawX, 0, 0, 0, 61, 141, 256, 256);
        }
        if (this.menu.hasBlueprintLibrary && slideProgress > 0.01f) {
            int drawX = 176 - 61 + (int) (61 * slideProgress);
            graphics.blit(RenderPipelines.GUI_TEXTURED, BLUEPRINT_PANEL_TEXTURE, drawX, 0, 0, 0, 61, 141, 256, 256);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        renderBlueprintGhosts(graphics);
        super.extractContents(graphics, mouseX, mouseY, a);
    }

    /**
     * テキストラベルの抽出。相対座標。
     */
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        if (this.menu.hasExtendedInventory && slideProgress > 0.8f && this.menu.getMaxPages() > 1) {
            int drawX = -(int) (61 * slideProgress) + 2 + 32;
            String text = (this.menu.getCurrentPage() + 1) + "/" + this.menu.getMaxPages();
            graphics.centeredText(this.font, text, drawX, 129, 0xFFFFFFFF);
        }
        if (this.menu.hasBlueprintLibrary && slideProgress > 0.8f && this.menu.getBlueprintMaxPages() > 1) {
            int drawX = 176 - 61 + (int) (61 * slideProgress) + 32;
            String text = (this.menu.getBlueprintCurrentPage() + 1) + "/" + this.menu.getBlueprintMaxPages();
            graphics.centeredText(this.font, text, drawX, 129, 0xFFFFFFFF);
        }
    }

    private void renderBlueprintGhosts(GuiGraphicsExtractor graphics) {
        ItemStack currentBlueprint = this.menu.getSlot(CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT).getItem();
        if (currentBlueprint.isEmpty() || !(currentBlueprint.getItem() instanceof BlueprintItem)) {
            cachedBlueprint = ItemStack.EMPTY;
            cachedIngredients = null;
            return;
        }
        if (!ItemStack.isSameItemSameComponents(cachedBlueprint, currentBlueprint) || cachedIngredients == null) {
            cachedBlueprint = currentBlueprint.copy();
            Item targetItem = BlueprintItem.getTargetItem(currentBlueprint);
            if (targetItem != null && this.minecraft != null && this.minecraft.level != null) {
                var recipeRegistry = this.minecraft.level.registryAccess().lookupOrThrow(Registries.RECIPE);
                this.cachedIngredients = recipeRegistry.listElements()
                        .map(holder -> (Recipe<?>) holder.value())
                        .filter(recipe ->
                                recipe instanceof AssemblyRecipe assemblyRecipe &&
                                        assemblyRecipe.getType() == ModRecipes.ASSEMBLY_TYPE.get() &&
                                        assemblyRecipe.getResultItem(this.minecraft.level.registryAccess()).is(targetItem)
                        )
                        .map(recipe -> ((AssemblyRecipe) recipe).getInputs())
                        .findFirst()
                        .orElse(null);
            }
        }
        if (cachedIngredients != null) {
            for (int i = 0; i < Math.min(cachedIngredients.size(), 6); i++) {
                AssemblyRecipe.SizedIngredient req = cachedIngredients.get(i);
                var items = req.ingredient().items();
                if (items.findAny().isEmpty()) continue;
                ItemStack displayStack = new ItemStack(req.ingredient().items().findFirst().get());
                Slot targetSlot = this.menu.getSlot(3 + i);
                int x = targetSlot.x;
                int y = targetSlot.y;
                ItemStack stackInSlot = targetSlot.getItem();
                if (stackInSlot.isEmpty() || !req.ingredient().test(stackInSlot) || stackInSlot.getCount() < req.count()) {
                    graphics.item(displayStack, x, y);
                    graphics.nextStratum();
                    graphics.fill(x, y, x + 16, y + 16, 0x80000000);
                    graphics.text(this.font, String.valueOf(req.count()), x + 10, y + 10, 0xFFFF5555, true);
                }
            }
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        float target = this.menu.isExtendedOpen ? 1.0f : 0.0f;
        slideProgress += (target - slideProgress) * 0.2f;
        if (Math.abs(slideProgress - target) < 0.01f) slideProgress = target;
        updateButtons();
    }

    private void updateButtons() {
        boolean isPanelVisible = this.menu.isExtendedOpen;
        boolean showLeft = isPanelVisible && this.menu.hasExtendedInventory && this.menu.getMaxPages() > 1;
        this.prevButton.visible = showLeft;
        this.nextButton.visible = showLeft;
        if (showLeft) {
            this.prevButton.active = this.menu.getCurrentPage() > 0;
            this.nextButton.active = this.menu.getCurrentPage() < this.menu.getMaxPages() - 1;
            int ox = (int) (-61 * slideProgress);
            this.prevButton.setPosition(this.leftPos + ox + 5, this.topPos + 137);
            this.nextButton.setPosition(this.leftPos + ox + 47, this.topPos + 137);
        }
        boolean showRight = isPanelVisible && this.menu.hasBlueprintLibrary && this.menu.getBlueprintMaxPages() > 1;
        this.prevBlueprintBtn.visible = showRight;
        this.nextBlueprintBtn.visible = showRight;
        if (showRight) {
            this.prevBlueprintBtn.active = this.menu.getBlueprintCurrentPage() > 0;
            this.nextBlueprintBtn.active = this.menu.getBlueprintCurrentPage() < this.menu.getBlueprintMaxPages() - 1;
            int drawX = 176 - 61 + (int) (61 * slideProgress);
            this.prevBlueprintBtn.setPosition(this.leftPos + drawX + 5, this.topPos + 137);
            this.nextBlueprintBtn.setPosition(this.leftPos + drawX + 47, this.topPos + 137);
        }
    }
}