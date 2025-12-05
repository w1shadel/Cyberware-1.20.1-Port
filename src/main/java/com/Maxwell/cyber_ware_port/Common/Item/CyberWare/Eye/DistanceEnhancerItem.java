package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Eye;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
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