package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ReinforcedFistItem extends CyberwareItem {
    public ReinforcedFistItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_HANDS).requires(ModItems.CYBER_ARM_LEFT, ModItems.CYBER_ARM_RIGHT).maxInstall(1));

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