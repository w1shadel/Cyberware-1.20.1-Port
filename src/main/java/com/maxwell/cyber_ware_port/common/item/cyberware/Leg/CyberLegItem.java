package com.maxwell.cyber_ware_port.common.item.cyberware.leg;

import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CyberLegItem extends CyberwareItem {
    public CyberLegItem(int slotId, DeferredHolder<Item, CyberwareItem> incompatibleHumanPart,
                        BodyPartType bodyPartType) {
        super(new Builder(7, slotId)
                .maxInstall(1)
                .incompatible(incompatibleHumanPart)
                .bodyPart(bodyPartType)
                .energy(0, 10, 0, StackingRule.STATIC));
    }
}