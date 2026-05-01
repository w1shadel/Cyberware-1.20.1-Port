package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.entity.misc.PlayerTempModelEntity;
import com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper.CyberCreeperEntity;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberskeleton.CyberSkeletonEntity;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwither.CyberWitherBoss;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwitherskeleton.CyberWitherSkeletonEntity;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberzombie.CyberZombieEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, CyberWare.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<CyberZombieEntity>> CYBER_ZOMBIE =
            ENTITIES.register("cyber_zombie",
                    (key) -> EntityType.Builder.of(CyberZombieEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, key)));
    public static final DeferredHolder<EntityType<?>, EntityType<CyberSkeletonEntity>> CYBER_SKELETON =
            ENTITIES.register("cyber_skeleton",
                    (key) -> EntityType.Builder.of(CyberSkeletonEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.99F)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, key)));
    public static final DeferredHolder<EntityType<?>, EntityType<CyberWitherSkeletonEntity>> CYBER_WITHER_SKELETON =
            ENTITIES.register("cyber_wither_skeleton",
                    (key) -> EntityType.Builder.of(CyberWitherSkeletonEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.4F)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, key)));
    public static final DeferredHolder<EntityType<?>, EntityType<CyberCreeperEntity>> CYBER_CREEPER =
            ENTITIES.register("cyber_creeper",
                    (key) -> EntityType.Builder.of(CyberCreeperEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 1.4F)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, key)));
    public static final DeferredHolder<EntityType<?>, EntityType<CyberWitherBoss>> CYBER_WITHER =
            ENTITIES.register("cyber_wither",
                    (key) -> EntityType.Builder.of(CyberWitherBoss::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.4F)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, key)));
    public static final DeferredHolder<EntityType<?>, EntityType<PlayerTempModelEntity>> PLAYER_INTERNAL_PARTS =
            ENTITIES.register("player_temp_model",
                    (key) -> EntityType.Builder.of(PlayerTempModelEntity::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, key)));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}