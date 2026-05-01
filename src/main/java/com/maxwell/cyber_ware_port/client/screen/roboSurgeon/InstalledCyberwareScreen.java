package com.maxwell.cyber_ware_port.client.screen.robosurgeon;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

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
            this.installedCyberware.clear();
            CyberwareUserData cyberware = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            ItemStacksResourceHandler installed = cyberware.getInstalledCyberware();
            for (int i = 0; i < installed.size(); i++) {
                ItemStack stack = installed.getResource(i).toStack(installed.getAmountAsInt(i));
                if (!stack.isEmpty()) {
                    this.installedCyberware.add(stack.copy());
                }
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int listTop = 32;
        int listBottom = this.height - 36;
        int listLeft = this.width / 2 - 120;
        int listRight = this.width / 2 + 120;
        graphics.fill(listLeft, listTop, listRight, listBottom, 0x80000000);
        int titleWidth = this.font.width(this.title);
        graphics.text(this.font, this.title, (this.width - titleWidth) / 2, 15, 0xFFFFFFFF, false);
        int currentY = listTop + 4 - (int) this.scrollOffset;
        for (ItemStack stack : this.installedCyberware) {
            if (currentY + ITEM_HEIGHT > listTop && currentY < listBottom) {
                graphics.item(stack, listLeft + 5, currentY);
                graphics.text(this.font, stack.getHoverName(), listLeft + 28, currentY + 5, 0xFFFFFFFF, false);
                if (mouseX >= listLeft && mouseX <= listRight && mouseY >= currentY && mouseY <= currentY + ITEM_HEIGHT) {
                    graphics.setTooltipForNextFrame(
                            this.font,
                            getTooltipFromItem(this.minecraft, stack),
                            stack.getTooltipImage(),
                            stack,
                            mouseX, mouseY,
                            null
                    );
                }
            }
            currentY += ITEM_HEIGHT;
        }
        int listHeight = listBottom - listTop;
        int contentHeight = this.installedCyberware.size() * ITEM_HEIGHT;
        if (contentHeight > listHeight) {
            int scrollBarHeight = (int) ((float) listHeight / contentHeight * listHeight);
            int scrollBarY = listTop + (int) (((float) this.scrollOffset / (contentHeight - listHeight)) * (listHeight - scrollBarHeight));
            graphics.fill(listRight - SCROLL_BAR_WIDTH - 1, scrollBarY, listRight - 1, scrollBarY + scrollBarHeight, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int listHeight = this.height - 36 - 32;
        int contentHeight = this.installedCyberware.size() * ITEM_HEIGHT;
        int maxScroll = Math.max(0, contentHeight - listHeight);
        this.scrollOffset -= scrollY * 10.0;
        this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, (double) maxScroll));
        return true;
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.previousScreen);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}