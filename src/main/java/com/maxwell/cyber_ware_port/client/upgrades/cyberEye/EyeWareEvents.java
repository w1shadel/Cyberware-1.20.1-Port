package com.maxwell.cyber_ware_port.client.upgrades.cybereye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
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
                    for (int i = 0;
                         i < handler.getSlots();
                         i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        if (stack.getItem() == item) {
                            ICyberware cw = CyberwareAPI.getCyberware(stack);
                            if (cw != null && !cw.isActive(stack)) return false;
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
        if (isFeatureActive(player, ModItems.DISTANCE_ENHANCER.get())) {
            event.setNewFovModifier(event.getNewFovModifier() * 0.5f);
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
    }

}