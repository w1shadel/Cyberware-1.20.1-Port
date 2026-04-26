package com.maxwell.cyber_ware_port.client.upgrades.cybereye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.items.IItemHandler;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class EyeWareEvents {
    private static boolean isFeatureActive(Player player, Item item) {
        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        if (data == null) return false;
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
}
