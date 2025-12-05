package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class AquaticPropulsionSystemItem extends CyberwareItem {
    public AquaticPropulsionSystemItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BOOTS)
                .maxInstall(1)
                .requires(ModItems.CYBER_LEG_RIGHT,ModItems.CYBER_LEG_LEFT)
                .energy(1, 0, 0, StackingRule.LINEAR));
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {
        if (entity.isInWater()) {
            int cost = 1;
            if (energyStorage.extractEnergy(cost, true) == cost) {
                energyStorage.extractEnergy(cost, false);entity.setDeltaMovement(entity.getDeltaMovement().add(entity.getLookAngle().scale(0.05)));
            }
        }
    }
}