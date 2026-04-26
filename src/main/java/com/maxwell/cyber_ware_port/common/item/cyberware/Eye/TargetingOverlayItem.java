package com.maxwell.cyber_ware_port.common.item.cyberware.eye;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

public class TargetingOverlayItem extends CyberwareItem {
    public TargetingOverlayItem(Properties p) {
        super(new Builder(p,3, RobosurgeonBlockEntity.SLOT_EYES)
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
    public void onPlayerTick(PlayerTickEvent.Post event, ItemStack stack, LivingEntity wearer) {
        if (!isActive(stack) || wearer.level().isClientSide()) return;
        if (wearer instanceof Player player) {
            double range = 32.0;
            AABB area = player.getBoundingBox().inflate(range);
            List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive());
            for (LivingEntity target : entities) {
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, true, false));
            }
        }
    }
}
