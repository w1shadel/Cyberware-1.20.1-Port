package com.maxwell.cyber_ware_port.common.item.cyberware.eye;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class TargetingOverlayItem extends CyberwareItem {
    public TargetingOverlayItem() {
        super(new Builder(3, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .energy(1, 0, 0, StackingRule.STATIC)
                .requires(ModItems.CYBER_EYE)
        );
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        return isActive(stack) ? super.getEnergyConsumption(stack) : 0;
    }

    @Override
    public void onSystemTick(LivingEntity entity, ItemStack stack) {
        if (isActive(stack)) {
            double range = 32.0;
            List<LivingEntity> entities = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(range));
            for (LivingEntity e : entities) {
                if (e != entity && e.isAlive()) {
                    e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
                }
            }
        }
    }
}