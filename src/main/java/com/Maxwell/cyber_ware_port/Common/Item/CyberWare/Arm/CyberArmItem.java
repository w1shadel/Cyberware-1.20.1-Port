package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm;

import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class CyberArmItem extends CyberwareItem {

    public CyberArmItem(int slotId, RegistryObject<Item> incompatibleHumanPart) {
        super(new Builder(7, slotId)
                .maxInstall(1)
                .incompatible(incompatibleHumanPart)
                .bodyPart(BodyPartType.ARM)
                .energy(2, 0, 0, StackingRule.STATIC));

    }
}