package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Bone;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class BonelacingItem extends CyberwareItem {
    private static final UUID HEALTH_UUID = UUID.fromString("11223344-5566-7788-9900-aabbccddeeff");

    public BonelacingItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_BONES).maxInstall(1));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = ArrayListMultimap.create();

        map.put(Attributes.MAX_HEALTH, new AttributeModifier(HEALTH_UUID, "Bonelacing Health", 10.0, AttributeModifier.Operation.ADDITION));
        return map;
    }
}