package com.Maxwell.cyber_ware_port.Client.Upgrades.CyberEye;import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import com.Maxwell.cyber_ware_port.Common.Network.A_PacketHandler;
import com.Maxwell.cyber_ware_port.Common.Network.ToggleCyberwarePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;public class CyberwareMenuScreen extends Screen {

    private record ToggleablePart(int slotId, ItemStack stack, ICyberware item) {}
    private final List<ToggleablePart> parts = new ArrayList<>();private static final float INNER_RADIUS = 40.0f;private static final float OUTER_RADIUS = 100.0f;private static final float ITEM_RADIUS = (INNER_RADIUS + OUTER_RADIUS) / 2.0f;public CyberwareMenuScreen() {
        super(Component.translatable("gui.cyber_ware_port.menu"));}

    @Override
    protected void init() {
        super.init();parts.clear();if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                ItemStackHandler handler = data.getInstalledCyberware();for (int i = 0;

 i < handler.getSlots();

 i++) {
                    ItemStack stack = handler.getStackInSlot(i);if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cw) {
                        if (cw.canToggle(stack)) {
                            parts.add(new ToggleablePart(i, stack, cw));}
                    }
                }
            });}
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {int centerX = width / 2;int centerY = height / 2;RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.setShader(GameRenderer::getPositionColorShader);Tesselator tesselator = Tesselator.getInstance();BufferBuilder buffer = tesselator.getBuilder();Matrix4f matrix = g.pose().last().pose();float red = 1.0f;float green = 0.0f;float blue = 0.0f;float alpha = 0.4f;buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);int segments = 60;for (int i = 0;

 i <= segments;

 i++) {
            double angle = 2 * Math.PI * i / segments;float cos = (float) Math.cos(angle);float sin = (float) Math.sin(angle);buffer.vertex(matrix, centerX + cos * OUTER_RADIUS, centerY + sin * OUTER_RADIUS, 0)
                    .color(red, green, blue, alpha).endVertex();buffer.vertex(matrix, centerX + cos * INNER_RADIUS, centerY + sin * INNER_RADIUS, 0)
                    .color(red, green, blue, alpha).endVertex();}
        tesselator.end();RenderSystem.disableBlend();if (parts.isEmpty()) {
            g.drawCenteredString(this.font, "No Active Cyberware", centerX, centerY, 0xFFFFFFFF);return;}

        double angleStep = 2 * Math.PI / parts.size();double dx = mouseX - centerX;double dy = mouseY - centerY;double dist = Math.sqrt(dx * dx + dy * dy);double mouseAngle = Math.atan2(dy, dx);
for (int i = 0;
 i < parts.size();
 i++) {
            ToggleablePart part = parts.get(i);double itemAngle = i * angleStep - Math.PI / 2;int x = centerX + (int) (ITEM_RADIUS * Math.cos(itemAngle));

            int y = centerY + (int) (ITEM_RADIUS * Math.sin(itemAngle));boolean isActive = part.item.isActive(part.stack);boolean isHovered = (mouseX >= x - 12 && mouseX <= x + 12 && mouseY >= y - 12 && mouseY <= y + 12);g.pose().pushPose();
String statusText = isActive ? "有効化" : "無効化";
int textColor = 0xFFFFFFFF;

            if (isHovered) textColor = 0xFFFFFF00;g.drawCenteredString(this.font, statusText, x, y - 20, textColor);g.renderItem(part.stack, x - 8, y - 8);if (isActive) {

                g.renderOutline(x - 10, y - 10, 20, 20, 0xFF00FF00);

            } else {

                g.renderOutline(x - 10, y - 10, 20, 20, 0xFFFF0000);

            }

            if (isHovered) {
                g.renderTooltip(this.font, part.stack, mouseX, mouseY);

            }

            g.pose().popPose();

        }
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { 
            int centerX = width / 2;

            int centerY = height / 2;

            double angleStep = 2 * Math.PI / parts.size();for (int i = 0;
 i < parts.size();
 i++) {
                ToggleablePart part = parts.get(i);double itemAngle = i * angleStep - Math.PI / 2;

                int x = centerX + (int) (ITEM_RADIUS * Math.cos(itemAngle));

                int y = centerY + (int) (ITEM_RADIUS * Math.sin(itemAngle));if (mouseX >= x - 12 && mouseX <= x + 12 && mouseY >= y - 12 && mouseY <= y + 12) {
                    A_PacketHandler.INSTANCE.sendToServer(new ToggleCyberwarePacket(part.slotId));Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F));
part.item.toggle(part.stack);
return true;

                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);

    }

    @Override
    public boolean isPauseScreen() {
        return false;

    }
}