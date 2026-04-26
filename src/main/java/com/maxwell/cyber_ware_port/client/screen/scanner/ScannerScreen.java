package com.maxwell.cyber_ware_port.client.screen.scanner;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.container.ScannerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("removal")
public class ScannerScreen extends AbstractContainerScreen<ScannerMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/scanner_gui.png");
    private static final int SAYING_COUNT = 74;
    private final List<Component> logLines = new ArrayList<>();
    private final RandomSource random = RandomSource.create();
    private int tickCounter = 0;

    public ScannerScreen(ScannerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, 176, 166);
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
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int aquaColor = 0xFF55FFFF;
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, aquaColor, false);
        Component warnText = Component.literal("Destroys Cyberware").withStyle(ChatFormatting.RED);
        int warnWidth = this.font.width(warnText);
        graphics.text(this.font, warnText, this.imageWidth - warnWidth - 9, this.titleLabelY, 0xFFFFFFFF, false);
        Component chanceText = Component.literal("50% Chance").withStyle(ChatFormatting.YELLOW);
        int chanceWidth = this.font.width(chanceText);
        graphics.text(this.font, chanceText, this.imageWidth - chanceWidth - 8, this.titleLabelY + 10, 0xFFFFFFFF, false);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, aquaColor, false);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        if (!logLines.isEmpty()) {
            Component line = logLines.get(logLines.size() - 1);
            int logStartX = 8;
            int logStartY = 20;
            int logColor = 0xFF55FFFF;
            graphics.text(this.font, line, logStartX, logStartY, logColor, false);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 4, 30, 0, 166, 161, 8, 256, 256);
        int maxBarWidth = 161;
        int progressWidth = menu.getScaledProgress(maxBarWidth);
        if (progressWidth > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 4, 30, 0, 175, progressWidth, 8, 256, 256);
        }
        super.extractContents(graphics, mouseX, mouseY, a);
    }
}