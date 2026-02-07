package com.maxwell.cyber_ware_port.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class CyberwareToleranceEvent extends Event {
    private final LivingEntity entity;
    private final int originalTolerance;
    private int newTolerance;

    public CyberwareToleranceEvent(LivingEntity entity, int originalTolerance) {
        this.entity = entity;
        this.originalTolerance = originalTolerance;
        this.newTolerance = originalTolerance;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    
    public int getOriginalTolerance() {
        return originalTolerance;
    }

    
    public int getNewTolerance() {
        return newTolerance;
    }

    
    public void setNewTolerance(int newTolerance) {
        this.newTolerance = newTolerance;
    }
}