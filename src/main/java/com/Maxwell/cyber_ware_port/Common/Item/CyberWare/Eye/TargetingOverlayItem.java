package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Eye;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.item.ItemStack;public class TargetingOverlayItem extends CyberwareItem {
    public TargetingOverlayItem() {
        super(new Builder(3, RobosurgeonBlockEntity.SLOT_EYES)
                .maxInstall(1)
                .energy(1, 0, 0, StackingRule.STATIC) 
                .requires(ModItems.CYBER_EYE)
        );

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;

    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        return isActive(stack) ? super.getEnergyConsumption(stack) : 0;

    }}