package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class CranialBroadcasterItem extends CyberwareItem {
    private static final List<Supplier<? extends EntityType<? extends Monster>>> CYBER_MOBS = Arrays.asList(ModEntities.CYBER_ZOMBIE, ModEntities.CYBER_SKELETON, ModEntities.CYBER_CREEPER);

    public CranialBroadcasterItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)
                .energy(2, 0, 0, StackingRule.STATIC));
    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        return isActive(stack) ? 2 : 0;

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer.level().getGameTime() % 20 != 0) return;
        if (!(wearer instanceof Player player)) return;
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (player.getRandom().nextInt(20) == 0) {
                int spawnCost = 200;
                if (data.extractEnergy(spawnCost, false) == spawnCost) {
                    spawnRandomCyberMob(player);
                }
            }
        });
    }

    private void spawnRandomCyberMob(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        double angle = player.getRandom().nextDouble() * Math.PI * 2;
        double distance = 20 + player.getRandom().nextDouble() * 20;
        int x = Mth.floor(player.getX() + Math.cos(angle) * distance);
        int z = Mth.floor(player.getZ() + Math.sin(angle) * distance);
        int y = serverLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos spawnPos = new BlockPos(x, y, z);
        EntityType<? extends Monster> mobType = CYBER_MOBS.get(player.getRandom().nextInt(CYBER_MOBS.size())).get();
        if (NaturalSpawner.isSpawnPositionOk(net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND, serverLevel, spawnPos, mobType)) {
            Monster mob = mobType.create(serverLevel);
            if (mob != null) {
                mob.moveTo(x + 0.5, y, z + 0.5, player.getRandom().nextFloat() * 360F, 0.0F);
                if (mob.checkSpawnRules(serverLevel, MobSpawnType.EVENT)) {
                    serverLevel.addFreshEntity(mob);
                }
            }
        }
    }
}