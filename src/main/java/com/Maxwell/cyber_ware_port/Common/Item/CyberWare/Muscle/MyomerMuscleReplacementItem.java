package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Muscle;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class MyomerMuscleReplacementItem extends CyberwareItem {

    public MyomerMuscleReplacementItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_MUSCLE)
                .maxInstall(10)
                .energy(6, 0, 0, StackingRule.LINEAR)
                .incompatible(ModItems.HUMAN_MUSCLE)
                .bodyPart(BodyPartType.MUSCLE)
                .addAttribute(Attributes.MOVEMENT_SPEED,
                        "a1b2c3d4-e5f6-7890-1234-56789abcdef0",
                        0.15,
                        AttributeModifier.Operation.MULTIPLY_TOTAL)
                .addAttribute(Attributes.ATTACK_DAMAGE,
                        "0fedcba9-8765-4321-0987-654321fedcba",
                        3.0,
                        AttributeModifier.Operation.ADDITION)
        );

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;

    }
}