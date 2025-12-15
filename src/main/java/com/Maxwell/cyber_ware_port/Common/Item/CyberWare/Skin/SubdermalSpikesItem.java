package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class SubdermalSpikesItem extends CyberwareItem {
    public SubdermalSpikesItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_SKIN).maxInstall(1));
    }

    @Override
    public void onLivingAttack(LivingAttackEvent event, ItemStack stack, LivingEntity wearer) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (event.getSource().getDirectEntity() != attacker) return;
            if (attacker != wearer) {
                float damageAmount = 2.0F;
                attacker.hurt(wearer.damageSources().thorns(wearer), damageAmount);
            }
        }
    }
}