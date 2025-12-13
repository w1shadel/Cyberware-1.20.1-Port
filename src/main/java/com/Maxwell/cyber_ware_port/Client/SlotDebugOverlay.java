package com.Maxwell.cyber_ware_port.Client;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class SlotDebugOverlay {
    private static final boolean ENABLE_DEBUG = false;

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (!ENABLE_DEBUG) return;
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            GuiGraphics g = event.getGuiGraphics();
            Minecraft mc = Minecraft.getInstance();
            if (mc.font == null) return;
            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();
            g.pose().pushPose();
            g.pose().translate(0, 0, 500);
            for (Slot slot : screen.getMenu().slots) {
                int x = guiLeft + slot.x;
                int y = guiTop + slot.y;
                int color = (slot.x < 0 || slot.y < 0 || slot.x > 176) ? 0x80FF0000 : 0x8000FF00;
                RenderSystem.enableBlend();
                g.fill(x, y, x + 16, y + 16, color);
                RenderSystem.disableBlend();
                if (isHovering(x, y, mouseX, mouseY)) {
                    g.renderOutline(x - 1, y - 1, 18, 18, 0xFFFFFFFF);
                    String info1 = "Index: " + slot.index;
                    String info2 = String.format("Rel: %d, %d", slot.x, slot.y);
                    int textY = mouseY - 20;
                    g.drawString(mc.font, info1, mouseX + 10, textY, 0xFFFFFFFF, true);
                    g.drawString(mc.font, info2, mouseX + 10, textY + 10, 0xFFFFFF00, true);

                }
            }
            g.pose().popPose();

        }
    }

    private static boolean isHovering(int x, int y, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;

    }
}