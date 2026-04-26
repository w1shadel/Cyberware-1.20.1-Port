package com.maxwell.cyber_ware_port.common.entity.monster.cyberzombie;

import com.maxwell.cyber_ware_port.common.entity.ICyberwareMob;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CyberZombieEntity extends Zombie implements ICyberwareMob {
    public CyberZombieEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        Objects.requireNonNull(this.getAttribute(Attributes.STEP_HEIGHT)).setBaseValue(2.0F);
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

    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public List<Item> getForbiddenDrops() {
        return Arrays.asList(
                ModItems.RAPID_FIRE_FLYWHEEL.get(),
                ModItems.LINEAR_ACTUATORS.get(),
                ModItems.INTERNAL_DEFIBRILLATOR.get()
        );
    }

}