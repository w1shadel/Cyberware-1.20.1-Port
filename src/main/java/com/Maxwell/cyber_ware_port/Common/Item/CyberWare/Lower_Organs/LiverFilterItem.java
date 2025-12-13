package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.MobEffectEvent;

public class LiverFilterItem extends CyberwareItem {
    public LiverFilterItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_STOMACH).maxInstall(1));

    }

    @Override
    public boolean hasEnergyProperties(net.minecraft.world.item.ItemStack stack) {
        return true;
    }

    @Override
    public void onPotionApplicable(MobEffectEvent.Applicable event, ItemStack stack, LivingEntity wearer) {
        if (event.getEffectInstance().getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                int cost = 50;
                if (data.extractEnergy(cost, false) == cost) {
                    event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                }
            });
        }
    }
}