package com.maxwell.cyber_ware_port.api.event;

import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class CyberwareAbilityEvent extends Event {
    private final LivingEntity entity;
    private final ItemStack stack;
    private final ICyberware cyberware;

    public CyberwareAbilityEvent(LivingEntity entity, ItemStack stack, ICyberware cyberware) {
        this.entity = entity;
        this.stack = stack;
        this.cyberware = cyberware;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public ItemStack getStack() {
        return stack;
    }

    public ICyberware getCyberware() {
        return cyberware;
    }
}