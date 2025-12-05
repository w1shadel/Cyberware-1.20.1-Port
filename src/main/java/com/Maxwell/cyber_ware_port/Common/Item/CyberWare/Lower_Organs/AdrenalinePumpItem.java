package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class AdrenalinePumpItem extends CyberwareItem {
    public AdrenalinePumpItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_STOMACH).maxInstall(1));
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {
        if (entity.getHealth() < entity.getMaxHealth() * 0.3) {

            int cost = 150;
            if (energyStorage.extractEnergy(cost, true) == cost) {
                energyStorage.extractEnergy(cost, false);
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1, false, false));
            }
        }
    }

    @Override public boolean hasEnergyProperties(ItemStack stack) { return true; }
}