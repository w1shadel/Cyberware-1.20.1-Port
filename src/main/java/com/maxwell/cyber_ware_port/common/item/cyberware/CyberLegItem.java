package com.maxwell.cyber_ware_port.common.item.cyberware;

import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class CyberLegItem extends CyberwareItem {
    public CyberLegItem(int slotId, RegistryObject<Item> incompatibleHumanPart) {
        super(new Builder(10, slotId)
                .maxInstall(1)
                .incompatible(incompatibleHumanPart)
                .bodyPart(BodyPartType.LEG)
                .energy(0, 2, 0, StackingRule.STATIC));

    }
}