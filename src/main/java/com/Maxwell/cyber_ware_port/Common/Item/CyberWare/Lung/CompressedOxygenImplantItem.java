package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lung;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;public class CompressedOxygenImplantItem extends CyberwareItem {
    public CompressedOxygenImplantItem() {
        super(new Builder(3, RobosurgeonBlockEntity.SLOT_LUNGS)
                .maxInstall(3)
                .requires(ModItems.HUMAN_LUNGS)
                .energy(5, 0, 0, StackingRule.LINEAR));

    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        return 0;
 
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {
        if (entity.getAirSupply() < entity.getMaxAirSupply()) {
            if (entity.tickCount % 20 == 0) {
                int cost = super.getEnergyConsumption(stack);if (energyStorage.extractEnergy(cost, true) == cost) {
                    energyStorage.extractEnergy(cost, false);

                    int newAir = Math.min(entity.getAirSupply() + 100, entity.getMaxAirSupply());

                    entity.setAirSupply(newAir);

                }
            }
        }
    }
}