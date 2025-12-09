package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Muscle;


import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraft.world.item.ItemStack;


public class WiredReflexesItem extends CyberwareItem {

    public WiredReflexesItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_MUSCLE)
                .maxInstall(3)
                .energy(2, 0, 0, StackingRule.LINEAR)

                .addAttribute(Attributes.ATTACK_SPEED,
                        "f420e7e0-1c4b-4b12-9c1f-9e7f7c123456",
                        0.20,
                        AttributeModifier.Operation.MULTIPLY_TOTAL)
        );

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;

    }
}