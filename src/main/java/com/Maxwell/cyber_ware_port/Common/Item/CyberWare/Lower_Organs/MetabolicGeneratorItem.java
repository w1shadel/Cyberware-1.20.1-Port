package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public class MetabolicGeneratorItem extends CyberwareItem {
    public MetabolicGeneratorItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_STOMACH)
                .maxInstall(2)
                .energy(0, 200, 200, StackingRule.LINEAR));
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player)) return;
        if (player.tickCount % 20 != 0) return;
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
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
        });
    }


}