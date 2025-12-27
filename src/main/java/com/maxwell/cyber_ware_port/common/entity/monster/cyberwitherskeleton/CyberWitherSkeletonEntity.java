package com.maxwell.cyber_ware_port.common.entity.monster.cyberwitherskeleton;

import com.maxwell.cyber_ware_port.common.entity.ICyberwareMob;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberskeleton.CyberSkeletonEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CyberWitherSkeletonEntity extends CyberSkeletonEntity implements ICyberwareMob {

    public CyberWitherSkeletonEntity(EntityType<? extends net.minecraft.world.entity.monster.Skeleton> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }
}