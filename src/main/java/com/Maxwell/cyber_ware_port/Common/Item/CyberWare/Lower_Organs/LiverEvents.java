package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LiverEvents {
    @SubscribeEvent
    public static void onPotionAdded(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {

            if (event.getEffectInstance().getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {

                    if (data.isCyberwareInstalled(ModItems.LIVER_FILTER.get())) {
                        int cost = 50; 
                        if (data.getEnergyStored() >= cost) {
                            data.extractEnergy(cost, false);

                            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                        }
                    }
                });
            }
        }
    }
}