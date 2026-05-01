package com.maxwell.cyber_ware_port.common.item.cyberware.heart;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class InternalDefibrillatorItem extends CyberwareItem {
    public InternalDefibrillatorItem(Properties p) {
        super(new Builder(p, 10, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .requires(ModItems.CARDIOMECHANIC_PUMP)
                .energy(0, 0, 0, StackingRule.STATIC)
                .eventCost(500));
    }

    @Override
    public void onLivingDeath(LivingDeathEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer.level().isClientSide()) return;
        CyberwareUserData data = wearer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        if (this.tryConsumeEventEnergy(data, stack)) {
            event.setCanceled(true);
            wearer.setHealth(wearer.getMaxHealth() * 0.5f);
            wearer.level().playSound(null, wearer.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
            if (CyberwareConfig.CONSUME_DEFIBRILLATOR_ON_USE.get()) {
                ItemStacksResourceHandler handler = data.getInstalledCyberware();
                for (int i = 0; i < handler.size(); i++) {
                    ItemStack stackInSlot = handler.getResource(i).toStack(handler.getAmountAsInt(i));
                    if (!stackInSlot.isEmpty() && stackInSlot.is(this)) {
                        handler.set(i, ItemResource.EMPTY, 0);
                        break;
                    }
                }
            }
            if (wearer instanceof ServerPlayer serverPlayer) {
                data.recalculateCapacity(serverPlayer);
                data.syncToClient(serverPlayer);
            }
        }
    }
}