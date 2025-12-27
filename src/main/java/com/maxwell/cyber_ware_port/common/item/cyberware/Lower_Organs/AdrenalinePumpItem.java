package com.maxwell.cyber_ware_port.common.item.cyberware.Lower_Organs;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public class AdrenalinePumpItem extends CyberwareItem {
    public AdrenalinePumpItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(1)
                .energy(0, 0, 0, StackingRule.STATIC)
                .eventCost(500));
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer.getHealth() < wearer.getMaxHealth() * 0.3f && !wearer.hasEffect(MobEffects.DAMAGE_BOOST)) {
            wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                if (tryConsumeEventEnergy(data, stack)) {
                    wearer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0, false, false));
                    wearer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1, false, false));
                }
            });
        }
    }
}