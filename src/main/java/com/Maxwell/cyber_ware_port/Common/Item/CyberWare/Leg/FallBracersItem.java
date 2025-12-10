package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;public class FallBracersItem extends CyberwareItem {
    public FallBracersItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_LEGS)
                .requires(ModItems.CYBER_LEG_RIGHT,ModItems.CYBER_LEG_LEFT).maxInstall(1));

    }
}