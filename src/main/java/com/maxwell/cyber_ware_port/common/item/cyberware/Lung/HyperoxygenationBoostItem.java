package com.maxwell.cyber_ware_port.common.item.cyberware.Lung;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public class HyperoxygenationBoostItem extends CyberwareItem {
    public HyperoxygenationBoostItem() {
        super(new Builder(4, RobosurgeonBlockEntity.SLOT_LUNGS)
                .maxInstall(3)
                .energy(2, 0, 0, StackingRule.LINEAR));
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer.isSprinting()) {
            wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                int cost = this.getEnergyConsumption(stack);
                if (data.extractEnergy(cost, true) == cost) {
                    data.extractEnergy(cost, false);
                    wearer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, 0, false, false, false));
                }
            });
        }
    }

    @Override
    public boolean hasEnergyProperties(ItemStack stack) {
        return true;
    }
}