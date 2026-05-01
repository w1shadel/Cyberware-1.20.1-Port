package com.maxwell.cyber_ware_port.common.item.cyberware.lower_organs;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AdrenalinePumpItem extends CyberwareItem {
    public AdrenalinePumpItem(Properties p) {
        super(new Builder(p, 5, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(1)
                .energy(0, 0, 0, StackingRule.STATIC)
                .eventCost(500));
    }

    @Override
    public void onSystemTick(LivingEntity wearer, ItemStack stack) {
        if (wearer.getHealth() < wearer.getMaxHealth() * 0.3f && !wearer.hasEffect(MobEffects.STRENGTH)) {
            CyberwareUserData data = wearer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            if (tryConsumeEventEnergy(data, stack)) {
                wearer.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 0, false, false));
                wearer.addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 1, false, false));
            }
        }
    }
}