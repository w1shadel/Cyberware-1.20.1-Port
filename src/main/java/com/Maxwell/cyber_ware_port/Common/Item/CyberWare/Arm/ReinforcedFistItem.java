package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;

public class ReinforcedFistItem extends CyberwareItem {
    public ReinforcedFistItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_HANDS).requires(ModItems.CYBER_ARM_LEFT, ModItems.CYBER_ARM_RIGHT).maxInstall(1));

    }
}