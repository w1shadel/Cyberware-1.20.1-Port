package com.maxwell.cyber_ware_port.common.item.cyberware.skin;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class SubdermalSpikesItem extends CyberwareItem {
    public SubdermalSpikesItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_SKIN).maxInstall(1));
    }

    @Override
    public void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, LivingEntity wearer) {
        if (event == null) return;
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (event.getSource().getDirectEntity() != attacker) return;
            if (attacker != wearer) {
                float damageAmount = 2.0F;
                attacker.hurt(wearer.damageSources().thorns(wearer), damageAmount);
            }
        }
    }
}
