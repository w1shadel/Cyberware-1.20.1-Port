package com.maxwell.cyber_ware_port.client.upgrades.cybereye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.items.IItemHandler;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class EyeWareEvents {
    private static boolean isFeatureActive(Player player, Item item) {
        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        IItemHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.is(item)) {
                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (cw != null && !cw.isActive(stack)) return false;
                return data.getEnergyStored() > 0;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getCamera().getFluidInCamera() == FogType.WATER) {
            Player player = Minecraft.getInstance().player;
            if (player != null && isFeatureActive(player, ModItems.LIQUID_REFRACTION.get())) {
                event.setNearPlaneDistance(-8.0F);
                event.setFarPlaneDistance(200.0F);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        if (isFeatureActive(player, ModItems.DISTANCE_ENHANCER.get())) {
            event.setNewFovModifier(event.getNewFovModifier() * 0.5f);
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;
        if (isFeatureActive(player, ModItems.TARGETING_OVERLAY.get())) {
            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getCamera().getPosition();
            double range = 32.0;
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
        );
        AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, consumer, aabb, 1.0F, 0.0F, 0.0F, 1.0F);
        poseStack.popPose();
    }
}