package com.maxwell.cyber_ware_port.client.upgrades.cyberEye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.client.ClientCyberwareSettings;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class CyberwareHudOverlay {
    private static final ResourceLocation BATTERY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/battery_hud.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
            if (!isHudActive(userData)) {
                return;
            }
            int x = ClientCyberwareSettings.hudX;
            int y = ClientCyberwareSettings.hudY;
            renderBatteryHud(event.getGuiGraphics(), mc, userData, x, y);
        });
    }

    private static boolean isHudActive(CyberwareUserData data) {
        ItemStackHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == ModItems.HUDJACK.get() || stack.getItem() == ModItems.CYBER_EYE.get()) {
                if (stack.getItem() instanceof ICyberware cw) {
                    if (cw.isActive(stack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void renderBatteryHud(GuiGraphics g, Minecraft mc, CyberwareUserData data, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, BATTERY_TEXTURE);
        int current = data.getEnergyStored();
        int max = data.getMaxEnergyStored();
        int prod = data.getLastProduction();
        int cons = data.getLastConsumption();
        float r, gVal, b, a;
        int textColor;
        if (current <= 0) {
            long time = System.currentTimeMillis();
            boolean flash = (time % 500) < 250;
            if (flash) {
                r = 1.0f;
                gVal = 0.0f;
                b = 0.0f;
                a = 1.0f;
                textColor = 0xFFFF0000;
            } else {
                r = 0.5f;
                gVal = 0.0f;
                b = 0.0f;
                a = 1.0f;
                textColor = 0xFF880000;
            }
        } else {
            float[] userColor = ClientCyberwareSettings.getColorFloats();
            r = userColor[0];
            gVal = userColor[1];
            b = userColor[2];
            a = userColor[3];
            textColor = ClientCyberwareSettings.hudColor;
        }
        RenderSystem.setShaderColor(r, gVal, b, a);
        int startX = x;
        int startY = y;
        int texTotalWidth = 37;
        int texTotalHeight = 25;
        int frameWidth = 13;
        int frameHeight = 25;
        g.blit(BATTERY_TEXTURE, startX, startY, 0, 0, frameWidth, frameHeight, texTotalWidth, texTotalHeight);
        if (max > 0 && current > 0) {
            int barTextureU = 27;
            int barTextureV = 2;
            int barWidth = 10;
            int barFullHeight = 22;
            int offsetX = 2;
            int offsetY = 2;
            float pct = (float) current / max;
            int renderHeight = (int) (barFullHeight * pct);
            if (renderHeight > 0) {
                int screenY = startY + offsetY + (barFullHeight - renderHeight);
                int textureV = barTextureV + (barFullHeight - renderHeight);
                g.blit(BATTERY_TEXTURE,
                        startX + offsetX, screenY,
                        barTextureU, textureV,
                        barWidth, renderHeight,
                        texTotalWidth, texTotalHeight);
            }
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int textX = startX + frameWidth + 4;
        int textY = startY + 4;
        g.drawString(mc.font, current + " / " + max, textX, textY, textColor, true);
        g.drawString(mc.font, "-" + cons + " / +" + prod, textX, textY + 10, textColor, true);
        RenderSystem.disableBlend();
    }
}