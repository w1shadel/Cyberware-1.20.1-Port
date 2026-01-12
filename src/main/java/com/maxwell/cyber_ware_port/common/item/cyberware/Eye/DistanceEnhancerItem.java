package com.maxwell.cyber_ware_port.common.item.cyberware.Eye;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.item.ItemStack;

public class DistanceEnhancerItem extends CyberwareItem {
    public DistanceEnhancerItem() {
        super(new Builder(2, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .requires(ModItems.CYBER_EYE)
        );
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }
}