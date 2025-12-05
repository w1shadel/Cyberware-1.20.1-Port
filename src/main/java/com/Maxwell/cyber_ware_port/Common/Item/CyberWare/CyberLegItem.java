package com.Maxwell.cyber_ware_port.Common.Item.CyberWare;

import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;

public class CyberLegItem extends CyberwareItem {
    public CyberLegItem(int slotId) {
        super(new Builder(10, slotId)
                .maxInstall(1)
                .incompatible(ModItems.HUMAN_LEFT_LEG,ModItems.HUMAN_RIGHT_LEG)
                .bodyPart(BodyPartType.LEG)
                .energy(0, 2, 0, StackingRule.STATIC));
    }
}