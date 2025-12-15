package com.Maxwell.cyber_ware_port.Common.Entity;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobSpawnerEvents {

    private static final int SPAWN_COOLDOWN = 600;
    private static long lastSpawnTime = 0;
    private static final Random RANDOM = new Random();
    private static final List<Supplier<? extends EntityType<?>>> OVERWORLD_MOBS = List.of(
            ModEntities.CYBER_ZOMBIE,
            ModEntities.CYBER_SKELETON,
            ModEntities.CYBER_CREEPER
    );

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var server = event.getServer();
        long currentTime = server.getTickCount();
        if (currentTime < lastSpawnTime + SPAWN_COOLDOWN) return;
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null || !overworld.isNight()) return;
        if (overworld.players().isEmpty()) return;
        ServerPlayer player = overworld.players().get(RANDOM.nextInt(overworld.players().size()));
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
            if (player.distanceToSqr(x, y, z) > 24 * 24 && level.noCollision(mobToSpawn.getAABB(x, y, z))) {
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
}