package com.maxwell.cyber_ware_port.common.item.cyberware.Leg;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ImplantedSpursItem extends CyberwareItem {
    public ImplantedSpursItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BOOTS)
                .maxInstall(4)
                .addAttribute(Attributes.MOVEMENT_SPEED,
                        "c0a9b8e0-1234-4567-89ab-cdef01234567",
                        0.1,
                        AttributeModifier.Operation.MULTIPLY_TOTAL)
        );
    }
}