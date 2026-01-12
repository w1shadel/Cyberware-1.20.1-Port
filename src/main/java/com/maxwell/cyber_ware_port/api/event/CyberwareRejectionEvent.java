package com.maxwell.cyber_ware_port.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class CyberwareRejectionEvent extends Event {
    private final LivingEntity entity;
    private final int currentTolerance;

    public CyberwareRejectionEvent(LivingEntity entity, int currentTolerance) {
        this.entity = entity;
        this.currentTolerance = currentTolerance;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public int getCurrentTolerance() {
        return currentTolerance;
    }
}