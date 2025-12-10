package com.Maxwell.cyber_ware_port.Common.Block.Radio;import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.EnumSet;public class TemptToBlockGoal extends Goal {

    private final PathfinderMob mob;

    private final double speedModifier;

    private final Block targetBlock;

    private final int searchRange;

    private BlockPos targetPos;

    private int tickDelay = 0;public TemptToBlockGoal(PathfinderMob pMob, double pSpeedModifier, Block pTargetBlock, int pSearchRange) {
        this.mob = pMob;

        this.speedModifier = pSpeedModifier;

        this.targetBlock = pTargetBlock;

        this.searchRange = pSearchRange;

        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));

    }

    @Override
    public boolean canUse() {

        if (this.tickDelay > 0) {
            --this.tickDelay;

            return false;

        }this.tickDelay = 40 + this.mob.getRandom().nextInt(40);if (this.mob.getNavigation().isInProgress()) {
            return false;

        }

        this.targetPos = findNearestPoweredBlock();

        return this.targetPos != null;

    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetPos.getX() + 0.5, this.targetPos.getY(), this.targetPos.getZ() + 0.5, this.speedModifier);

    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.getNavigation().isDone() || this.mob.distanceToSqr(this.targetPos.getX() + 0.5, this.targetPos.getY(), this.targetPos.getZ() + 0.5) < 4.0) {
            return false;

        }
        BlockState state = this.mob.level().getBlockState(this.targetPos);

        return state.is(this.targetBlock) && state.getValue(RadioKitBlock.POWERED);

    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();

        this.targetPos = null;

    }

    @Nullable
    private BlockPos findNearestPoweredBlock() {

        return BlockPos.findClosestMatch(this.mob.blockPosition(), this.searchRange, this.searchRange, (pos) -> {
            BlockState state = this.mob.level().getBlockState(pos);

            return state.is(this.targetBlock) && state.getValue(RadioKitBlock.POWERED);

        }).orElse(null);

    }
}