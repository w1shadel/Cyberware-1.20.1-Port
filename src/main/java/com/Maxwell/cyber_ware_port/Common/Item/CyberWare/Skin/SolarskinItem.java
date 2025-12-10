package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.item.ItemStack;public class SolarskinItem extends CyberwareItem {
    public SolarskinItem() {
        super(new Builder(4, RobosurgeonBlockEntity.SLOT_SKIN).maxInstall(4));

    }
    @Override
    public boolean hasEnergyProperties(ItemStack stack) {
        return true;
    }
}