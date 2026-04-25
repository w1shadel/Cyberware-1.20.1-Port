package com.maxwell.cyber_ware_port.client.upgrades.handmenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PortableCraftingButton extends Button {
    private final ItemStack craftingTableStack;

    public PortableCraftingButton(int x, int y, OnPress onPress) {
        super(x, y, 20, 20, Component.empty(), onPress, DEFAULT_NARRATION);
        this.craftingTableStack = new ItemStack(Items.CRAFTING_TABLE);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.extractDefaultSprite(graphics);
        int itemX = this.getX() + 2;
        int itemY = this.getY() + 2;
        graphics.item(this.craftingTableStack, itemX, itemY);
    }
}