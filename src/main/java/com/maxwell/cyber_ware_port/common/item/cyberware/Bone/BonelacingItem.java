package com.maxwell.cyber_ware_port.common.item.cyberware.bone;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BonelacingItem extends CyberwareItem {
    public BonelacingItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_BONES)
                .maxInstall(8)
                .addAttribute(Attributes.MAX_HEALTH, "bonelacing_health", 10.0, AttributeModifier.Operation.ADD_VALUE));
    }
}