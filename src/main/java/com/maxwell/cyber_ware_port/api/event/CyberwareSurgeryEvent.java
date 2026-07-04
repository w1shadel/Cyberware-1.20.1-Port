package com.maxwell.cyber_ware_port.api.event;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class CyberwareSurgeryEvent extends Event {
    private final LivingEntity patient;
    private final RobosurgeonBlockEntity blockEntity;

    public CyberwareSurgeryEvent(LivingEntity patient, RobosurgeonBlockEntity blockEntity) {
        this.patient = patient;
        this.blockEntity = blockEntity;
    }

    public LivingEntity getPatient() {
        return patient;
    }

    public RobosurgeonBlockEntity getRobosurgeon() {
        return blockEntity;
    }

    @Cancelable
    public static class Pre extends CyberwareSurgeryEvent {
        private Component denialReason;

        public Pre(LivingEntity patient, RobosurgeonBlockEntity blockEntity) {
            super(patient, blockEntity);
        }

        public Component getDenialReason() {
            return denialReason;
        }

        public void setDenialReason(Component reason) {
            this.denialReason = reason;
        }
    }

    public static class Post extends CyberwareSurgeryEvent {
        public Post(LivingEntity patient, RobosurgeonBlockEntity blockEntity) {
            super(patient, blockEntity);
        }
    }
}