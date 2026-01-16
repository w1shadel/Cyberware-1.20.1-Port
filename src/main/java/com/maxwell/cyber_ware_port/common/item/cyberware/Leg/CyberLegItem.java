package com.maxwell.cyber_ware_port.common.item.cyberware.Leg;

import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class CyberLegItem extends CyberwareItem {
    public CyberLegItem(int slotId, RegistryObject<Item> incompatibleHumanPart) {
        super(new Builder(7, slotId)
                .maxInstall(1)
                .incompatible(incompatibleHumanPart)
                .energy(0, 10, 0, StackingRule.STATIC));
    }
}