package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.item.Rarity;public class CreativeBatteryItem extends CyberwareItem {
    public CreativeBatteryItem() {
        super(new Builder(0, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(1)
                .energy(0, 1_000_000, 2_000_000_000, StackingRule.STATIC)
                .properties(p -> p.rarity(Rarity.EPIC))
        );

    }
}