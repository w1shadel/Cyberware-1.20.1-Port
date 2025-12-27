package com.maxwell.cyber_ware_port.common.item.cyberware.Leg;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

public class AquaticPropulsionSystemItem extends CyberwareItem {
    public AquaticPropulsionSystemItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BOOTS)
                .maxInstall(1)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT)
                .energy(1, 0, 0, StackingRule.LINEAR)
                .addAttribute(
                        ForgeMod.SWIM_SPEED.get(),
                        "a1b2c3d4-5555-6666-7777-888899990000",
                        1,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                )
        );
    }
}