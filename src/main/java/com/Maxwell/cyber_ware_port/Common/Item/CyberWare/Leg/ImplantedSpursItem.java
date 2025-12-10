package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
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