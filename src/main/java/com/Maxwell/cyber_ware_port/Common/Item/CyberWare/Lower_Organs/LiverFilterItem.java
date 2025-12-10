package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;public class LiverFilterItem extends CyberwareItem {
    public LiverFilterItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_STOMACH).maxInstall(1));

    }

    @Override
    public boolean hasEnergyProperties(net.minecraft.world.item.ItemStack stack) { return true;
 }
}