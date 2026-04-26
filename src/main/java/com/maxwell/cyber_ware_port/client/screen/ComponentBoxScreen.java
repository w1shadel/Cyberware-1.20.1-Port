package com.maxwell.cyber_ware_port.client.screen;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.container.ComponentBoxMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ComponentBoxScreen extends AbstractContainerScreen<ComponentBoxMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/component_box_normal.png");

    public ComponentBoxScreen(ComponentBoxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 150;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.leftPos,
                this.topPos,
                0.0F, 0.0F,
                this.imageWidth,
                this.imageHeight,
                256, 256
        );
        super.extractContents(graphics, mouseX, mouseY, a);
    }
}