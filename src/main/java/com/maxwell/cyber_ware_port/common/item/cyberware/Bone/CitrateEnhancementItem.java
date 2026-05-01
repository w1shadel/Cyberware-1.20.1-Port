package com.maxwell.cyber_ware_port.common.item.cyberware.bone;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public class CitrateEnhancementItem extends CyberwareItem {
    public CitrateEnhancementItem(Properties p) {
        super(new Builder(p, 5, RobosurgeonBlockEntity.SLOT_BONES)
                .maxInstall(1));
    }

    @Override
    public void onLivingFall(LivingFallEvent event, ItemStack stack, LivingEntity wearer) {
        if (isActive(stack)) {
            event.setDamageMultiplier(event.getDamageMultiplier() * 0.5F);
        }
    }
}
