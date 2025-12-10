package com.Maxwell.cyber_ware_port.Client.Upgrades.HandManu;import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;public class PortableCraftingButton extends Button {

    private final ItemStack craftingTableStack;public PortableCraftingButton(int x, int y, OnPress onPress) {

        super(x, y, 20, 20, Component.empty(), onPress, DEFAULT_NARRATION);this.craftingTableStack = new ItemStack(Items.CRAFTING_TABLE);}

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {

        super.renderWidget(g, mouseX, mouseY, partialTick);

int itemX = this.getX() + 2;int itemY = this.getY() + 2;g.renderItem(craftingTableStack, itemX, itemY);}
}