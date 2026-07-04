package com.maxwell.cyber_ware_port.client.upgrades.cybereye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.client.ClientCyberwareSettings;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class CyberwareHudOverlay {
    private static final ResourceLocation BATTERY_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/battery_hud.png");

    private static final Map<Integer, Float> itemPrevProgress = new HashMap<>();
    private static final Map<Integer, Float> itemProgress = new HashMap<>();
    private static final Map<Integer, Integer> itemYIndices = new HashMap<>();
    private static float prevSlideProgress = 0.0f;
    private static float slideProgress = 0.0f;
    private static boolean lastHudActive = false;
    private static int bootTicks = 0;
    private static int shutdownTicks = 0;

    public static Set<Integer> getInactiveSlotIds(CyberwareUserData userData) {
        Set<Integer> inactive = new HashSet<>();
        IItemHandler handler = userData.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null && !stack.is(ModItems.HUDJACK.get())) {
                boolean isToggledOn = cw.isActive(stack);
                boolean isEmpOffline = isToggledOn && userData.getEmpTicks() > 0;
                boolean isNoPower = isToggledOn && cw.getEnergyConsumption(stack) > 0 && userData.getEnergyStored() < cw.getEnergyConsumption(stack) && userData.getEmpTicks() <= 0;
                boolean isToggledOff = cw.canToggle(stack) && !isToggledOn;
                if (isEmpOffline || isNoPower || isToggledOff) {
                    inactive.add(i);
                }
            }
        }
        return inactive;
    }

    public static boolean hasActiveErrorsOrOff(Player player, CyberwareUserData userData) {
        return !getInactiveSlotIds(userData).isEmpty();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CyberwareUserData userData = mc.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        boolean currentHudActive = isHudActive(userData);

        if (currentHudActive && !lastHudActive) {
            bootTicks = 20;
            shutdownTicks = 0;
            if (mc.level != null) {
                mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                        net.minecraft.sounds.SoundEvents.CONDUIT_ACTIVATE, SoundSource.PLAYERS, 0.7F, 1.4F, false);
            }
        } else if (!currentHudActive && lastHudActive) {
            shutdownTicks = 15;
            bootTicks = 0;
            if (mc.level != null) {
                mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                        net.minecraft.sounds.SoundEvents.CONDUIT_DEACTIVATE, SoundSource.PLAYERS, 0.7F, 0.8F, false);
            }
        }
        lastHudActive = currentHudActive;

        if (bootTicks > 0) bootTicks--;
        if (shutdownTicks > 0) shutdownTicks--;

        boolean isMoveMode = mc.screen instanceof CyberwareMenuScreen menu && menu.isHudMoveMode;
        Set<Integer> currentInactive = getInactiveSlotIds(userData);

        for (int slotId : currentInactive) {
            if (!itemYIndices.containsKey(slotId)) {
                boolean[] occupied = new boolean[10];
                for (int index : itemYIndices.values()) {
                    if (index >= 0 && index < 10) {
                        occupied[index] = true;
                    }
                }
                int freeIndex = 0;
                for (int j = 0; j < 10; j++) {
                    if (!occupied[j]) {
                        freeIndex = j;
                        break;
                    }
                }
                itemYIndices.put(slotId, freeIndex);
            }
        }
        Set<Integer> trackedSlots = new HashSet<>(itemProgress.keySet());
        trackedSlots.addAll(currentInactive);
        for (int slotId : trackedSlots) {
            float prev = itemProgress.getOrDefault(slotId, 0.0f);
            itemPrevProgress.put(slotId, prev);
            float target = currentInactive.contains(slotId) ? 1.0f : 0.0f;
            float tickSpeed = 0.05F;
            float next;
            if (prev < target) {
                next = Math.min(prev + tickSpeed, target);
            } else {
                next = Math.max(prev - tickSpeed, target);
            }
            itemProgress.put(slotId, next);
        }
        itemProgress.entrySet().removeIf(entry -> entry.getValue() == 0.0f && !currentInactive.contains(entry.getKey()));
        itemPrevProgress.keySet().removeIf(key -> !itemProgress.containsKey(key));
        itemYIndices.keySet().removeIf(key -> !itemProgress.containsKey(key));
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        if (VanillaGuiLayers.HOTBAR.equals(event.getName())) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;
            CyberwareUserData userData = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());

            boolean isRenderable = isHudActive(userData) || shutdownTicks > 0;
            if (!isRenderable) {
                return;
            }

            boolean isMoveMode = mc.screen instanceof CyberwareMenuScreen menu && menu.isHudMoveMode;
            RandomSource rand = mc.level != null ? mc.level.random : RandomSource.create();

            int jitterX = 0;
            int jitterY = 0;
            if (userData.getEmpTicks() > 0 || bootTicks > 0 || (shutdownTicks > 0 && rand.nextFloat() < 0.3f)) {
                jitterX = rand.nextInt(5) - 2;
                jitterY = rand.nextInt(5) - 2;
            }

            float hudAlpha = 1.0f;
            if (bootTicks > 0) {
                if (rand.nextFloat() < 0.15f) {
                    hudAlpha = 0.1f;
                } else {
                    hudAlpha = rand.nextFloat() * 0.4f + 0.5f;
                }
            } else if (shutdownTicks > 0) {
                float progress = (float) shutdownTicks / 15.0f;
                if (rand.nextFloat() < 0.25f) {
                    hudAlpha = 0.0f;
                } else {
                    hudAlpha = progress;
                }
            }

            int x = ClientCyberwareSettings.hudX;
            int y = ClientCyberwareSettings.hudY;
            renderBatteryHud(event.getGuiGraphics(), mc, userData, x, y, jitterX, jitterY, hudAlpha);

            int currentY = y + 30;
            if (userData.getImmunityTime() > 0) {
                renderImmunityHud(event.getGuiGraphics(), mc, userData, x, currentY, hudAlpha);
            }

            boolean hasActiveAnim = !itemProgress.isEmpty();
            if (hasActiveAnim || isMoveMode) {
                int barX = ClientCyberwareSettings.barX;
                int barY = ClientCyberwareSettings.barY;
                float partialTick = event.getPartialTick().getGameTimeDeltaTicks();

                for (Map.Entry<Integer, Float> entry : itemProgress.entrySet()) {
                    int slotId = entry.getKey();
                    ItemStack stack = userData.getInstalledCyberware().getStackInSlot(slotId);
                    if (stack.isEmpty()) continue;
                    ICyberware cw = CyberwareAPI.getCyberware(stack);
                    if (cw == null) continue;
                    Integer yIndex = itemYIndices.get(slotId);
                    if (yIndex == null) continue;
                    int slotY = barY + 5 + (yIndex * 20) + jitterY;
                    float prevProgress = itemPrevProgress.getOrDefault(slotId, 0.0f);
                    float currentProgress = prevProgress + (entry.getValue() - prevProgress) * partialTick;
                    if (currentProgress > 0.0f) {
                        int itemStartX;
                        int screenWidth = mc.getWindow().getGuiScaledWidth();
                        if (ClientCyberwareSettings.slideDirection == 0) {
                            itemStartX = -26 - 10;
                        } else {
                            itemStartX = screenWidth + 10;
                        }
                        int itemCurrentX = (int) (itemStartX + (barX - itemStartX) * currentProgress) + jitterX;
                        renderBarSlot(event.getGuiGraphics(), mc, userData, itemCurrentX, slotY, isMoveMode, rand, stack, cw, slotId, hudAlpha);
                    }
                }
            }
        }
    }

    private static boolean isHudActive(CyberwareUserData data) {
        IItemHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null && stack.is(ModItems.HUDJACK.get())) {
                if (cw.isActive(stack)) return true;
            }
        }
        return false;
    }

    public static void renderBatteryHud(GuiGraphics g, Minecraft mc, CyberwareUserData data, int x, int y, int jitterX, int jitterY, float hudAlpha) {
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
                a = hudAlpha;
                textColor = ((int) (hudAlpha * 255) << 24) | 0xFF0000;
            } else {
                r = 0.5f;
                gVal = 0.0f;
                b = 0.0f;
                a = hudAlpha;
                textColor = ((int) (hudAlpha * 255) << 24) | 0x880000;
            }
        } else {
            float[] userColor = ClientCyberwareSettings.getColorFloats();
            r = userColor[0];
            gVal = userColor[1];
            b = userColor[2];
            a = userColor[3] * hudAlpha;
            textColor = ((int) (a * 255) << 24) | (ClientCyberwareSettings.hudColor & 0xFFFFFF);
        }
        g.setColor(r, gVal, b, a);
        int startX = x + jitterX;
        int startY = y + jitterY;
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
        g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        int textX = startX + frameWidth + 4;
        int textY = startY + 4;
        String powerText = current + " / " + max;
        String diffText = "-" + cons + " / +" + prod;
        if (data.getEmpTicks() > 0 || bootTicks > 10 || (shutdownTicks > 0 && mc.level != null && mc.level.random.nextFloat() < 0.5f)) {
            powerText = scrambleText(powerText, 0.45f);
            diffText = scrambleText(diffText, 0.45f);
        }
        g.drawString(mc.font, powerText, textX, textY, textColor, true);
        g.drawString(mc.font, diffText, textX, textY + 10, textColor, true);
        RenderSystem.disableBlend();
    }

    private static void renderBarSlot(GuiGraphics g, Minecraft mc, CyberwareUserData data, int startX, int slotY, boolean isMoveMode, RandomSource rand, ItemStack stack, ICyberware cw, int slotId, float hudAlpha) {
        int hudColor = ClientCyberwareSettings.hudColor;
        int width = 26;
        int height = 20;
        float[] floats = ClientCyberwareSettings.getColorFloats();
        g.setColor(floats[0], floats[1], floats[2], floats[3] * hudAlpha);
        int outlineColor = ((int) (floats[3] * hudAlpha * 255) << 24) | (hudColor & 0xFFFFFF);
        int bLen = 5;
        int bThk = 1;
        g.fill(startX, slotY, startX + bLen, slotY + bThk, outlineColor);
        g.fill(startX, slotY, startX + bThk, slotY + bLen, outlineColor);
        g.fill(startX + width - bLen, slotY, startX + width, slotY + bThk, outlineColor);
        g.fill(startX + width - bThk, slotY, startX + width, slotY + bLen, outlineColor);
        g.fill(startX, slotY + height - bThk, startX + bLen, slotY + height, outlineColor);
        g.fill(startX, slotY + height - bLen, startX + bThk, slotY + height, outlineColor);
        g.fill(startX + width - bLen, slotY + height - bThk, startX + width, slotY + height, outlineColor);
        g.fill(startX + width - bThk, slotY + height - bLen, startX + width, slotY + height, outlineColor);
        g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (isMoveMode) {
            ItemStack eyePlaceholder = new ItemStack(ModItems.CYBER_EYE.get());
            g.setColor(0.35f, 0.35f, 0.35f, hudAlpha);
            RenderSystem.setShaderColor(0.35f, 0.35f, 0.35f, hudAlpha);
            g.renderItem(eyePlaceholder, startX + 5, slotY + 2);
            g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            return;
        }
        boolean isToggledOn = cw.isActive(stack);
        boolean isEmpOffline = isToggledOn && data.getEmpTicks() > 0;
        boolean isNoPower = isToggledOn && cw.getEnergyConsumption(stack) > 0 && data.getEnergyStored() < cw.getEnergyConsumption(stack) && data.getEmpTicks() <= 0;
        float r = 1.0f, gVal = 1.0f, b = 1.0f, a = hudAlpha;
        if (isEmpOffline) {
            r = 0.5f;
            gVal = 0.15f;
            b = 0.15f;
        } else if (isNoPower) {
            r = 0.45f;
            gVal = 0.4f;
            b = 0.15f;
        } else {
            r = 0.25f;
            gVal = 0.25f;
            b = 0.25f;
        }
        g.setColor(r, gVal, b, a);
        RenderSystem.setShaderColor(r, gVal, b, a);
        g.renderItem(stack, startX + 5, slotY + 2);
        g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (isEmpOffline) {
            RenderSystem.enableBlend();
            int staticLineColor = ((int) (a * 255) << 24) | 0xFF0000;
            for (int line = 0; line < 2; line++) {
                int lineY = (slotY + 2) + rand.nextInt(16);
                g.fill(startX + 5, lineY, startX + 21, lineY + 1, staticLineColor);
            }
            RenderSystem.disableBlend();
        }
        if (isNoPower) {
            boolean blink = (System.currentTimeMillis() % 1600) < 800;
            if (blink) {
                g.pose().pushPose();
                g.pose().translate(0.0F, 0.0F, 200.0F);
                int blinkColor = ((int) (a * 255) << 24) | 0xFF0000;
                g.drawString(mc.font, "!", startX + 15, slotY + 10, blinkColor, true);
                g.pose().popPose();
            }
        }
    }

    public static void renderImmunityHud(GuiGraphics g, Minecraft mc, CyberwareUserData data, int x, int y, float hudAlpha) {
        int ticks = data.getImmunityTime();
        if (ticks > 0) {
            int seconds = ticks / 20;
            int minutes = seconds / 60;
            String timeStr = String.format("%02d:%02d", minutes, seconds % 60);
            int hudColor = ClientCyberwareSettings.hudColor;
            int textColor = ((int) (hudAlpha * 255) << 24) | (hudColor & 0xFFFFFF);
            g.drawString(mc.font, "SUPPRESSANT: " + timeStr, x, y, textColor, true);
        }
    }

    private static String scrambleText(String original, float intensity) {
        char[] chars = original.toCharArray();
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != ' ' && chars[i] != '/' && rand.nextFloat() < intensity) {
                chars[i] = (char) (33 + rand.nextInt(93));
            }
        }
        return new String(chars);
    }
}