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

    /**
     * 手術開始前に発火するイベント。
     * キャンセルすると手術は実行されません。
     */
    @Cancelable
    public static class Pre extends CyberwareSurgeryEvent {
        private Component denialReason;

        public Pre(LivingEntity patient, RobosurgeonBlockEntity blockEntity) {
            super(patient, blockEntity);
        }

        /**
         * 手術を拒否する理由を設定します（プレイヤーへのチャット通知などに使用可能）
         */
        public void setDenialReason(Component reason) {
            this.denialReason = reason;
        }

        public Component getDenialReason() {
            return denialReason;
        }
    }

    /**
     * 手術完了後に発火するイベント。
     * キャンセル不可。実績解除や追加効果の付与などに使用。
     */
    public static class Post extends CyberwareSurgeryEvent {
        public Post(LivingEntity patient, RobosurgeonBlockEntity blockEntity) {
            super(patient, blockEntity);
        }
    }
}