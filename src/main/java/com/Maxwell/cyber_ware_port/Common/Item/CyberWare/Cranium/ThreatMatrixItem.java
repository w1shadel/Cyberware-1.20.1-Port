package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
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
                    int dodgeCost = this.getEventConsumption(stack);
                    if (data.extractEnergy(dodgeCost, false) == dodgeCost) {
                        event.setCanceled(true);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 2.0f);
                    }
                });
            }
        }
    }
}