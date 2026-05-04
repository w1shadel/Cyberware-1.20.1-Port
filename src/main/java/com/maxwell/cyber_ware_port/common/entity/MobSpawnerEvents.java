package com.maxwell.cyber_ware_port.common.entity;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.radio.RadioKitBlock;
import com.maxwell.cyber_ware_port.common.block.radio.tower.RadioTowerCoreBlock;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = CyberWare.MODID)
public class MobSpawnerEvents {
    private static final double RADIO_KIT_BOOST = 0.3;
    private static final double RADIO_TOWER_BOOST = 0.15;
    private static final long ACTIVE_TIMEOUT = 420;
    private static final double PLAYER_BEACON_BOOST = 0.25;

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || event.loadedFromDisk()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Mob vanillaMob)) return;
        if (vanillaMob.getSpawnType() == EntitySpawnReason.CONVERSION) {
            return;
        }
        EntityType<?> type = entity.getType();
        var mobData = com.maxwell.cyber_ware_port.api.json.MobDataManager.MOB_DATA.get(type);
        if (mobData != null && mobData.replaceWith != null) {
            double bonusChance = calculateBonusChance(level, vanillaMob.blockPosition());
            double totalChance = mobData.chance + bonusChance;
            if (level.getRandom().nextFloat() < totalChance) {
                tryReplaceMob(event, level, vanillaMob, mobData.replaceWith);
            }
        }
    }

    private static void tryReplaceMob(EntityJoinLevelEvent event, ServerLevel level, Mob original, EntityType<?> newType) {
        try {
            Entity spawned = newType.create(level, EntitySpawnReason.CONVERSION);
            if (spawned instanceof Mob customMob) {
                customMob.setPos(original.getX(), original.getY(), original.getZ());
                customMob.yBodyRot = original.yBodyRot;
                customMob.yHeadRot = original.yHeadRot;
                customMob.finalizeSpawn(level, level.getCurrentDifficultyAt(original.blockPosition()), EntitySpawnReason.CONVERSION, null);
                level.addFreshEntity(customMob);
                event.setCanceled(true);
            }
        } catch (Exception e) {
            CyberWare.LOGGER.error("Failed to replace mob: {}", e.getMessage());
        }
    }

    private static double calculateBonusChance(ServerLevel level, BlockPos spawnPos) {
        double bonus = 0.0;
        ResourceKey<Level> dimKey = level.dimension();
        long currentTime = level.getGameTime();
        if (isActive(RadioKitBlock.LAST_ACTIVE_TIME, dimKey, currentTime)) {
            bonus += RADIO_KIT_BOOST;
        }
        if (isActive(RadioTowerCoreBlock.LAST_TOWER_ACTIVE_TIME, dimKey, currentTime)) {
            bonus += RADIO_TOWER_BOOST;
        }
        List<? extends Player> players = level.players();
        for (Player player : players) {
            if (player.distanceToSqr(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()) < 2304) {
                if (isCranialBroadcasterActive(player)) {
                    bonus += PLAYER_BEACON_BOOST;
                    break;
                }
            }
        }
        return bonus;
    }

    private static boolean isCranialBroadcasterActive(Player player) {
        return player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get()).isCyberwareInstalled(ModItems.CRANIAL_BROADCASTER.get());
    }

    private static boolean isActive(Map<ResourceKey<Level>, Long> timeMap, ResourceKey<Level> dimKey, long currentTime) {
        Long lastActive = timeMap.get(dimKey);
        if (lastActive == null) {
            return false;
        }
        return Math.abs(currentTime - lastActive) < ACTIVE_TIMEOUT;
    }
}