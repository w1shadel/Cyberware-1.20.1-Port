package com.maxwell.cyber_ware_port.common.capability;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.util.CyberwareBodyStatus;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Collection;
import java.util.Iterator;

@EventBusSubscriber(modid = CyberWare.MODID)
public class CapabilityEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CyberwareUserData cap = serverPlayer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            if (!cap.isInitialized()) {
                cap.fillWithHumanParts();
            }
            cap.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get()).syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            CyberwareUserData data = serverPlayer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            data.recalculateCapacity(serverPlayer);
            data.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();
        CyberwareUserData oldData = original.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        CyberwareUserData newData = newPlayer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        if (!event.isWasDeath()) {
            newData.copyFrom(oldData);
        } else {
            if (CyberwareConfig.KEEP_CYBERWARE_ON_DEATH.get()) {
                newData.copyFrom(oldData);
                newData.setRespawnGracePeriod(12000);
                newData.ensureEssentialPartsAfterDeath();
            } else {
                newData.resetToHuman();
                ItemStackHandler handler = newData.getInstalledCyberware();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getOrDefault(CyberWare.GHOST_COMPONENT, false)) {
                        handler.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            }
        }
        if (newPlayer instanceof ServerPlayer serverPlayer) {
            newData.recalculateCapacity(serverPlayer);
            newData.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player) {
            Collection<ItemEntity> drops = event.getDrops();
            Iterator<ItemEntity> iterator = drops.iterator();
            while (iterator.hasNext()) {
                ItemStack stack = iterator.next().getItem();
                if (stack.getOrDefault(CyberWare.GHOST_COMPONENT, false)) {
                    iterator.remove();
                }
            }
        }
    }

    private static boolean isHandFunctional(Player player, InteractionHand hand) {
        if (player instanceof FakePlayer) return true;
        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        return new CyberwareBodyStatus(data.getInstalledCyberware()).isHandFunctional();
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!isHandFunctional(event.getEntity(), InteractionHand.MAIN_HAND)) {
            event.setNewSpeed(event.getOriginalSpeed() * 0.05f);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.is(ModBlocks.ROBO_SURGEON.get()) || state.is(ModBlocks.SURGERY_CHAMBER.get())) return;
        if (!isHandFunctional(event.getEntity(), InteractionHand.MAIN_HAND)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!isHandFunctional(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.is(ModBlocks.ROBO_SURGEON.get()) ||
                state.is(ModBlocks.SURGERY_CHAMBER.get()) ||
                state.is(BlockTags.DOORS) ||
                state.is(BlockTags.TRAPDOORS) ||
                state.is(BlockTags.BUTTONS) ||
                state.is(BlockTags.BEDS)) {
            return;
        }
        if (!isHandFunctional(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!isHandFunctional(event.getEntity(), event.getHand())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            CyberwareBodyStatus status = new CyberwareBodyStatus(data.getInstalledCyberware());
            if (!status.hasPart(BodyPartType.SKIN)) {
                event.setAmount(event.getAmount() * 1.5f);
            }
        }
    }
}