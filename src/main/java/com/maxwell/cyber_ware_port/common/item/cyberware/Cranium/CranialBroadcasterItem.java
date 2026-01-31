package com.maxwell.cyber_ware_port.common.item.cyberware.Cranium;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CranialBroadcasterItem extends CyberwareItem {
    public CranialBroadcasterItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)
                .energy(2, 0, 0, StackingRule.STATIC)); // 毎秒ではなく毎チック微量を消費
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public void onSystemTick(LivingEntity wearer, ItemStack stack) {
    }
}