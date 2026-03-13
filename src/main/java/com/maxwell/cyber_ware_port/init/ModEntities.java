package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper.CyberCreeperEntity;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberskeleton.CyberSkeletonEntity;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwither.CyberWitherBoss;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwitherskeleton.CyberWitherSkeletonEntity;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberzombie.CyberZombieEntity;
import net.minecraft.core.registries.Registries;
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
                    () -> EntityType.Builder.of(CyberZombieEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F)
                            .build("cyber_zombie"));
    public static final DeferredHolder<EntityType<?>, EntityType<CyberSkeletonEntity>> CYBER_SKELETON =
            ENTITIES.register("cyber_skeleton",
                    () -> EntityType.Builder.of(CyberSkeletonEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.99F)
                            .build("cyber_skeleton"));
    public static final DeferredHolder<EntityType<?>, EntityType<CyberWitherSkeletonEntity>> CYBER_WITHER_SKELETON =
            ENTITIES.register("cyber_wither_skeleton",
                    () -> EntityType.Builder.of(CyberWitherSkeletonEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.4F)
                            .build("cyber_wither_skeleton"));
    public static final DeferredHolder<EntityType<?>, EntityType<CyberCreeperEntity>> CYBER_CREEPER =
            ENTITIES.register("cyber_creeper",
                    () -> EntityType.Builder.of(CyberCreeperEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 1.4F)
                            .build("cyber_creeper"));
    public static final DeferredHolder<EntityType<?>, EntityType<CyberWitherBoss>> CYBER_WITHER =
            ENTITIES.register("cyber_wither",
                    () -> EntityType.Builder.of(CyberWitherBoss::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.4F)
                            .build("cyber_wither"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}