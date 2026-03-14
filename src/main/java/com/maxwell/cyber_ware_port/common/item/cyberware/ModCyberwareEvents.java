package com.maxwell.cyber_ware_port.common.item.cyberware;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.api.json.CyberwareDataManager;
import com.maxwell.cyber_ware_port.api.json.MobDataManager;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = CyberWare.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModCyberwareEvents {
    @SubscribeEvent
    public static void onRegisterReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new CyberwareDataManager());
        event.addListener(new MobDataManager());
    }

    private static void dispatch(LivingEntity entity, BiConsumer<ICyberware, ItemStack> action) {
        if (entity == null) return;
        CyberwareUserData data = entity.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        ItemStackHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (!stack.isEmpty() && cw != null && cw.isActive(stack)) {
                action.accept(cw, stack);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            dispatch(attacker, (cw, stack) -> cw.onLivingDamagePre(null, stack, attacker));
        }
        dispatch(event.getEntity(), (cw, stack) -> cw.onLivingDamagePre(null, stack, event.getEntity()));
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer sp) {
            CyberwareUserData data = sp.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            data.tick(sp);
            dispatch(sp, (cw, stack) -> cw.onPlayerTick(event, stack, sp));
        }
    }

    @SubscribeEvent
    public static void onEntityTeleport(EntityTeleportEvent event) {
        double targetX = event.getTargetX();
        double targetY = event.getTargetY();
        double targetZ = event.getTargetZ();
        double range = 16.0;
        AABB searchArea = new AABB(targetX - range, targetY - range, targetZ - range, targetX + range, targetY + range, targetZ + range);
        List<Player> players = event.getEntity().level().getEntitiesOfClass(Player.class, searchArea);
        for (Player player : players) {
            dispatch(player, (cw, stack) -> cw.onEntityTeleport(event, stack, player));
            if (((ICancellableEvent) event).isCanceled()) return;
        }
    }

    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
        dispatch(event.getEntity(), (cw, stack) -> cw.onItemUseTick(event, stack, event.getEntity()));
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            dispatch(player, (cw, stack) -> {
                if (!((ICancellableEvent) event).isCanceled()) {
                    cw.onLivingIncomingDamage(event, stack, player);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        dispatch(event.getEntity(), (cw, stack) -> cw.onLeftClickBlock(event, stack, event.getEntity()));
    }

    @SubscribeEvent
    public static void onPotionApplicable(MobEffectEvent.Applicable event) {
        dispatch(event.getEntity(), (cw, stack) -> {
            if (event.getResult() != MobEffectEvent.Applicable.Result.DO_NOT_APPLY) {
                cw.onPotionApplicable(event, stack, event.getEntity());
            }
        });
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        dispatch(event.getEntity(), (cw, stack) -> {
            if (!((ICancellableEvent) event).isCanceled()) {
                cw.onLivingDeath(event, stack, event.getEntity());
            }
        });
    }

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        dispatch(event.getEntity(), (cw, stack) -> cw.onHarvestCheck(event, stack, event.getEntity()));
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        dispatch(event.getEntity(), (cw, stack) -> cw.onBreakSpeed(event, stack, event.getEntity()));
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        dispatch(event.getEntity(), (cw, stack) -> cw.onLivingFall(event, stack, event.getEntity()));
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        dispatch(event.getEntity(), (cw, stack) -> cw.onLivingJump(event, stack, event.getEntity()));
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
    }
}