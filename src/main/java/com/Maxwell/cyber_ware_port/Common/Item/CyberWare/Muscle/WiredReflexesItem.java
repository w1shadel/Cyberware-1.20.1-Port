package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Muscle;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class WiredReflexesItem extends CyberwareItem {
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("f420e7e0-1c4b-4b12-9c1f-9e7f7c123456");

    public WiredReflexesItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_MUSCLE)
                .maxInstall(3)
                .energy(2, 0, 0, StackingRule.LINEAR));
    }

    @Override
    public boolean canToggle(ItemStack stack) { return true; }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();

        map.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_UUID, "Wired Reflexes Speed", 0.20, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return map;
    }
}