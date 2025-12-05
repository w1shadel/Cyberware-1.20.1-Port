package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class SolarskinItem extends CyberwareItem {
    public SolarskinItem() {
        super(new Builder(4, RobosurgeonBlockEntity.SLOT_SKIN).maxInstall(4));
    }

    @Override
    public boolean hasEnergyProperties(ItemStack stack) { return true; }

    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {
        if (!entity.level().isClientSide && entity.level().isDay() &&
                !entity.level().isRaining() && entity.level().canSeeSky(entity.blockPosition())) {

            energyStorage.receiveEnergy(5, false);
        }
    }
}