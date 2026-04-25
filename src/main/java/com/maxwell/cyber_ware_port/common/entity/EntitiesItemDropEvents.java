package com.maxwell.cyber_ware_port.common.entity;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper.CyberCreeperEntity;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwither.CyberWitherBoss;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwitherskeleton.CyberWitherSkeletonEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = CyberWare.MODID)
public class EntitiesItemDropEvents {
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
        int looting = (attacker != null) ? EnchantmentHelper.getEnchantmentLevel(attacker.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING), attacker) : 0;
        if (entity instanceof CyberWitherBoss) {
            addScavengedDrop(event, ModItems.INTERNAL_DEFIBRILLATOR.get());
            List<Item> witherPool = generateMobDropPool(cyberMob);
            witherPool.remove(ModItems.INTERNAL_DEFIBRILLATOR.get());
            if (!witherPool.isEmpty()) {
                int dropCount = 5 + random.nextInt(4);
                for (int i = 0; i < dropCount; i++) {
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
        if (attacker != null && attacker.getMainHandItem().is(ModItems.KATANA.get())) {
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
        LivingEntity entity = (LivingEntity) cyberMob;
        var mobData = com.maxwell.cyber_ware_port.api.json.MobDataManager.MOB_DATA.get(entity.getType());
        boolean isHighTier = cyberMob.isHighTierMob();
        List<Item> specialDrops = new ArrayList<>(cyberMob.getSpecialDrops());
        List<Item> forbiddenDrops = new ArrayList<>(cyberMob.getForbiddenDrops());
        if (mobData != null) {
            isHighTier |= mobData.isHighTier;
            if (mobData.specialDrops != null) pool.addAll(mobData.specialDrops);
            if (mobData.forbiddenDrops != null) forbiddenDrops.addAll(mobData.forbiddenDrops);
        }
        if (isHighTier) {
            pool.addAll(CACHED_HIGH_TIER_POOL);
        }
        if (!specialDrops.isEmpty()) {
            pool.addAll(specialDrops);
            pool.addAll(specialDrops);
        }
        if (!forbiddenDrops.isEmpty()) {
            pool.removeAll(forbiddenDrops);
        }
        return pool;
    }

    private static void addScavengedDrop(LivingDropsEvent event, Item item) {
        ItemStack stack = new ItemStack(item);
        if (item instanceof CyberwareItem cw) {
            cw.setPristine(stack, false);
        }
        event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack));
    }

    private static void initDropPools() {
        CACHED_COMMON_POOL = new ArrayList<>();
        CACHED_HIGH_TIER_POOL = new ArrayList<>();
        List<Item> highTierItems = List.of(
                ModItems.INTERNAL_DEFIBRILLATOR.get(),
                ModItems.RAPID_FIRE_FLYWHEEL.get(),
                ModItems.LINEAR_ACTUATORS.get(),
                ModItems.CONSCIOUSNESS_TRANSMITTER.get(),
                ModItems.STEM_CELL_SYNTHESIZER.get()
        );
        for (DeferredHolder<Item, ? extends Item> entry : ModItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (item instanceof CyberwareItem) {
                Identifier id = BuiltInRegistries.ITEM.getKey(item);
                if (id.getPath().contains("body_part")) continue;
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
        if (event.getExplosion().getDirectSourceEntity() instanceof CyberCreeperEntity creeper) {
            if (creeper.isCausingCustomExplosion()) {
                return;
            }
            event.setCanceled(true);
            Level level = event.getLevel();
            if (level.isClientSide) {
                return;
            }
            boolean mobGriefing = level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
            Level.ExplosionInteraction interaction = mobGriefing
                    ? Level.ExplosionInteraction.BLOCK
                    : Level.ExplosionInteraction.NONE;
            creeper.setCausingCustomExplosion(true);
            float baseRadius = creeper.isPowered() ? 6.0F : 3.0F;
            float finalRadius = baseRadius + 1.0F;
            long time = level.getDayTime() % 24000;
            if (time >= 0 && time < 13000) {
                finalRadius *= 1.5F;
            }
            level.explode(
                    creeper,
                    null,
                    null,
                    creeper.getX(),
                    creeper.getY(),
                    creeper.getZ(),
                    finalRadius,
                    false,
                    interaction);
            creeper.discard();
        }
    }
}