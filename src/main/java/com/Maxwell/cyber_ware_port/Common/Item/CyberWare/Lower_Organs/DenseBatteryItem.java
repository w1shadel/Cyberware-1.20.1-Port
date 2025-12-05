package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.item.Rarity;

public class DenseBatteryItem extends CyberwareItem {
    public DenseBatteryItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(1)
                .energy(0, 0, 60000, StackingRule.STATIC)
                .properties(p -> p.rarity(Rarity.RARE)));
    }
}