package com.maxwell.cyber_ware_port.common.entity;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.radio.RadioKitBlock;
import com.maxwell.cyber_ware_port.common.block.radio.tower.RadioTowerCoreBlock;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobSpawnerEvents {
    private static final double RADIO_KIT_BOOST = 0.3;
    private static final double RADIO_TOWER_BOOST = 0.15;
    private static final long ACTIVE_TIMEOUT = 420;

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()) {
            return;
        }
        if (!event.getLevel().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob vanillaMob)) return;
        ServerLevel level = (ServerLevel) event.getLevel();
        double bonusChance = calculateBonusChance(level, vanillaMob.blockPosition());
        EntityType<?> type = entity.getType();
        if (type == EntityType.ZOMBIE) {
            double chance = CyberwareConfig.ZOMBIE_CONVERSION_CHANCE.get();
            tryReplaceMob(event, level, vanillaMob, ModEntities.CYBER_ZOMBIE.get(), chance + bonusChance);
        } else if (type == EntityType.SKELETON) {
            double chance = CyberwareConfig.SKELETON_CONVERSION_CHANCE.get();
            tryReplaceMob(event, level, vanillaMob, ModEntities.CYBER_SKELETON.get(), chance + bonusChance);
        } else if (type == EntityType.CREEPER) {
            double chance = CyberwareConfig.CREEPER_CONVERSION_CHANCE.get();
            tryReplaceMob(event, level, vanillaMob, ModEntities.CYBER_CREEPER.get(), chance + bonusChance);
        } else if (type == EntityType.WITHER_SKELETON) {
            if (isInsideFortress(level, vanillaMob.blockPosition())) {
                double chance = CyberwareConfig.WITHER_CONVERSION_CHANCE.get();
                tryReplaceMob(event, level, vanillaMob, ModEntities.CYBER_WITHER_SKELETON.get(), chance + bonusChance);
            }
        }
    }

    private static final double PLAYER_BEACON_BOOST = 0.25; // インプラントによるボーナス

    private static double calculateBonusChance(ServerLevel level, BlockPos spawnPos) {
        double bonus = 0.0;
        ResourceKey<Level> dimKey = level.dimension();
        long currentTime = level.getGameTime();
        // 1. 固定設置型（電波塔など）のチェック
        if (isActive(RadioKitBlock.LAST_ACTIVE_TIME, dimKey, currentTime)) bonus += RADIO_KIT_BOOST;
        if (isActive(RadioTowerCoreBlock.LAST_TOWER_ACTIVE_TIME, dimKey, currentTime)) bonus += RADIO_TOWER_BOOST;
        // 2. 周囲のプレイヤー（インプラント）のチェック
        // スポーン地点から一定範囲（例: 48ブロック）に起動中のブロードキャスターを持つプレイヤーがいるか
        List<? extends Player> players = level.players();
        for (Player player : players) {
            if (player.distanceToSqr(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()) < 48 * 48) {
                if (isCranialBroadcasterActive(player)) {
                    bonus += PLAYER_BEACON_BOOST;
                    break; // 一人いれば十分
                }
            }
        }
        return bonus;
    }

    private static boolean isCranialBroadcasterActive(Player player) {
        return player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).map(data -> {
            return data.isCyberwareInstalled(com.maxwell.cyber_ware_port.init.ModItems.CRANIAL_BROADCASTER.get());
        }).orElse(false);
    }

    private static boolean isActive(Map<ResourceKey<Level>, Long> timeMap, ResourceKey<Level> dimKey, long currentTime) {
        Long lastActive = timeMap.get(dimKey);
        if (lastActive == null) return false;
        return Math.abs(currentTime - lastActive) < ACTIVE_TIMEOUT;
    }

    private static void tryReplaceMob(EntityJoinLevelEvent event, ServerLevel level, Mob original, EntityType<?> newType, double chance) {
        if (level.getRandom().nextFloat() < chance) {
            Mob customMob = (Mob) newType.create(level);
            if (customMob != null) {
                customMob.moveTo(original.getX(), original.getY(), original.getZ(), original.getYRot(), original.getXRot());
                customMob.yBodyRot = original.yBodyRot;
                customMob.yHeadRot = original.yHeadRot;
                customMob.finalizeSpawn(level, level.getCurrentDifficultyAt(original.blockPosition()), MobSpawnType.CONVERSION, null, null);
                level.addFreshEntity(customMob);
                event.setCanceled(true);
            }
        }
    }

    private static boolean isInsideFortress(ServerLevel level, BlockPos pos) {
        return level.structureManager().getStructureWithPieceAt(pos, BuiltinStructures.FORTRESS).isValid();
    }
}