package com.maxwell.cyber_ware_port.common.item.cyberware.Arm;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class RetractableClawsItem extends CyberwareItem {

    public RetractableClawsItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_HANDS)
                .maxInstall(4)
                .requires(ModItems.CYBER_ARM_LEFT, ModItems.CYBER_ARM_RIGHT)
        );

    }
    @Override
    public void onLivingHurt(LivingHurtEvent event, ItemStack stack, LivingEntity attacker) {

        if (attacker.getMainHandItem().isEmpty()) {

            float bonusDamage = 4.0f;
            event.setAmount(event.getAmount() + bonusDamage);
        }
    }
}