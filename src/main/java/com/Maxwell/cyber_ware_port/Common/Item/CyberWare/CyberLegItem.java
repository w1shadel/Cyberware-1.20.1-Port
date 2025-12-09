package com.Maxwell.cyber_ware_port.Common.Item.CyberWare;

import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
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