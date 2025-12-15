package com.Maxwell.cyber_ware_port.Client.Screen.RoboSurgeon;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class InstalledCyberwareScreen extends Screen {

    private static final int ITEM_HEIGHT = 20;
    private static final int SCROLL_BAR_WIDTH = 6;
    private final Screen previousScreen;
    private final List<ItemStack> installedCyberware = new ArrayList<>();
    private double scrollOffset = 0.0;

    public InstalledCyberwareScreen(Screen previousScreen) {
        super(Component.translatable("gui.cyber_ware_port.installed_cyberware.title"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.translatable("gui.back"), (button) -> {
            this.onClose();
        }).bounds(this.width / 2 - 100, this.height - 28, 200, 20).build());
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(cyberware -> {
                ItemStackHandler installed = cyberware.getInstalledCyberware();
                for (int i = 0;
                     i < installed.getSlots();
                     i++) {
                    ItemStack stack = installed.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        this.installedCyberware.add(stack);
                    }
                }
            });
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int listTop = 32;
        int listBottom = this.height - 36;
        int listLeft = this.width / 2 - 120;
        int listRight = this.width / 2 + 120;
        guiGraphics.fill(listLeft, listTop, listRight, listBottom, 0x80000000);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        guiGraphics.enableScissor(listLeft, listTop, listRight, listBottom);
        int currentY = listTop + 4 - (int) this.scrollOffset;
        for (ItemStack stack : this.installedCyberware) {
            guiGraphics.renderItem(stack, listLeft + 5, currentY);
            guiGraphics.drawString(this.font, stack.getDisplayName(), listLeft + 28, currentY + 5, 0xFFFFFF);
            if (mouseY >= listTop && mouseY < listBottom && isMouseOver(mouseX, mouseY, listLeft + 5, currentY, 16, 16)) {
                guiGraphics.renderTooltip(this.font, stack, mouseX, mouseY);
            }
            currentY += ITEM_HEIGHT;
        }
        guiGraphics.disableScissor();
        int listHeight = listBottom - listTop;
        int contentHeight = this.installedCyberware.size() * ITEM_HEIGHT;
        if (contentHeight > listHeight) {
            int scrollBarHeight = (int) ((float) listHeight / contentHeight * listHeight);
            int scrollBarY = listTop + (int) (((float) this.scrollOffset / (contentHeight - listHeight)) * (listHeight - scrollBarHeight));
            guiGraphics.fill(listRight - SCROLL_BAR_WIDTH - 1, scrollBarY, listRight - 1, scrollBarY + scrollBarHeight, 0xFFFFFFFF);
        }

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int listHeight = this.height - 36 - 32;
        int contentHeight = this.installedCyberware.size() * ITEM_HEIGHT;
        int maxScroll = Math.max(0, contentHeight - listHeight);
        this.scrollOffset -= delta * 10.0;
        this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, maxScroll));
        return true;
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.previousScreen);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}