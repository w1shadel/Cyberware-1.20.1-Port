package com.Maxwell.cyber_ware_port.Client.Screen;import com.Maxwell.cyber_ware_port.Common.Container.ComponentBoxMenu;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;@SuppressWarnings("remeoval")
public class ComponentBoxScreen extends AbstractContainerScreen<ComponentBoxMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/component_box_normal.png");public ComponentBoxScreen(ComponentBoxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);this.imageHeight = 150;this.inventoryLabelY = this.imageHeight - 94;}

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);int x = (width - imageWidth) / 2;int y = (height - imageHeight) / 2;g.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);}

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);super.render(g, mouseX, mouseY, partialTick);renderTooltip(g, mouseX, mouseY);}
}