package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class LinearActuatorsItem extends CyberwareItem {
    public LinearActuatorsItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_LEGS)
                .requires(ModItems.CYBER_LEG_RIGHT,ModItems.CYBER_LEG_LEFT)
                .maxInstall(1));
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {}

    public float getJumpBoost() { return 0.25f; }
}