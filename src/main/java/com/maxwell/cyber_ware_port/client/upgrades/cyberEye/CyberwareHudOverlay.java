package com.maxwell.cyber_ware_port.client.upgrades.cybereye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.client.ClientCyberwareSettings;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class CyberwareHudOverlay {
    private static final Identifier BATTERY_TEXTURE =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/battery_hud.png");

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        if (VanillaGuiLayers.HOTBAR.equals(event.getName())) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;
            CyberwareUserData userData = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            if (isHudActive(userData)) {
                int x = ClientCyberwareSettings.hudX;
                int y = ClientCyberwareSettings.hudY;
                renderBatteryHud(event.getGuiGraphics(), mc, userData, x, y);
            }
        }
    }

    private static boolean isHudActive(CyberwareUserData data) {
        ItemStacksResourceHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stack = handler.getResource(i).toStack(handler.getAmountAsInt(i));
            if (stack.isEmpty()) continue;
            if (stack.is(ModItems.HUDJACK.get())) {
                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (cw != null && cw.isActive(stack)) return true;
            }
        }
        return false;
    }

    public static void renderBatteryHud(GuiGraphicsExtractor g, Minecraft mc, CyberwareUserData data, int x, int y) {
        int current = data.getEnergyStored();
        int max = data.getMaxEnergyStored();
        int prod = data.getLastProduction();
        int cons = data.getLastConsumption();
        int hudColor;
        int textColor;
        if (current <= 0 && max > 0) {
            boolean flash = (System.currentTimeMillis() % 500) < 250;
            hudColor = flash ? 0xFFFF0000 : 0xFF880000;
            textColor = hudColor;
        } else {
            float[] userColor = ClientCyberwareSettings.getColorFloats();
            hudColor = ARGB.color(
                    (int) (userColor[3] * 255),
                    (int) (userColor[0] * 255),
                    (int) (userColor[1] * 255),
                    (int) (userColor[2] * 255)
            );
            textColor = ClientCyberwareSettings.hudColor;
        }
        int texTotalWidth = 37;
        int texTotalHeight = 25;
        int frameWidth = 13;
        int frameHeight = 25;
        g.blit(
                RenderPipelines.GUI_TEXTURED,
                BATTERY_TEXTURE,
                x, y,
                0.0F, 0.0F,
                frameWidth, frameHeight,
                texTotalWidth, texTotalHeight,
                hudColor
        );
        if (max > 0 && current > 0) {
            int barTextureU = 27;
            int barTextureV = 2;
            int barWidth = 10;
            int barFullHeight = 22;
            float pct = (float) current / max;
            int renderHeight = (int) (barFullHeight * pct);
            if (renderHeight > 0) {
                int screenY = y + 2 + (barFullHeight - renderHeight);
                float textureV = (float) barTextureV + (barFullHeight - renderHeight);
                g.blit(
                        RenderPipelines.GUI_TEXTURED,
                        BATTERY_TEXTURE,
                        x + 2, screenY,
                        (float) barTextureU, textureV,
                        barWidth, renderHeight,
                        texTotalWidth, texTotalHeight,
                        hudColor
                );
            }
        }
        int textX = x + frameWidth + 4;
        g.text(mc.font, current + " / " + max, textX, y + 4, textColor, true);
        String stats = "-" + cons + " / +" + prod;
        g.text(mc.font, stats, textX, y + 14, textColor, true);
    }
}