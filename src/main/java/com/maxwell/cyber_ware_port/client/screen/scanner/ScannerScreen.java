package com.maxwell.cyber_ware_port.client.screen.scanner;

import com.maxwell.cyber_ware_port.common.container.ScannerMenu;
import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("removal")
public class ScannerScreen extends AbstractContainerScreen<ScannerMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/scanner_gui.png");
    private static final int SAYING_COUNT = 74;
    private final List<Component> logLines = new ArrayList<>();
    private final RandomSource random = RandomSource.create();
    private int tickCounter = 0;

    public ScannerScreen(ScannerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.menu.isCrafting()) {
            tickCounter++;
            if (tickCounter % 5 == 0) {
                addRandomLog();
            }
        } else {
            if (!logLines.isEmpty()) {
                logLines.clear();
            }
        }
    }

    private void addRandomLog() {
        int index = random.nextInt(SAYING_COUNT);
        String key = "cyberware.gui.scanner_saying." + index;
        logLines.add(Component.translatable(key));
        if (logLines.size() > 1) {
            logLines.remove(0);
        }
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        int color = 0x55FFFF;

        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, color, false);


        Component warnText = Component.literal("Destroys Cyberware").withStyle(ChatFormatting.RED);
        int warnWidth = this.font.width(warnText);

        guiGraphics.drawString(this.font, warnText, this.imageWidth - warnWidth - 9, this.titleLabelY, 0xFFFFFF, false);


        Component chanceText = Component.literal("50% Chance").withStyle(ChatFormatting.YELLOW);
        int chanceWidth = this.font.width(chanceText);

        guiGraphics.drawString(this.font, chanceText, this.imageWidth - chanceWidth - 8, this.titleLabelY + 10, 0xFFFFFF, false);


        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, color, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        if (!logLines.isEmpty()) {
            Component line = logLines.get(logLines.size() - 1);
            int logStartX = x + 8;
            int logStartY = y + 20;
            int logColor = 0x55FFFF;
            guiGraphics.drawString(this.font, line, logStartX, logStartY, logColor, false);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, x + 4, y + 30, 0, 166, 161, 8);
        int maxBarWidth = 161;
        int progressWidth = menu.getScaledProgress(maxBarWidth);
        if (progressWidth > 0) {
            guiGraphics.blit(TEXTURE, x + 4, y + 30, 0, 175, progressWidth, 8);
        }
        RenderSystem.disableBlend();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}