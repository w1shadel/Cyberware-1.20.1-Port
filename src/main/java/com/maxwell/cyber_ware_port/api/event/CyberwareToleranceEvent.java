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

    /**
     * Config設定または保存された本来の許容値を取得します。
     */
    public int getOriginalTolerance() {
        return originalTolerance;
    }

    /**
     * イベント処理後の最終的な許容値を取得します。
     */
    public int getNewTolerance() {
        return newTolerance;
    }

    /**
     * 新しい許容値を設定します。
     * アドオンやPotion効果などで許容値を変更する場合に使用します。
     */
    public void setNewTolerance(int newTolerance) {
        this.newTolerance = newTolerance;
    }
}