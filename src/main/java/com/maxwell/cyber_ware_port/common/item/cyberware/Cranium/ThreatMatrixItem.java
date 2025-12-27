package com.maxwell.cyber_ware_port.common.item.cyberware.Cranium;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class ThreatMatrixItem extends CyberwareItem {
    public ThreatMatrixItem() {
        super(new Builder(15, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)
                .energy(8, 0, 0, StackingRule.STATIC)
                .eventCost(500));
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public void onLivingAttack(LivingAttackEvent event, ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player)) return;
        boolean isLightlyArmored = player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        if (isLightlyArmored) {
            if (player.getRandom().nextFloat() < 0.3f) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    if (tryConsumeEventEnergy(data, stack)) {
                        event.setCanceled(true);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 2.0f);
                    }
                });
            }
        }
    }
}