package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Eye;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class LowLightVisionItem extends CyberwareItem {

    public LowLightVisionItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .energy(2, 0, 0, StackingRule.STATIC) 
                .requires(ModItems.CYBER_EYE)
        );
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {

        return isActive(stack) ? super.getEnergyConsumption(stack) : 0;
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {

        if (isActive(stack)) {
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
    }
}