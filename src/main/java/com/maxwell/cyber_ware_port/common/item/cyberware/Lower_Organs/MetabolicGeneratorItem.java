package com.maxwell.cyber_ware_port.common.item.cyberware.lower_organs;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MetabolicGeneratorItem extends CyberwareItem {
    public MetabolicGeneratorItem(Properties p) {
        super(new Builder(p,5, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(2)
                .energy(0, 200, 200, StackingRule.LINEAR));
    }

    @Override
    public void onSystemTick(LivingEntity wearer, ItemStack stack) {
        if (!(wearer instanceof Player player)) return;
        if (player.tickCount % 20 != 0) return;
        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        if (data.getEnergyStored() < data.getMaxEnergyStored()) {
            int genAmount = 200;
            if (player.getFoodData().getFoodLevel() > 6) {
                if (player.getFoodData().getSaturationLevel() > 0) {
                    if (data.receiveEnergy(genAmount, true) == genAmount) {
                        player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() - 1.0f);
                        data.receiveEnergy(genAmount, false);
                    }
                } else {
                    if (data.receiveEnergy(genAmount, true) == genAmount) {
                        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - 1);
                        data.receiveEnergy(genAmount, false);
                    }
                }
            }
        }
    }
}