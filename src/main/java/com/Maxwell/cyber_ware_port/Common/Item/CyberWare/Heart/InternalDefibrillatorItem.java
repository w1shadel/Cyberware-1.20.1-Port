package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Config.CyberwareConfig;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.items.ItemStackHandler;

public class InternalDefibrillatorItem extends CyberwareItem {

    public InternalDefibrillatorItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_HEART)
                .maxInstall(1)
                .requires(ModItems.CARDIOMECHANIC_PUMP)
                .energy(0, 0, 0, StackingRule.STATIC)
                .eventCost(500));
    }

    @Override
    public void onLivingDeath(LivingDeathEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer.level().isClientSide()) return;
        wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (this.tryConsumeEventEnergy(data, stack)) {
                event.setCanceled(true);
                wearer.setHealth(wearer.getMaxHealth() * 0.5f);
                wearer.level().playSound(null, wearer.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
                if (CyberwareConfig.CONSUME_DEFIBRILLATOR_ON_USE.get()) {
                    ItemStackHandler handler = data.getInstalledCyberware();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stackInSlot = handler.getStackInSlot(i);
                        if (!stackInSlot.isEmpty() && stackInSlot.getItem() == this) {
                            handler.setStackInSlot(i, ItemStack.EMPTY);
                            break;
                        }
                    }
                }
                if (wearer instanceof ServerPlayer serverPlayer) {
                    data.recalculateCapacity(serverPlayer);
                    data.syncToClient(serverPlayer);
                }
            }
        });
    }
}