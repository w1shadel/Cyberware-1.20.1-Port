package com.maxwell.cyber_ware_port.client.debug;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class DebugMouseOverlay {
//    @SubscribeEvent
//    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
//
//        if (!(event.getScreen() instanceof CyberwareMenuScreen)) {
//            return;
//        }
//
//        GuiGraphics g = event.getGuiGraphics();
//        Minecraft mc = Minecraft.getInstance();
//
//        int mouseX = event.getMouseX();
//        int mouseY = event.getMouseY();
//
//        String debugText = String.format("X: %d, Y: %d", mouseX, mouseY);
//
//
//        g.pose().pushPose();
//        g.pose().translate(0, 0, 500);
//
//        g.drawString(mc.font, debugText, mouseX + 10, mouseY - 10, 0xFF00FF00, false);
//
//        g.fill(mouseX - 5, mouseY, mouseX + 6, mouseY + 1, 0x80FFFFFF);
//        g.fill(mouseX, mouseY - 5, mouseX + 1, mouseY + 6, 0x80FFFFFF);
//
//        g.pose().popPose();
//    }
}