package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

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

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;

    }
}