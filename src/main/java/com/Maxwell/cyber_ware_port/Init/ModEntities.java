package com.Maxwell.cyber_ware_port.Init;

import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberCreeper.CyberCreeperEntity;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberSkeleton.CyberSkeletonEntity;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWither.CyberWitherBoss;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWitherSkeleton.CyberWitherSkeletonEntity;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberZombie.CyberZombieEntity;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("removal")
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CyberWare.MODID);
    public static final RegistryObject<EntityType<CyberZombieEntity>> CYBER_ZOMBIE = ENTITIES.register("cyber_zombie",
            () -> EntityType.Builder.of(CyberZombieEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build(new ResourceLocation(CyberWare.MODID, "cyber_zombie").toString()));
    public static final RegistryObject<EntityType<CyberSkeletonEntity>> CYBER_SKELETON = ENTITIES.register("cyber_skeleton",
            () -> EntityType.Builder.of(CyberSkeletonEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).build(new ResourceLocation(CyberWare.MODID, "cyber_skeleton").toString()));
    public static final RegistryObject<EntityType<CyberWitherSkeletonEntity>> CYBER_WITHER_SKELETON = ENTITIES.register("cyber_wither_skeleton",
            () -> EntityType.Builder.of(CyberWitherSkeletonEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 2.4F).build(new ResourceLocation(CyberWare.MODID, "cyber_wither_skeleton").toString()));
    public static final RegistryObject<EntityType<CyberCreeperEntity>> CYBER_CREEPER = ENTITIES.register("cyber_creeper",
            () -> EntityType.Builder.of(CyberCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 1.4F).build(new ResourceLocation(CyberWare.MODID, "cyber_creeper").toString()));
    public static final RegistryObject<EntityType<CyberWitherBoss>> CYBER_WITHER = ENTITIES.register("cyber_wither",
            () -> EntityType.Builder.of(CyberWitherBoss::new, MobCategory.MONSTER)
                    .sized(0.7F, 2.4F).build(new ResourceLocation(CyberWare.MODID, "cyber_wither").toString()));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}