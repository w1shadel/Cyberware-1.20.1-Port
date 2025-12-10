package com.Maxwell.cyber_ware_port.Client.Upgrades.CyberEye;import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareUserData;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class CyberwareHudOverlay {

    private static final ResourceLocation BATTERY_TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/battery_hud.png");@SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;Minecraft mc = Minecraft.getInstance();Player player = mc.player;if (player == null) return;player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
            if (!userData.isCyberwareInstalled(ModItems.HUDJACK.get()) && !userData.isCyberwareInstalled(ModItems.CYBER_EYE.get())) {
                return;}
            if (userData.getMaxEnergyStored() <= 0) return;renderBatteryHud(event.getGuiGraphics(), mc, userData);});}
    private static void renderBatteryHud(GuiGraphics g, Minecraft mc, CyberwareUserData data) {

        RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.setShaderTexture(0, BATTERY_TEXTURE);int startX = 10;int startY = 10;int current = data.getEnergyStored();int max = data.getMaxEnergyStored();int prod = data.getLastProduction();int cons = data.getLastConsumption();int texTotalWidth = 37;int texTotalHeight = 25;int frameWidth = 13;int frameHeight = 25;g.blit(BATTERY_TEXTURE, startX, startY, 0, 0, frameWidth, frameHeight, texTotalWidth, texTotalHeight);if (max > 0 && current > 0) {
            int barTextureU = 27;int barTextureV = 2;int barWidth = 10;int barFullHeight = 22;int offsetX = 2;int offsetY = 2;float pct = (float) current / max;int renderHeight = (int) (barFullHeight * pct);if (renderHeight > 0) {
                int screenY = startY + offsetY + (barFullHeight - renderHeight);int textureV = barTextureV + (barFullHeight - renderHeight);g.blit(BATTERY_TEXTURE,
                        startX + offsetX, screenY,
                        barTextureU, textureV,
                        barWidth, renderHeight,
                        texTotalWidth, texTotalHeight);}
        }

        int textX = startX + frameWidth + 4;int textY = startY + 4;g.drawString(mc.font, current + " / " + max, textX, textY, 0xFFCC0000, true);g.drawString(mc.font, "-" + cons + " / +" + prod, textX, textY + 10, 0xFFFFFFFF, true);RenderSystem.disableBlend();}
}