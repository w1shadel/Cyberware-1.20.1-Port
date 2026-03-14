package com.maxwell.cyber_ware_port.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class CyberwareRejectionEvent extends Event implements ICancellableEvent {
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