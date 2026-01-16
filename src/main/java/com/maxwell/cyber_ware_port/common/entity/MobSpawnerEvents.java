package com.maxwell.cyber_ware_port.common.entity;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.radio.RadioKitBlock;
import com.maxwell.cyber_ware_port.common.block.radio.tower.RadioTowerCoreBlock;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        double bonusChance = calculateBonusChance(level);
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

    private static double calculateBonusChance(ServerLevel level) {
        double bonus = 0.0;
        ResourceKey<Level> dimKey = level.dimension();
        long currentTime = level.getGameTime();
        if (isActive(RadioKitBlock.LAST_ACTIVE_TIME, dimKey, currentTime)) {
            bonus += RADIO_KIT_BOOST;
        }
        if (isActive(RadioTowerCoreBlock.LAST_TOWER_ACTIVE_TIME, dimKey, currentTime)) {
            bonus += RADIO_TOWER_BOOST;
        }
        return bonus;
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