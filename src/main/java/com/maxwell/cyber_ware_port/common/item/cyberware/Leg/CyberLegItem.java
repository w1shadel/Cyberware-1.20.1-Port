package com.maxwell.cyber_ware_port.common.item.cyberware.leg;

import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class CyberLegItem extends CyberwareItem {
    public CyberLegItem(int slotId, RegistryObject<Item> incompatibleHumanPart,
                        com.maxwell.cyber_ware_port.common.item.base.BodyPartType bodyPartType) {
        super(new Builder(7, slotId)
                .maxInstall(1)
                .incompatible(incompatibleHumanPart)
                .bodyPart(bodyPartType)
                .energy(0, 10, 0, StackingRule.STATIC));
    }
}