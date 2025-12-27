package com.maxwell.cyber_ware_port.common.item.cyberware.Arm;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

public class RapidFireFlywheelItem extends CyberwareItem {
    public RapidFireFlywheelItem() {
        super(new Builder(8, RobosurgeonBlockEntity.SLOT_ARMS)
                .maxInstall(1)
        );

    }

    @Override
    public void onItemUseTick(LivingEntityUseItemEvent.Tick event, ItemStack stack, LivingEntity wearer) {
        if (event.getItem().getItem() instanceof BowItem) {
            wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
                int costPerTick = 2;
                if (userData.extractEnergy(costPerTick, false) == costPerTick) {
                    event.setDuration(event.getDuration() - 1);
                }
            });
        }
    }
}