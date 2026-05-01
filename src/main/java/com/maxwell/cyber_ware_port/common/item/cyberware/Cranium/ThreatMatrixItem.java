package com.maxwell.cyber_ware_port.common.item.cyberware.cranium;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class ThreatMatrixItem extends CyberwareItem {
    public ThreatMatrixItem(Properties p) {
        super(new Builder(p, 15, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)
                .energy(8, 0, 0, StackingRule.STATIC)
                .eventCost(500));
    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public void onLivingIncomingDamage(LivingIncomingDamageEvent event, ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player)) return;
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) ||
                event.getSource().is(DamageTypeTags.IS_FIRE) ||
                event.getSource().is(DamageTypeTags.IS_FALL)) {
            return;
        }
        boolean isLightlyArmored = player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() &&
                player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        if (isLightlyArmored && player.getRandom().nextFloat() < 0.3f) {
            if (tryConsumeEventEnergy(player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get()), stack)) {
                event.setCanceled(true);
                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 2.0f);
            }
        }
    }
}