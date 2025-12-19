package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
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