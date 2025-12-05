package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class RetractableClawsItem extends CyberwareItem {
    private static final UUID DAMAGE_UUID = UUID.fromString("c0ffee00-1234-5678-abcd-1234567890ab");

    public RetractableClawsItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_HANDS)
                .maxInstall(1)
                .requires(ModItems.CYBER_ARM_LEFT, ModItems.CYBER_ARM_RIGHT)
        );
    }

    @Override
    public boolean canToggle(ItemStack stack) { return true; }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();

        map.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(DAMAGE_UUID, "Claw Damage", 4.0, AttributeModifier.Operation.ADDITION));
        return map;
    }
}