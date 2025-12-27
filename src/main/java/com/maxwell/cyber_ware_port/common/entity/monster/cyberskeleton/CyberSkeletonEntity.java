package com.maxwell.cyber_ware_port.common.entity.monster.cyberskeleton;

import com.maxwell.cyber_ware_port.common.entity.ICyberwareMob;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("removal")
public class CyberSkeletonEntity extends Skeleton implements ICyberwareMob {

    private static final int MELEE_TRIGGER_DIST_SQR = 5 * 5;
    private static final int MELEE_COOLDOWN_TICKS = 100;
    private static final int MAX_MELEE_ATTACKS = 5;
    public int meleeCooldown = 0;
    public int attackCounter = 0;

    public CyberSkeletonEntity(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
    }

    @Override
    public List<Item> getSpecialDrops() {
        return Arrays.asList(
                ModItems.RAPID_FIRE_FLYWHEEL.get(),
                ModItems.LINEAR_ACTUATORS.get()
        );
    }

    @Override
    public List<Item> getForbiddenDrops() {
        return Arrays.asList(
                ModItems.CYBER_ARM_LEFT.get(),
                ModItems.CYBER_ARM_RIGHT.get(),
                ModItems.CYBER_LEG_LEFT.get(),
                ModItems.CYBER_LEG_RIGHT.get(),
                ModItems.INTERNAL_DEFIBRILLATOR.get()
        );
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new CyberMeleeGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new CyberBowAttackGoal(this, 1.0D, 20, 15.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void reassessWeaponGoal() {
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.meleeCooldown > 0) {
            this.meleeCooldown--;
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack arrowStack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem)));
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - arrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        arrow.shoot(d0, d1 + d3 * 0.2D, d2, 2.5F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
    }

    static class CyberBowAttackGoal extends RangedBowAttackGoal<CyberSkeletonEntity> {

        private final CyberSkeletonEntity skeleton;

        public CyberBowAttackGoal(CyberSkeletonEntity mob, double speedModifier, int attackIntervalMin, float attackRadius) {
            super(mob, speedModifier, attackIntervalMin, attackRadius);
            this.skeleton = mob;
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }
            if (this.skeleton.getTarget() == null || this.skeleton.meleeCooldown > 0) {
                return false;
            }
            double distSqr = this.skeleton.distanceToSqr(this.skeleton.getTarget());
            return distSqr > MELEE_TRIGGER_DIST_SQR;
        }
    }

    static class CyberMeleeGoal extends MeleeAttackGoal {

        private final CyberSkeletonEntity skeleton;

        public CyberMeleeGoal(CyberSkeletonEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
            this.skeleton = mob;
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }
            if (this.skeleton.getTarget() == null || this.skeleton.meleeCooldown > 0) {
                return false;
            }
            double distSqr = this.skeleton.distanceToSqr(this.skeleton.getTarget());
            return distSqr <= MELEE_TRIGGER_DIST_SQR;
        }

        @Override
        public void stop() {
            super.stop();
            this.skeleton.meleeCooldown = MELEE_COOLDOWN_TICKS;
        }
    }
}