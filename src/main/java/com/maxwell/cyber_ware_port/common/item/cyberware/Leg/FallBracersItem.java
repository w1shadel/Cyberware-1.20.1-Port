package com.maxwell.cyber_ware_port.common.item.cyberware.leg;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public class FallBracersItem extends CyberwareItem {
    public FallBracersItem(Properties p) {
        super(new Builder(p, 5, RobosurgeonBlockEntity.SLOT_LEGS)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT).maxInstall(1));

    }

    @Override
    public void onLivingFall(LivingFallEvent event, ItemStack stack, LivingEntity wearer) {
        float reduction = 4.0F;
        event.setDistance(Math.max(0, event.getDistance() - reduction));
    }
}
