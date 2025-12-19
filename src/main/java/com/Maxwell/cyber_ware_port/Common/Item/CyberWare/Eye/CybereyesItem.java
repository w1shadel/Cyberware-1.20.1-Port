package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Eye;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public class CybereyesItem extends CyberwareItem {

    public CybereyesItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .bodyPart(BodyPartType.EYES)
                .incompatible(ModItems.HUMAN_EYES)
                .energy(1, 0, 0, StackingRule.STATIC));

    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer.hasEffect(MobEffects.BLINDNESS)) {
            wearer.removeEffect(MobEffects.BLINDNESS);
        }
    }
}