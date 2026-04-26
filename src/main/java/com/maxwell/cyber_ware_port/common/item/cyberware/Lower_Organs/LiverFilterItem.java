package com.maxwell.cyber_ware_port.common.item.cyberware.lower_organs;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class LiverFilterItem extends CyberwareItem {
    public LiverFilterItem(Properties p) {
        super(new Builder(p,5, RobosurgeonBlockEntity.SLOT_STOMACH).maxInstall(1));
    }

    @Override
    public boolean hasEnergyProperties(ItemStack stack) {
        return true;
    }

    @Override
    public void onPotionApplicable(MobEffectEvent.Applicable event, ItemStack stack, LivingEntity wearer) {
        if (event.getEffectInstance().getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
            CyberwareUserData data = wearer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            int cost = 50;
            if (data.extractEnergy(cost, false) == cost) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }
}