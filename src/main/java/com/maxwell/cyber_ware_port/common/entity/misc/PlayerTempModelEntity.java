package com.maxwell.cyber_ware_port.common.entity.misc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class PlayerTempModelEntity extends Mob {
    public PlayerTempModelEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }
    @Override
    public void tick() {
        super.tick();
        this.remove(RemovalReason.DISCARDED);
    }
}
