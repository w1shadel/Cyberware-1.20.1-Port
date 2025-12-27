package com.maxwell.cyber_ware_port.common.item.cyberware.Muscle;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class WiredReflexesItem extends CyberwareItem {

    public WiredReflexesItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_MUSCLE)
                .maxInstall(3)
                .energy(2, 0, 0, StackingRule.LINEAR)
                .addAttribute(Attributes.ATTACK_SPEED,
                        "f420e7e0-1c4b-4b12-9c1f-9e7f7c123456",
                        0.20,
                        AttributeModifier.Operation.MULTIPLY_TOTAL)
        );

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;

    }

    @Override
    public void onLivingAttack(LivingAttackEvent event, ItemStack stack, LivingEntity wearer) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                int reflexCost = 10;
                if (data.extractEnergy(reflexCost, false) == reflexCost) {
                    wearer.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, attacker.getEyePosition());
                }
            });
        }
    }
}