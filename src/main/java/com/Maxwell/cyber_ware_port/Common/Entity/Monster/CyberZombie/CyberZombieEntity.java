package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberZombie;


import com.Maxwell.cyber_ware_port.Common.Block.Radio.TemptToBlockGoal;

import com.Maxwell.cyber_ware_port.Common.Entity.ICyberwareMob;

import com.Maxwell.cyber_ware_port.Init.ModBlocks;

import com.Maxwell.cyber_ware_port.Init.ModItems;

import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.monster.Zombie;

import net.minecraft.world.item.Item;

import net.minecraft.world.level.Level;


import java.util.Arrays;

import java.util.List;


public class CyberZombieEntity extends Zombie implements ICyberwareMob {
    public CyberZombieEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);


        this.setMaxUpStep(2.0F);

    }
    @Override
    public List<Item> getSpecialDrops() {

        return Arrays.asList(
                ModItems.CYBER_ARM_LEFT.get(),
                ModItems.CYBER_ARM_RIGHT.get(),
                ModItems.CYBER_LEG_LEFT.get(),
                ModItems.CYBER_LEG_RIGHT.get(),
                ModItems.REINFORCED_FIST.get(),
                ModItems.DEPLOYABLE_WHEELS.get()
        );

    }

    @Override
    public List<Item> getForbiddenDrops() {

        return Arrays.asList(
                ModItems.RAPID_FIRE_FLYWHEEL.get(),
                ModItems.LINEAR_ACTUATORS.get(),
                ModItems.INTERNAL_DEFIBRILLATOR.get()
        );

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(3, new TemptToBlockGoal(this, 1.0D, ModBlocks.RADIO_KIT_BLOCK.get(), 64));

    }

    @Override
    protected boolean isSunBurnTick() {
        return false;

    }
}