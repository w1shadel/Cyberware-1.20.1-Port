package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart;
import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
public class InternalDefibrillatorItem extends CyberwareItem {
    public InternalDefibrillatorItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)

                .energy(0, 0, 500, StackingRule.STATIC));
    }
}