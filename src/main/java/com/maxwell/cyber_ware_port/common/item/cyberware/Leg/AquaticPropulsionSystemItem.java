package com.maxwell.cyber_ware_port.common.item.cyberware.leg;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AquaticPropulsionSystemItem extends CyberwareItem {
    public AquaticPropulsionSystemItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BOOTS)
                .maxInstall(1)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT)
                .energy(1, 0, 0, StackingRule.LINEAR)
                .addAttribute(
                        Attributes.WATER_MOVEMENT_EFFICIENCY,
                        "aquatic_propulsion_swim_speed",
                        0.5,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }
}