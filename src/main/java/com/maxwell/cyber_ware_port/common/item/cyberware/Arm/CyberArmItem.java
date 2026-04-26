package com.maxwell.cyber_ware_port.common.item.cyberware.arm;

import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CyberArmItem extends CyberwareItem {
    public CyberArmItem(Properties properties, int slotId, DeferredHolder<Item, CyberwareItem> incompatibleHumanPart,
                        BodyPartType bodyPartType) {
        super(new Builder(properties,7, slotId)
                .maxInstall(1)
                .incompatible(incompatibleHumanPart)
                .bodyPart(bodyPartType)
                .energy(2, 0, 0, StackingRule.STATIC));
    }
}