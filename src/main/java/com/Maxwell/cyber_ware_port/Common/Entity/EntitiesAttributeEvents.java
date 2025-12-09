package com.Maxwell.cyber_ware_port.Common.Entity;

import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWither.CyberWitherBoss;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModEntities;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CyberWare.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntitiesAttributeEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {

        event.put(ModEntities.CYBER_ZOMBIE.get(), Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D) 
                .add(Attributes.MOVEMENT_SPEED, 0.35D) 
                .add(Attributes.ATTACK_DAMAGE, 6.0D) 
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .build());

        event.put(ModEntities.CYBER_SKELETON.get(), AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .build());
        event.put(ModEntities.CYBER_CREEPER.get(), Creeper.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D) 
                .build());
        event.put(ModEntities.CYBER_WITHER.get(), CyberWitherBoss.createAttributes().build());
        event.put(ModEntities.CYBER_WITHER_SKELETON.get(), AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .build());
    }

}
