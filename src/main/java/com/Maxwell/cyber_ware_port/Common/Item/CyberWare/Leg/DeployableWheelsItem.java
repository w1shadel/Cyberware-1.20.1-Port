package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

import com.Maxwell.cyber_ware_port.Init.ModItems;

import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.energy.IEnergyStorage;


public class DeployableWheelsItem extends CyberwareItem {
    public DeployableWheelsItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BOOTS)
                .maxInstall(1)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT)
                .energy(2, 0, 0, StackingRule.LINEAR));
    }

    // Toggle機能を有効にする
    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }
}