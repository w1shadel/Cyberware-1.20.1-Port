package com.maxwell.cyber_ware_port.common.entity;

import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobSpawnerEvents {

    private static long lastSpawnTime = 0;
    private static final RandomSource RANDOM = RandomSource.create();
    private static final List<Supplier<? extends EntityType<?>>> OVERWORLD_MOBS = List.of(
            ModEntities.CYBER_ZOMBIE,
            ModEntities.CYBER_SKELETON,
            ModEntities.CYBER_CREEPER
    );

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var server = event.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;
        if (!overworld.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }
        long currentTime = server.getTickCount();
        int cooldown = CyberwareConfig.SPAWN_INTERVAL.get();
        if (currentTime < lastSpawnTime + cooldown) return;
        if (!overworld.isNight()) return;
        if (overworld.players().isEmpty()) return;
        ServerPlayer player = overworld.players().get(RANDOM.nextInt(overworld.players().size()));
        int nearbyMonsters = overworld.getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(64),
                e -> e.getType().getCategory() == MobCategory.MONSTER
        ).size();
        if (nearbyMonsters >= CyberwareConfig.MAX_NEARBY_MOBS.get()) {
            return;
        }
        Supplier<? extends EntityType<?>> mobSupplier = OVERWORLD_MOBS.get(RANDOM.nextInt(OVERWORLD_MOBS.size()));
        EntityType<?> mobToSpawn = mobSupplier.get();
        if (trySpawnMob(overworld, player, mobToSpawn)) {
            lastSpawnTime = currentTime;
        }
    }

    private static boolean trySpawnMob(ServerLevel level, ServerPlayer player, EntityType<?> mobToSpawn) {
        for (int i = 0; i < 10; i++) {
            int x = (int) player.getX() + RANDOM.nextInt(48) - 24;
            int z = (int) player.getZ() + RANDOM.nextInt(48) - 24;
            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            BlockPos spawnPos = new BlockPos(x, y, z);
            if (!level.getFluidState(spawnPos.below()).isEmpty()) {
                continue;
            }
            if (!level.getFluidState(spawnPos).isEmpty()) {
                continue;
            }
            if (level.getBrightness(LightLayer.BLOCK, spawnPos) > 0) {
                continue;
            }
            if (!level.getBlockState(spawnPos.below()).isValidSpawn(level, spawnPos.below(), mobToSpawn)) {
                continue;
            }
            if (player.distanceToSqr(x, y, z) > 24 * 24 && level.noCollision(mobToSpawn.getAABB(x, y, z))) {
                if (!SpawnPlacements.checkSpawnRules(mobToSpawn, level, MobSpawnType.EVENT, spawnPos, (RandomSource) RANDOM)) {
                    continue;
                }
                Mob mob = (Mob) mobToSpawn.create(level);
                if (mob != null) {
                    mob.setPos(x + 0.5, y, z + 0.5);
                    level.addFreshEntity(mob);
                    return true;
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!event.getLevel().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            return;
        }
        if (!event.loadedFromDisk()) {
            if (event.getEntity().getType() == EntityType.WITHER_SKELETON) {
                ServerLevel level = (ServerLevel) event.getLevel();
                WitherSkeleton vanillaMob = (WitherSkeleton) event.getEntity();
                if (isInsideFortress(level, vanillaMob.blockPosition())) {
                    double chance = CyberwareConfig.WITHER_CONVERSION_CHANCE.get();
                    if (level.getRandom().nextFloat() < chance) {
                        spawnCustomMob(level, vanillaMob);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    private static boolean isInsideFortress(ServerLevel level, BlockPos pos) {
        return level.structureManager().getStructureWithPieceAt(pos, BuiltinStructures.FORTRESS).isValid();
    }

    private static void spawnCustomMob(ServerLevel level, WitherSkeleton original) {
        Mob customMob = (Mob) ModEntities.CYBER_WITHER_SKELETON.get().create(level);
        if (customMob != null) {
            customMob.moveTo(original.getX(), original.getY(), original.getZ(), original.getYRot(), original.getXRot());
            customMob.finalizeSpawn(level, level.getCurrentDifficultyAt(original.blockPosition()), MobSpawnType.STRUCTURE, null, null);
            level.addFreshEntity(customMob);
        }
    }
}