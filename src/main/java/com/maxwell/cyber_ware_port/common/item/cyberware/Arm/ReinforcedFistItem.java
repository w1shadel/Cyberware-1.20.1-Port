package com.maxwell.cyber_ware_port.common.item.cyberware.arm;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ReinforcedFistItem extends CyberwareItem {
    public ReinforcedFistItem(Properties p) {
        super(new Builder(p, 5, RobosurgeonBlockEntity.SLOT_HANDS).requires(ModItems.CYBER_ARM_LEFT, ModItems.CYBER_ARM_RIGHT).maxInstall(1));

    }

    @Override
    public void onHarvestCheck(PlayerEvent.HarvestCheck event, ItemStack stack, LivingEntity wearer) {
        if (!event.canHarvest() && wearer.getMainHandItem().isEmpty()) {
            event.setCanHarvest(true);
        }
    }

    @Override
    public void onBreakSpeed(PlayerEvent.BreakSpeed event, ItemStack stack, LivingEntity wearer) {
        if (wearer.getMainHandItem().isEmpty()) {
            event.setNewSpeed(4.0f);
        }
    }
}
