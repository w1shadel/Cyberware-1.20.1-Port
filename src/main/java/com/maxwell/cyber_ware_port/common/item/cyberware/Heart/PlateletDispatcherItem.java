package com.maxwell.cyber_ware_port.common.item.cyberware.Heart;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public class PlateletDispatcherItem extends CyberwareItem {
    public PlateletDispatcherItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .energy(2, 0, 0, StackingRule.STATIC));

    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        float ratio = wearer.getHealth() / wearer.getMaxHealth();
        if (ratio >= 0.8f && ratio < 1.0f) {
            if (wearer.tickCount % 40 == 0) {
                wearer.heal(1.0f);
            }
        }
    }
}