package com.Maxwell.cyber_ware_port.Client.Screen.CWB;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberwareWorkbenchBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipie.AssemblyRecipe;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipie.EngineeringRecipe;
import com.Maxwell.cyber_ware_port.Common.Container.CyberwareWorkbenchMenu;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.Common.Network.PacketHandler;
import com.Maxwell.cyber_ware_port.Common.Network.StartWorkbenchCraftingPacket;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModRecipes;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
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

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/engineering.png");
    private ItemStack cachedBlueprint = ItemStack.EMPTY;
    private List<AssemblyRecipe.SizedIngredient> cachedIngredients = null;
    public CyberwareWorkbenchScreen(CyberwareWorkbenchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    @Override
    protected void init() {
        super.init();
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.addRenderableWidget(new AbstractWidget(x + 40, y + 35, 18, 18, Component.empty()) {
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

                        tooltip.add(Component.translatable("gui.cyber_ware_port.assemble")
                                .withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN));
                    }

                    else if (hasInput) {

                        tooltip.add(Component.translatable("gui.cyber_ware_port.deconstruct")
                                .withStyle(ChatFormatting.BOLD, ChatFormatting.RED));

                        if (minecraft != null && minecraft.level != null) {
                            SimpleContainer tempInv = new SimpleContainer(inputStack);

                            Optional<EngineeringRecipe> recipeOpt = minecraft.level.getRecipeManager()
                                    .getRecipeFor(ModRecipes.ENGINEERING_TYPE.get(), tempInv, minecraft.level);

                            if (recipeOpt.isPresent()) {
                                float chance = recipeOpt.get().getBlueprintChance();
                                String chanceStr = String.format("%.0f", chance * 100); 

                                tooltip.add(Component.translatable("gui.cyber_ware_port.blueprint_chance", chanceStr)
                                        .withStyle(ChatFormatting.GRAY));
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
                PacketHandler.INSTANCE.sendToServer(new StartWorkbenchCraftingPacket());

            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
                this.defaultButtonNarrationText(pNarrationElementOutput);
            }
        });
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
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
        }boolean isChanged = !ItemStack.isSameItemSameTags(cachedBlueprint, currentBlueprint);

        if (isChanged) {
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
            int startSlotIndex = CyberwareWorkbenchBlockEntity.OUTPUT_SLOT_START;
            int maxSlots = 6;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

            for (int i = 0; i < cachedIngredients.size(); i++) {
                if (i >= maxSlots) break;

                AssemblyRecipe.SizedIngredient req = cachedIngredients.get(i);
                if (req.ingredient.getItems().length == 0) continue;

                int slotIndex = startSlotIndex + i;
                Slot targetSlot = this.menu.getSlot(slotIndex);
                ItemStack displayStack = req.ingredient.getItems()[0];
                int requiredCount = req.count;

                int x = this.leftPos + targetSlot.x;
                int y = this.topPos + targetSlot.y;int totalFound = 0;
                for (int checkSlotIdx = startSlotIndex; checkSlotIdx < CyberwareWorkbenchBlockEntity.SPECIAL_OUTPUT_SLOT; checkSlotIdx++) {
                    ItemStack stackInSlot = this.menu.getSlot(checkSlotIdx).getItem();
                    if (req.ingredient.test(stackInSlot)) {
                        totalFound += stackInSlot.getCount();
                    }
                }

                if (totalFound >= requiredCount) continue;

                boolean showGhost = true;
                if (targetSlot.hasItem() && req.ingredient.test(targetSlot.getItem())) {
                    showGhost = false;
                }

                if (showGhost) {
                    guiGraphics.renderItem(displayStack, x, y);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    guiGraphics.fill(x, y, x + 16, y + 16, 0x80000000);
                    RenderSystem.disableBlend();
                }

                String countStr = String.valueOf(req.count);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
                guiGraphics.drawString(this.font, countStr, x + 1, y + 16 - 8, 0xFF5555, true);
                guiGraphics.pose().popPose();
            }
            guiGraphics.pose().popPose();
        }
    }
}