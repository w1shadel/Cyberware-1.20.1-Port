package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lung;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;public class HyperoxygenationBoostItem extends CyberwareItem {
    public HyperoxygenationBoostItem() {
        super(new Builder(4, RobosurgeonBlockEntity.SLOT_LUNGS)
                .maxInstall(3)
                .energy(1, 0, 0, StackingRule.LINEAR));

    }
    @Override
    public void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {

        if (entity.isSprinting()) {
            int cost = 2;

            if (energyStorage.extractEnergy(cost, true) == cost) {
                energyStorage.extractEnergy(cost, false);
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, 0, false, false, false));

            }
        }
    }
    @Override
    public boolean hasEnergyProperties(ItemStack stack) { return true;
 }
}