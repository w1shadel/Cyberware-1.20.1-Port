package com.maxwell.cyber_ware_port.common.item.cyberware;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.event.CyberwareAbilityEvent;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.command.CyberwareCommands;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(modid = CyberWare.MODID)
public class ModCyberwareEvents {
    /**
     * インストールされたサイバーウェアを走査し、アクションを実行するヘルパーメソッド。
     * キャンセル可能な CyberwareAbilityEvent を発火させます。
     */
    private static void dispatch(LivingEntity entity, BiConsumer<ICyberware, ItemStack> action) {
        if (entity == null) return;
        entity.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            ItemStackHandler handler = data.getInstalledCyberware();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                // アイテムチェック && 有効化チェック
                if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cw && cw.isActive(stack)) {
                    // ★EVENT HOOK: 能力発動の可否を問う
                    CyberwareAbilityEvent event = new CyberwareAbilityEvent(entity, stack, cw);
                    if (MinecraftForge.EVENT_BUS.post(event)) {
                        continue; // キャンセルされたらこのパーツの処理をスキップ
                    }
                    // 処理実行
                    action.accept(cw, stack);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 1. 攻撃側の処理 (Attacker)
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            dispatch(attacker, (cw, stack) -> {
                cw.onLivingHurt(event, stack, attacker);
            });
        }
        // 2. 防御側の処理 (Target/Victim) - 装甲やダメージ軽減など
        dispatch(event.getEntity(), (cw, stack) -> {
            // ICyberware側で自分が攻撃者か被害者か判断する必要がありますが、
            // 引数としては被害者（イベント主体）を渡します。
            cw.onLivingHurt(event, stack, event.getEntity());
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (player instanceof ServerPlayer sp) {
                // CyberwareUserData.tick() 内で全パーツの onSystemTick() が呼ばれるため、
                // ここの dispatch は削除して一本化しました。
                data.tick(sp);
            }
        });
    }

    @SubscribeEvent
    public static void onEntityTeleport(EntityTeleportEvent event) {
        double targetX = event.getTargetX();
        double targetY = event.getTargetY();
        double targetZ = event.getTargetZ();
        double range = 16.0;
        net.minecraft.world.phys.AABB searchArea = new net.minecraft.world.phys.AABB(
                targetX - range, targetY - range, targetZ - range,
                targetX + range, targetY + range, targetZ + range
        );
        java.util.List<Player> players = event.getEntity().level().getEntitiesOfClass(Player.class, searchArea);
        for (Player player : players) {
            dispatch(player, (cw, stack) -> {
                cw.onEntityTeleport(event, stack, player);
            });
            if (event.isCanceled()) return;
        }
    }

    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
        dispatch(event.getEntity(), (cw, stack) -> cw.onItemUseTick(event, stack, event.getEntity()));
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            dispatch(player, (cw, stack) -> {
                if (!event.isCanceled()) cw.onLivingAttack(event, stack, player);
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
            // 誰かがDENYしたらそれ以降は処理しない（上書き防止）
            if (event.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
                cw.onPotionApplicable(event, stack, event.getEntity());
            }
        });
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        dispatch(event.getEntity(), (cw, stack) -> {
            if (!event.isCanceled()) cw.onLivingDeath(event, stack, event.getEntity());
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
        CyberwareCommands.register(event.getDispatcher());
    }
}