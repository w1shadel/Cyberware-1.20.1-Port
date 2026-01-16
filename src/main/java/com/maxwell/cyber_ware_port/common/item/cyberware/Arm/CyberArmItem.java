package com.maxwell.cyber_ware_port.common.item.cyberware.Arm;

import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class CyberArmItem extends CyberwareItem {
    public CyberArmItem(int slotId, RegistryObject<Item> incompatibleHumanPart) {
        super(new Builder(7, slotId)
                .maxInstall(1)
                .incompatible(incompatibleHumanPart)
                .energy(2, 0, 0, StackingRule.STATIC));
    }
}