package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.item.ItemStack;public class NeuralContextualizerItem extends CyberwareItem {
    public NeuralContextualizerItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)

                .energy(1, 0, 0, StackingRule.STATIC));

    }

    @Override public boolean canToggle(ItemStack stack) { return true;
 }
}