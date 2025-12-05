package com.Maxwell.cyber_ware_port.Client.Screen.Scanner;

import com.Maxwell.cyber_ware_port.Common.Container.ScannerMenu;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.systems.RenderSystem;
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

    private final List<Component> logLines = new ArrayList<>();

    private int tickCounter = 0;

    private final RandomSource random = RandomSource.create();

    public ScannerScreen(ScannerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    /**
     * 画面が開いている間、毎tick呼ばれるメソッド
     * ここでログの更新処理を行います
     */
    @Override
    protected void containerTick() {
        super.containerTick();

        if (this.menu.isCrafting()) {
            tickCounter++;

            if (tickCounter % 2 == 0) {
                addRandomLog();
            }
        } else {

            if (!logLines.isEmpty()) {
                logLines.clear();
            }
        }
    }

    /**
     * ランダムな文字列を生成してリストに追加する
     */
    private void addRandomLog() {

        String[] prefixes = {"0x", "MEM:", "DAT:", "SEG:", "RW:"};
        String prefix = prefixes[random.nextInt(prefixes.length)];

        String hex = Integer.toHexString(random.nextInt());

        String val = String.valueOf(random.nextInt(99));

        String logText = prefix + hex.toUpperCase() + " [" + val + "]";

        logLines.add(Component.literal(logText));

        if (logLines.size() > 1) {
            logLines.remove(0);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        int color = 0x55FFFF;

        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, color, false);

        boolean hasPaper = this.menu.getSlot(0).hasItem();
        boolean hasInput = this.menu.getSlot(1).hasItem();
        String percentageObj = (hasPaper && hasInput) ? "50.0" : "0.0";
        Component chanceText = Component.translatable("gui.cyber_ware_port.scanner.chance", percentageObj);
        int textWidth = this.font.width(chanceText);
        int xPos = this.imageWidth - textWidth - 8;
        guiGraphics.drawString(this.font, chanceText, xPos, this.titleLabelY, color, false);

        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, color, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if (!logLines.isEmpty()) {

            Component line = logLines.get(logLines.size() - 1);
            int logStartX = x + 8;
            int logStartY = y + 20; 
            int logColor = 0x55FFFF;

            guiGraphics.drawString(this.font, line, logStartX, logStartY, logColor, false);
        }guiGraphics.blit(TEXTURE, x + 4, y + 30, 0, 166, 161, 8);

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