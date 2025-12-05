package com.Maxwell.cyber_ware_port.Client.Upgrades.CyberEye;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class EyeWareEvents {

    private static boolean isFeatureActive(Player player, net.minecraft.world.item.Item item) {
        return player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY)
                .map(data -> {
                    ItemStackHandler handler = data.getInstalledCyberware();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        if (stack.getItem() == item) {

                            if (item instanceof ICyberware cw && !cw.isActive(stack)) return false;

                            return data.getEnergyStored() > 0;
                        }
                    }
                    return false;
                }).orElse(false);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getCamera().getFluidInCamera() == FogType.WATER) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            if (isFeatureActive(player, ModItems.LIQUID_REFRACTION.get())) {

                event.setNearPlaneDistance(-8.0F);
                event.setFarPlaneDistance(200.0F);

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        if (isFeatureActive(player, ModItems.DISTANCE_ENHANCER.get())) {event.setNewFovModifier(event.getNewFovModifier() * 0.5f);
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (isFeatureActive(player, ModItems.TARGETING_OVERLAY.get())) {
            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getCamera().getPosition();double range = 32.0;
            for (Entity e : mc.level.entitiesForRendering()) {
                if (e instanceof LivingEntity && e != player && e.isAlive()) {
                    if (e.distanceToSqr(player) < range * range) {
                        renderEntityOutline(poseStack, e, cameraPos);
                    }
                }
            }
        }
    }

    private static void renderEntityOutline(PoseStack poseStack, Entity entity, Vec3 cameraPos) {
        poseStack.pushPose();

        poseStack.translate(
                entity.getX() - cameraPos.x,
                entity.getY() - cameraPos.y,
                entity.getZ() - cameraPos.z
        );AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());

        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(poseStack, consumer, aabb, 1.0F, 0.0F, 0.0F, 1.0F);

        poseStack.popPose();
    }
}