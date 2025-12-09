package com.Maxwell.cyber_ware_port.Common.Entity;


import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberCreeper.CyberCreeperEntity;

import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWither.CyberWitherBoss;

import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWitherSkeleton.CyberWitherSkeletonEntity;

import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;

import com.Maxwell.cyber_ware_port.CyberWare;

import com.Maxwell.cyber_ware_port.Init.ModEntities;

import com.Maxwell.cyber_ware_port.Init.ModItems;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.util.RandomSource;

import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.SpawnPlacements;

import net.minecraft.world.entity.item.ItemEntity;

import net.minecraft.world.entity.monster.Monster;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.enchantment.EnchantmentHelper;

import net.minecraft.world.item.enchantment.Enchantments;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.levelgen.Heightmap;

import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;

import net.minecraftforge.event.entity.living.LivingDropsEvent;

import net.minecraftforge.event.level.ExplosionEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraftforge.registries.RegistryObject;


import java.util.ArrayList;

import java.util.List;

@Mod.EventBusSubscriber(modid = CyberWare.MODID)
public class CommonEvents {

    private static List<Item> CACHED_COMMON_POOL = null;

    private static List<Item> CACHED_HIGH_TIER_POOL = null;


    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof ICyberwareMob cyberMob)) {
            return;

        }
        if (CACHED_COMMON_POOL == null) {
            initDropPools();

        }

        RandomSource random = entity.getRandom();


        LivingEntity attacker = null;

        if (event.getSource().getEntity() instanceof LivingEntity livingAttacker) {
            attacker = livingAttacker;

        }
        int looting = (attacker != null) ? EnchantmentHelper.getEnchantmentLevel(Enchantments.MOB_LOOTING, attacker) : 0;

        if (entity instanceof CyberWitherBoss) {
            addScavengedDrop(event, ModItems.INTERNAL_DEFIBRILLATOR.get());

            List<Item> witherPool = generateMobDropPool(cyberMob);

            witherPool.remove(ModItems.INTERNAL_DEFIBRILLATOR.get());

            if (!witherPool.isEmpty()) {
                int dropCount = 5 + random.nextInt(4);

                for (int i = 0;
 i < dropCount;
 i++) {
                    Item randomItem = witherPool.get(random.nextInt(witherPool.size()));

                    addScavengedDrop(event, randomItem);

                }
            }
            return;

        }
        List<Item> pool = generateMobDropPool(cyberMob);

        if (pool.isEmpty()) return;

        float dropChance = 0.25f + (looting * 0.05f);

        if (random.nextFloat() < dropChance) {
            Item selectedItem = pool.get(random.nextInt(pool.size()));

            addScavengedDrop(event, selectedItem);

        }
        if (attacker != null && attacker.getMainHandItem().getItem() == ModItems.KATANA.get()) {

            float katanaChance = 0.50f + (looting * 0.10f);


            if (random.nextFloat() < katanaChance) {
                Item katanaDrop = pool.get(random.nextInt(pool.size()));

                addScavengedDrop(event, katanaDrop);

            }
        }
        if (entity instanceof CyberWitherSkeletonEntity) {
            if (random.nextFloat() < 0.05f + (looting * 0.02f)) {
                addScavengedDrop(event, ModItems.CYBER_WITHER_SKELETON_SKULL_ITEM.get());

            }
        }
    }
    private static List<Item> generateMobDropPool(ICyberwareMob cyberMob) {
        List<Item> pool = new ArrayList<>();


        pool.addAll(CACHED_COMMON_POOL);


        if (cyberMob.isHighTierMob()) {
            pool.addAll(CACHED_HIGH_TIER_POOL);

        }

        List<Item> specialDrops = cyberMob.getSpecialDrops();

        if (specialDrops != null && !specialDrops.isEmpty()) {
            pool.addAll(specialDrops);

            pool.addAll(specialDrops);

        }

        List<Item> forbiddenDrops = cyberMob.getForbiddenDrops();

        if (forbiddenDrops != null && !forbiddenDrops.isEmpty()) {
            pool.removeAll(forbiddenDrops);

        }

        return pool;

    }

    private static void addScavengedDrop(LivingDropsEvent event, Item item) {
        addScavengedDrop(event, new ItemStack(item));

    }

    private static void addScavengedDrop(LivingDropsEvent event, ItemStack stack) {
        if (stack.getItem() instanceof CyberwareItem cw) {
            cw.setPristine(stack, false);
 
        }
        event.getDrops().add(new ItemEntity(event.getEntity().level(),
                event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack));

    }

    private static void initDropPools() {
        CACHED_COMMON_POOL = new ArrayList<>();

        CACHED_HIGH_TIER_POOL = new ArrayList<>();


        List<Item> highTierItems = new ArrayList<>();

        highTierItems.add(ModItems.INTERNAL_DEFIBRILLATOR.get());

        highTierItems.add(ModItems.RAPID_FIRE_FLYWHEEL.get());

        highTierItems.add(ModItems.LINEAR_ACTUATORS.get());

        highTierItems.add(ModItems.CONSCIOUSNESS_TRANSMITTER.get());

        highTierItems.add(ModItems.STEM_CELL_SYNTHESIZER.get());
for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
            Item item = entry.get();

            if (item instanceof CyberwareItem) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);


                if (id != null && id.getPath().contains("body_part")) continue;

                if (item == ModItems.CREATIVE_BATTERY.get()) continue;


                if (highTierItems.contains(item)) {
                    CACHED_HIGH_TIER_POOL.add(item);

                } else {
                    CACHED_COMMON_POOL.add(item);

                }
            }
        }
    }
    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {

        if (event.getExplosion().getExploder() instanceof CyberCreeperEntity creeper) {

            if (creeper.isCausingCustomExplosion()) {
                return;

            }

            event.setCanceled(true);


            Level level = event.getLevel();

            if (level.isClientSide) return;


            creeper.setCausingCustomExplosion(true);


            float baseRadius = creeper.isPowered() ? 6.0F : 3.0F;

            float finalRadius = baseRadius + 1.0F;


            long time = level.getDayTime() % 24000;

            if (time >= 0 && time < 13000) {
                finalRadius *= 1.5F;

            }level.explode(
                    creeper,
                    creeper.getX(),
                    creeper.getY(),
                    creeper.getZ(),
                    finalRadius,
                    true,
                    Level.ExplosionInteraction.BLOCK
            );


            creeper.discard();

        }
    }
    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {

        event.register(
                ModEntities.CYBER_ZOMBIE.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );


        event.register(
                ModEntities.CYBER_SKELETON.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );


        event.register(
                ModEntities.CYBER_CREEPER.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );


        event.register(
                ModEntities.CYBER_WITHER_SKELETON.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );

    }
}