package com.Maxwell.cyber_ware_port.Client.CreativeTab;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CyberwareSideTabButton extends Button {

    private final ItemStack iconStack;

    public CyberwareSideTabButton(int x, int y, int width, int height, ItemStack iconStack, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.iconStack = iconStack;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // 背景は親のGUIで描画済みなので、ここでは描画しない

        // ホバー時のハイライト（半透明の白）
        if (this.isHovered()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            // x, y, x2, y2, color (ARGB)
            g.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x80FFFFFF);
            RenderSystem.disableBlend();
        }

        // アイコンの描画（ボタンの中央）
        if (!iconStack.isEmpty()) {
            int itemX = this.getX() + (this.width - 16) / 2;
            int itemY = this.getY() + (this.height - 16) / 2;
            g.renderItem(iconStack, itemX, itemY);
            g.renderItemDecorations(Minecraft.getInstance().font, iconStack, itemX, itemY);
        }
    }
}