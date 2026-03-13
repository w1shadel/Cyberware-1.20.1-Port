package com.maxwell.cyber_ware_port.common.item.cyberware.leg;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class DeployableWheelsItem extends CyberwareItem {
    public DeployableWheelsItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BOOTS)
                .maxInstall(1)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT)
                .energy(2, 0, 0, StackingRule.LINEAR)
                .addAttribute(
                        Attributes.STEP_HEIGHT,
                        "deployable_wheels_step_height",
                        1.0,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }
}