package com.maxwell.cyber_ware_port.common.item.cyberware.Eye;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CybereyesItem extends CyberwareItem {
    public CybereyesItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .bodyPart(BodyPartType.EYES)
                .incompatible(ModItems.HUMAN_EYES)
                .energy(1, 0, 0, StackingRule.STATIC));
    }

    @Override
    public void onSystemTick(LivingEntity wearer, ItemStack stack) {
        if (!isActive(stack)) {
            return;
        }
        if (wearer.hasEffect(MobEffects.BLINDNESS)) {
            wearer.removeEffect(MobEffects.BLINDNESS);
        }
    }
}