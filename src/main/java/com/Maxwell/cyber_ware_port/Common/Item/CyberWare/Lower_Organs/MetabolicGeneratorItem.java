package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class MetabolicGeneratorItem extends CyberwareItem {
    public MetabolicGeneratorItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(64)

                .energy(0, 25, 25, StackingRule.LINEAR));
    }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {
        if (!(entity instanceof Player player)) return;

        if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {

            if (player.tickCount % 20 == 0) {

                if (player.getFoodData().getFoodLevel() > 6) {

                    if (player.getFoodData().getSaturationLevel() > 0) {
                        player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() - 1.0f);
                        energyStorage.receiveEnergy(200, false); 
                    } else {
                        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - 1);
                        energyStorage.receiveEnergy(200, false);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasEnergyProperties(ItemStack stack) { return true; }
}