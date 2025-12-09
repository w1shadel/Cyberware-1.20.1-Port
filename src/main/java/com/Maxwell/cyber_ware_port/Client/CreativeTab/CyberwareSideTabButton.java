package com.Maxwell.cyber_ware_port.Client.CreativeTab;



import com.mojang.blaze3d.systems.RenderSystem;


import net.minecraft.client.gui.GuiGraphics;


import net.minecraft.client.gui.components.Button;


import net.minecraft.network.chat.Component;



public class CyberwareSideTabButton extends Button {

    public CyberwareSideTabButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);


    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {

        if (this.isHovered()) {
            RenderSystem.enableBlend();


            RenderSystem.defaultBlendFunc();



            g.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x1EFFFFFF);


            RenderSystem.disableBlend();


        }

    }
}