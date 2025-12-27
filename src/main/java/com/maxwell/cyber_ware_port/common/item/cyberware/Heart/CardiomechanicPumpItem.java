package com.maxwell.cyber_ware_port.common.item.cyberware.Heart;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.MobEffectEvent;

public class CardiomechanicPumpItem extends CyberwareItem {
    public CardiomechanicPumpItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .bodyPart(BodyPartType.HEART)
                .incompatible(ModItems.HUMAN_HEART)
                .energy(2, 0, 0, StackingRule.STATIC));

    }

    @Override
    public void onPotionApplicable(MobEffectEvent.Applicable event, ItemStack stack, LivingEntity wearer) {
        if (event.getEffectInstance().getEffect() == net.minecraft.world.effect.MobEffects.WEAKNESS) {
            wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                int pumpCost = 50;
                if (data.extractEnergy(pumpCost, false) == pumpCost) {
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            });
        }
    }
}