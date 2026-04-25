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
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.network.PacketDistributor;

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
    private Button toggleButton;
    private Button prevButton;
    private Button nextButton;
    private Button prevBlueprintBtn;
    private Button nextBlueprintBtn;

    public CyberwareWorkbenchScreen(CyberwareWorkbenchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.prevButton = Button.builder(Component.literal("<"), (btn) -> PacketDistributor.sendToServer(new ComponentChangePagePacket(-1, 0)))
                .bounds(0, 0, 15, 20).build();
        this.nextButton = Button.builder(Component.literal(">"), (btn) -> PacketDistributor.sendToServer(new ComponentChangePagePacket(1, 0)))
                .bounds(0, 0, 15, 20).build();
        this.addRenderableWidget(prevButton);
        this.addRenderableWidget(nextButton);
        this.prevBlueprintBtn = Button.builder(Component.literal("<"), (btn) -> PacketDistributor.sendToServer(new ComponentChangePagePacket(-1, 1)))
                .bounds(0, 0, 15, 20).build();
        this.nextBlueprintBtn = Button.builder(Component.literal(">"), (btn) -> PacketDistributor.sendToServer(new ComponentChangePagePacket(1, 1)))
                .bounds(0, 0, 15, 20).build();
        this.addRenderableWidget(prevBlueprintBtn);
        this.addRenderableWidget(nextBlueprintBtn);
        this.toggleButton = Button.builder(Component.literal("≡"), (btn) -> {
            boolean newState = !this.menu.isExtendedOpen;
            PacketDistributor.sendToServer(new ComponentToggleExtendTabPacket(newState));
            this.menu.isExtendedOpen = newState;
        }).bounds(this.leftPos + 5, this.topPos - 10, 12, 12).build();
        if (!this.menu.hasExtendedInventory && !this.menu.hasBlueprintLibrary) {
            this.toggleButton.visible = false;
        }
        this.addRenderableWidget(toggleButton);
        this.addRenderableWidget(new AbstractWidget(this.leftPos + 40, this.topPos + 35, 18, 18, Component.empty()) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                ItemStack inputStack = menu.getSlot(CyberwareWorkbenchBlockEntity.INPUT_SLOT).getItem();
                ItemStack blueprintStack = menu.getSlot(CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT).getItem();
                ItemStack paperStack = menu.getSlot(CyberwareWorkbenchBlockEntity.PAPER_SLOT).getItem();
                boolean hasPaper = paperStack.is(Items.PAPER);
                if (this.isHovered() && !inputStack.isEmpty()) {
                    guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x50FFFFFF);
                }
                if (this.isHovered()) {
                    List<Component> tooltip = new ArrayList<>();
                    if (!blueprintStack.isEmpty()) {
                        tooltip.add(Component.translatable("gui.cyber_ware_port.assemble").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN));
                    } else if (!inputStack.isEmpty()) {
                        tooltip.add(Component.translatable("gui.cyber_ware_port.deconstruct").withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
                        if (minecraft != null && minecraft.level != null) {
                            Optional<RecipeHolder<EngineeringRecipe>> recipeOpt = minecraft.level.getRecipeManager()
                                    .getRecipeFor(ModRecipes.ENGINEERING_TYPE.get(), new SingleRecipeInput(inputStack), minecraft.level);
                            if (recipeOpt.isPresent()) {
                                float chance = hasPaper ? recipeOpt.get().value().getBlueprintChance() : 0.0f;
                                tooltip.add(Component.translatable("gui.cyber_ware_port.blueprint_chance", String.format("%.0f", chance * 100)).withStyle(ChatFormatting.GRAY));
                            }
                        }
                    }
                    if (!tooltip.isEmpty()) {
                        guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
                    }
                }
            }

            @Override
            public void onClick(double pMouseX, double pMouseY) {
                PacketDistributor.sendToServer(new StartWorkbenchCraftingPacket());
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
                this.defaultButtonNarrationText(pNarrationElementOutput);
            }
        });
        updateButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        float target = this.menu.isExtendedOpen ? 1.0f : 0.0f;
        if (Math.abs(slideProgress - target) > 0.01f) {
            slideProgress += (target - slideProgress) * 0.2f;
        } else {
            slideProgress = target;
        }
        updateButtons();
    }

    private void updateButtons() {
        boolean isPanelVisible = this.menu.isExtendedOpen;
        boolean showLeftButtons = isPanelVisible && this.menu.hasExtendedInventory && this.menu.getMaxPages() > 1;
        this.prevButton.visible = showLeftButtons;
        this.nextButton.visible = showLeftButtons;
        if (showLeftButtons) {
            this.prevButton.active = (this.menu.getCurrentPage() > 0);
            this.nextButton.active = (this.menu.getCurrentPage() < this.menu.getMaxPages() - 1);
            int panelOriginX = (int) (-61 * slideProgress);
            this.prevButton.setX(this.leftPos + panelOriginX + 5);
            this.prevButton.setY(this.topPos + 137);
            this.nextButton.setX(this.leftPos + panelOriginX + 47);
            this.nextButton.setY(this.topPos + 137);
        }
        boolean showRightButtons = isPanelVisible && this.menu.hasBlueprintLibrary && this.menu.getBlueprintMaxPages() > 1;
        this.prevBlueprintBtn.visible = showRightButtons;
        this.nextBlueprintBtn.visible = showRightButtons;
        if (showRightButtons) {
            this.prevBlueprintBtn.active = (this.menu.getBlueprintCurrentPage() > 0);
            this.nextBlueprintBtn.active = (this.menu.getBlueprintCurrentPage() < this.menu.getBlueprintMaxPages() - 1);
            int panelWidth = 61;
            int slideOffset = (int) (panelWidth * slideProgress);
            int panelDrawX = this.leftPos + 176 - panelWidth + slideOffset;
            this.prevBlueprintBtn.setX(panelDrawX + 5);
            this.prevBlueprintBtn.setY(this.topPos + 137);
            this.nextBlueprintBtn.setX(panelDrawX + 47);
            this.nextBlueprintBtn.setY(this.topPos + 137);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        if (this.menu.hasExtendedInventory && slideProgress > 0.01f) {
            int drawX = x - (int) (61 * slideProgress) + 2;
            guiGraphics.blit(COMPONENT_BOX_TEXTURE, drawX, y, 0, 0, 61, 141, 256, 256);
            if (this.menu.getMaxPages() > 1 && slideProgress > 0.8f) {
                guiGraphics.drawCenteredString(this.font, (this.menu.getCurrentPage() + 1) + "/" + this.menu.getMaxPages(), drawX + 32, y + 129, 0xFFFFFF);
            }
        }
        if (this.menu.hasBlueprintLibrary && slideProgress > 0.01f) {
            int drawX = x + 176 - 61 + (int) (61 * slideProgress);
            guiGraphics.blit(BLUEPRINT_PANEL_TEXTURE, drawX, y, 0, 0, 61, 141, 256, 256);
            if (this.menu.getBlueprintMaxPages() > 1 && slideProgress > 0.8f) {
                guiGraphics.drawCenteredString(this.font, (this.menu.getBlueprintCurrentPage() + 1) + "/" + this.menu.getBlueprintMaxPages(), drawX + 32, y + 129, 0xFFFFFF);
            }
        }
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        renderBlueprintGhosts(guiGraphics);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);
    }

    private void renderBlueprintGhosts(GuiGraphics guiGraphics) {
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
                for (RecipeHolder<AssemblyRecipe> holder : this.minecraft.level.getRecipeManager().getAllRecipesFor(ModRecipes.ASSEMBLY_TYPE.get())) {
                    if (holder.value().getResultItem(this.minecraft.level.registryAccess()).is(targetItem)) {
                        cachedIngredients = holder.value().getInputs();
                        break;
                    }
                }
            }
        }
        if (cachedIngredients != null) {
            for (int i = 0; i < Math.min(cachedIngredients.size(), 6); i++) {
                AssemblyRecipe.SizedIngredient req = cachedIngredients.get(i);
                ItemStack[] items = req.ingredient().getItems();
                if (items.length == 0) continue;
                Slot targetSlot = this.menu.getSlot(3 + i);
                int x = this.leftPos + targetSlot.x;
                int y = this.topPos + targetSlot.y;
                ItemStack stackInSlot = targetSlot.getItem();
                if (stackInSlot.isEmpty() || !req.ingredient().test(stackInSlot) || stackInSlot.getCount() < req.count()) {
                    guiGraphics.renderItem(items[0], x, y);
                    RenderSystem.enableBlend();
                    guiGraphics.fill(x, y, x + 16, y + 16, 0x80000000);
                    RenderSystem.disableBlend();
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                    guiGraphics.drawString(this.font, String.valueOf(req.count()), x + 10, y + 10, 0xFF5555, true);
                    guiGraphics.pose().popPose();
                }
            }
        }
    }
}