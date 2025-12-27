package com.maxwell.cyber_ware_port.common.item.cyberware.Arm;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class RetractableClawsItem extends CyberwareItem {

    public RetractableClawsItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_HANDS)
                .maxInstall(4)
                .requires(ModItems.CYBER_ARM_LEFT, ModItems.CYBER_ARM_RIGHT)
                .addAttribute(Attributes.ATTACK_DAMAGE,
                        "c0ffee00-1234-5678-abcd-1234567890ab",
                        4.0,
                        AttributeModifier.Operation.ADDITION)
        );

    }
}