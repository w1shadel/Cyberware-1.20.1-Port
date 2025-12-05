package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Eye;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Set;

public class LowLightVisionItem extends CyberwareItem {

    public LowLightVisionItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .energy(0, 0, 0, StackingRule.STATIC) 

                .requires(ModItems.CYBER_EYE)
        );
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true; 
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {

        if (!isActive(stack)) return;

        int cost = 2; 

        if (energyStorage.extractEnergy(cost, true) == cost) {
            energyStorage.extractEnergy(cost, false);

            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
    }
}