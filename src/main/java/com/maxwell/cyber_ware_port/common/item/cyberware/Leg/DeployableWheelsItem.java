package com.maxwell.cyber_ware_port.common.item.cyberware.Leg;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;

public class DeployableWheelsItem extends CyberwareItem {
    public DeployableWheelsItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BOOTS)
                .maxInstall(1)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT)
                .energy(2, 0, 0, StackingRule.LINEAR)
                .addAttribute(
                        ForgeMod.STEP_HEIGHT_ADDITION.get(),
                        "d6c3e8a0-1234-4a5b-8c9d-123456789abc",
                        0.65,
                        AttributeModifier.Operation.ADDITION
                )
        );
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }
}