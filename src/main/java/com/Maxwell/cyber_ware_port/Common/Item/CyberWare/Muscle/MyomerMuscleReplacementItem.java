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

public class MyomerMuscleReplacementItem extends CyberwareItem {
    private static final UUID MOVEMENT_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-1234-56789abcdef0");
    private static final UUID DAMAGE_UUID = UUID.fromString("0fedcba9-8765-4321-0987-654321fedcba");

    public MyomerMuscleReplacementItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_MUSCLE)
                .maxInstall(10)
                .energy(6, 0, 0, StackingRule.LINEAR));
    }
    @Override
    public boolean canToggle(ItemStack stack) { return true; }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();

        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_UUID, "Myomer Speed", 0.15, AttributeModifier.Operation.MULTIPLY_TOTAL));
        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(DAMAGE_UUID, "Myomer Strength", 3.0, AttributeModifier.Operation.ADDITION));
        return map;
    }
}