package com.Maxwell.cyber_ware_port.Client.Screen.RoboSurgeon;


import com.Maxwell.cyber_ware_port.Common.Network.ClientPacketHandler;


import com.Maxwell.cyber_ware_port.CyberWare;


import com.mojang.blaze3d.systems.RenderSystem;


import net.minecraft.client.Minecraft;


import net.minecraft.client.gui.GuiGraphics;


import net.minecraftforge.api.distmarker.Dist;


import net.minecraftforge.client.event.RenderGuiOverlayEvent;


import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;


import net.minecraftforge.eventbus.api.SubscribeEvent;


import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class SurgeryOverlay {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {

        if (event.getOverlay() != VanillaGuiOverlay.HELMET.type()) return;



        int progress = ClientPacketHandler.currentProgress;


        int max = ClientPacketHandler.maxProgress;



        if (progress <= 0) return;



        float alpha = 0.0f;



        if (progress < 30) {
            alpha = progress / 30.0f;


        }

        else if (progress > max - 20) {
            float remaining = max - progress;


            alpha = remaining / 20.0f;


        }

        else {
            alpha = 1.0f;


        }

        alpha = Math.max(0.0f, Math.min(1.0f, alpha));



        if (alpha > 0) {
            renderBlackout(event.getGuiGraphics(), alpha);


        }
    }

    private static void renderBlackout(GuiGraphics g, float alpha) {
        Minecraft mc = Minecraft.getInstance();


        int width = mc.getWindow().getGuiScaledWidth();


        int height = mc.getWindow().getGuiScaledHeight();



        RenderSystem.enableBlend();


        RenderSystem.defaultBlendFunc();



        g.pose().pushPose();


        g.pose().translate(0, 0, 2000);

int color = ((int) (alpha * 255) << 24);



        g.fill(0, 0, width, height, color);



        g.pose().popPose();


        RenderSystem.disableBlend();


    }
}