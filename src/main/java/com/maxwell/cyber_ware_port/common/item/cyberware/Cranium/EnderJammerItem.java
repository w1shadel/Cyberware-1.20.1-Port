package com.maxwell.cyber_ware_port.common.item.cyberware.cranium;


import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class EnderJammerItem extends CyberwareItem {
    public EnderJammerItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)
                .energy(5, 0, 0, StackingRule.LINEAR)
                .eventCost(200));
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public void onEntityTeleport(EntityTeleportEvent event, ItemStack stack, LivingEntity wearer) {
        if (this.tryConsumeEventEnergy(wearer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get()), stack)) {
            if (event.isCanceled()) {
                event.setCanceled(true);
            }
        }
    }
}
