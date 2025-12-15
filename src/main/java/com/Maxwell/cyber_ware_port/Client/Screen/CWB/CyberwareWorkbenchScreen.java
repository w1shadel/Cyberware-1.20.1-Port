package com.Maxwell.cyber_ware_port.Client.Screen.CWB;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberwareWorkbenchBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.AssemblyRecipe;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.EngineeringRecipe;
import com.Maxwell.cyber_ware_port.Common.Container.CyberwareWorkbenchMenu;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.Common.Network.A_PacketHandler;
import com.Maxwell.cyber_ware_port.Common.Network.ComponentChangePagePacket;
import com.Maxwell.cyber_ware_port.Common.Network.ComponentToggleExtendTabPacket;
import com.Maxwell.cyber_ware_port.Common.Network.StartWorkbenchCraftingPacket;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModRecipes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("removal")
public class CyberwareWorkbenchScreen extends AbstractContainerScreen<CyberwareWorkbenchMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/gui/engineering.png");
    private static final ResourceLocation COMPONENT_BOX_TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/gui/component_box.png");
    private static final ResourceLocation BLUEPRINT_PANEL_TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/gui/blueprint_chest.png");

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
        this.prevButton = Button.builder(Component.literal("<"), (btn) -> A_PacketHandler.INSTANCE.sendToServer(new ComponentChangePagePacket(-1, 0)))
                .pos(0, 0).size(15, 20).build();
        this.nextButton = Button.builder(Component.literal(">"), (btn) -> A_PacketHandler.INSTANCE.sendToServer(new ComponentChangePagePacket(1, 0)))
                .pos(0, 0).size(15, 20).build();
        this.addRenderableWidget(prevButton);
        this.addRenderableWidget(nextButton);
        this.prevBlueprintBtn = Button.builder(Component.literal("<"), (btn) -> A_PacketHandler.INSTANCE.sendToServer(new ComponentChangePagePacket(-1, 1)))
                .pos(0, 0).size(15, 20).build();
        this.nextBlueprintBtn = Button.builder(Component.literal(">"), (btn) -> A_PacketHandler.INSTANCE.sendToServer(new ComponentChangePagePacket(1, 1)))
                .pos(0, 0).size(15, 20).build();
        this.addRenderableWidget(prevBlueprintBtn);
        this.addRenderableWidget(nextBlueprintBtn);
        int toggleX = this.leftPos + 5;
        int toggleY = this.topPos - 10;
        this.toggleButton = Button.builder(Component.literal("≡"), (btn) -> {
            boolean newState = !this.menu.isExtendedOpen;
            A_PacketHandler.INSTANCE.sendToServer(new ComponentToggleExtendTabPacket(newState));
            this.menu.isExtendedOpen = newState;
        }).pos(toggleX, toggleY).size(12, 12).build();
        if (!this.menu.hasExtendedInventory && !this.menu.hasBlueprintLibrary) {
            this.toggleButton.visible = false;
        }
        this.addRenderableWidget(toggleButton);
        this.addRenderableWidget(new AbstractWidget(this.leftPos + 40, this.topPos + 35, 18, 18, Component.empty()) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                ItemStack inputStack = menu.getSlot(CyberwareWorkbenchBlockEntity.INPUT_SLOT).getItem();
                ItemStack blueprintStack = menu.getSlot(CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT).getItem();
                boolean hasInput = !inputStack.isEmpty();
                if (this.isHovered() && hasInput) {
                    guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x50FFFFFF);
                }
                if (this.isHovered()) {
                    List<Component> tooltip = new ArrayList<>();
                    if (!blueprintStack.isEmpty()) {
                        tooltip.add(Component.translatable("gui.cyber_ware_port.assemble").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN));
                    } else if (hasInput) {
                        tooltip.add(Component.translatable("gui.cyber_ware_port.deconstruct").withStyle(ChatFormatting.BOLD, ChatFormatting.RED));
                        if (minecraft != null && minecraft.level != null) {
                            SimpleContainer tempInv = new SimpleContainer(inputStack);
                            Optional<EngineeringRecipe> recipeOpt = minecraft.level.getRecipeManager().getRecipeFor(ModRecipes.ENGINEERING_TYPE.get(), tempInv, minecraft.level);
                            if (recipeOpt.isPresent()) {
                                float chance = recipeOpt.get().getBlueprintChance();
                                String chanceStr = String.format("%.0f", chance * 100);
                                tooltip.add(Component.translatable("gui.cyber_ware_port.blueprint_chance", chanceStr).withStyle(ChatFormatting.GRAY));
                            }
                        }
                    }
                    if (!tooltip.isEmpty()) {
                        guiGraphics.pose().pushPose();
                        guiGraphics.pose().translate(0, 0, 200);
                        guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
                        guiGraphics.pose().popPose();

                    }
                }
            }

            @Override
            public void onClick(double pMouseX, double pMouseY) {
                A_PacketHandler.INSTANCE.sendToServer(new StartWorkbenchCraftingPacket());
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
        float speed = 0.2f;
        if (Math.abs(slideProgress - target) > 0.01f) {
            slideProgress += (target - slideProgress) * speed;

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
            int btnY = this.topPos + 12 + 125;
            int btnX = this.leftPos + panelOriginX + 5;
            this.prevButton.setX(btnX);
            this.prevButton.setY(btnY);
            this.nextButton.setX(btnX + 42);
            this.nextButton.setY(btnY);

        }
        boolean showRightButtons = isPanelVisible && this.menu.hasBlueprintLibrary && this.menu.getBlueprintMaxPages() > 1;
        this.prevBlueprintBtn.visible = showRightButtons;
        this.nextBlueprintBtn.visible = showRightButtons;
        if (showRightButtons) {
            this.prevBlueprintBtn.active = (this.menu.getBlueprintCurrentPage() > 0);
            this.nextBlueprintBtn.active = (this.menu.getBlueprintCurrentPage() < this.menu.getBlueprintMaxPages() - 1);
            int panelWidth = 61;
            int guiWidth = 176;
            int slideOffset = (int) (panelWidth * slideProgress);
            int panelDrawX = this.leftPos + guiWidth - panelWidth + slideOffset;
            int btnY = this.topPos + 12 + 125;
            this.prevBlueprintBtn.setX(panelDrawX + 5);
            this.prevBlueprintBtn.setY(btnY);
            this.nextBlueprintBtn.setX(panelDrawX + 42);
            this.nextBlueprintBtn.setY(btnY);

        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        float currentAnim = slideProgress;
        if (this.menu.hasExtendedInventory) {
            RenderSystem.setShaderTexture(0, COMPONENT_BOX_TEXTURE);
            int moveX = (int) (61 * currentAnim);
            int drawX = x - moveX + 2;
            int drawY = y;
            if (currentAnim > 0.01f) {
                guiGraphics.blit(COMPONENT_BOX_TEXTURE, drawX, drawY, 0, 0, 61, 141, 256, 256);

            }
            if (this.menu.getMaxPages() > 1 && currentAnim > 0.8f) {
                String pageStr = (this.menu.getCurrentPage() + 1) + "/" + this.menu.getMaxPages();
                guiGraphics.drawCenteredString(this.font, pageStr, drawX + 32, drawY + 129, 0xFFFFFF);

            }
        }
        if (this.menu.hasBlueprintLibrary) {
            RenderSystem.setShaderTexture(0, BLUEPRINT_PANEL_TEXTURE);
            int panelWidth = 61;
            int slideOffset = (int) (panelWidth * currentAnim);
            int drawX = x + 176 - panelWidth + slideOffset;
            int drawY = y;
            if (currentAnim > 0.01f) {
                guiGraphics.blit(BLUEPRINT_PANEL_TEXTURE, drawX, drawY, 0, 0, 61, 141, 256, 256);

            }
            if (this.menu.getBlueprintMaxPages() > 1 && currentAnim > 0.8f) {
                String pageStr = (this.menu.getBlueprintCurrentPage() + 1) + "/" + this.menu.getBlueprintMaxPages();
                guiGraphics.drawCenteredString(this.font, pageStr, drawX + 32, drawY + 129, 0xFFFFFF);

            }
        }
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        renderBlueprintGhosts(guiGraphics);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);

    }

    private void renderBlueprintGhosts(GuiGraphics guiGraphics) {
        Slot blueprintSlot = this.menu.getSlot(CyberwareWorkbenchBlockEntity.BLUEPRINT_SLOT);
        ItemStack currentBlueprint = blueprintSlot.getItem();
        if (currentBlueprint.isEmpty() || !(currentBlueprint.getItem() instanceof BlueprintItem)) {
            cachedBlueprint = ItemStack.EMPTY;
            cachedIngredients = null;
            return;

        }
        boolean isChanged = !ItemStack.isSameItemSameTags(cachedBlueprint, currentBlueprint);
        if (isChanged || cachedIngredients == null) {
            cachedBlueprint = currentBlueprint.copy();
            cachedIngredients = null;
            Item targetItem = BlueprintItem.getTargetItem(currentBlueprint);
            if (targetItem != null && this.minecraft != null && this.minecraft.level != null) {
                List<AssemblyRecipe> recipes = this.minecraft.level.getRecipeManager()
                        .getAllRecipesFor(ModRecipes.ASSEMBLY_TYPE.get());
                for (AssemblyRecipe recipe : recipes) {
                    if (recipe.getResultItem(this.minecraft.level.registryAccess()).getItem() == targetItem) {
                        cachedIngredients = recipe.getInputs();
                        break;

                    }
                }
            }
        }
        if (cachedIngredients != null) {
            int startSlotIndex = 3;
            int maxSlots = 6;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            for (int i = 0;
                 i < cachedIngredients.size();
                 i++) {
                if (i >= maxSlots) break;
                AssemblyRecipe.SizedIngredient req = cachedIngredients.get(i);
                if (req.ingredient().getItems().length == 0) continue;
                int slotIndex = startSlotIndex + i;
                if (slotIndex >= this.menu.slots.size()) break;
                Slot targetSlot = this.menu.getSlot(slotIndex);
                ItemStack displayStack = req.ingredient().getItems()[0];
                int requiredCount = req.count();
                int x = this.leftPos + targetSlot.x;
                int y = this.topPos + targetSlot.y;
                ItemStack stackInSlot = targetSlot.getItem();
                boolean isFulfilled = !stackInSlot.isEmpty()
                        && req.ingredient().test(stackInSlot)
                        && stackInSlot.getCount() >= requiredCount;
                if (!isFulfilled) {
                    guiGraphics.renderItem(displayStack, x, y);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    guiGraphics.fill(x, y, x + 16, y + 16, 0x80000000);
                    RenderSystem.disableBlend();
                    String countStr = String.valueOf(requiredCount);
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                    guiGraphics.drawString(this.font, countStr, x + 10, y + 10, 0xFF5555, true);
                    guiGraphics.pose().popPose();

                }
            }
            guiGraphics.pose().popPose();

        }
    }
}