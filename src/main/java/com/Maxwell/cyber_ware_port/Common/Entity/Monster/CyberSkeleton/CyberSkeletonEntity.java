package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberSkeleton;

import com.Maxwell.cyber_ware_port.Common.Block.Radio.TemptToBlockGoal;
import com.Maxwell.cyber_ware_port.Common.Entity.ICyberwareMob;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("removal")
public class CyberSkeletonEntity extends Skeleton implements ICyberwareMob {

    private static final int MELEE_TRIGGER_DIST_SQR = 5 * 5;

    private static final int MELEE_EXIT_DIST_SQR = 12 * 12;

    private static final int MAX_MELEE_ATTACKS = 9;

    private static final int MELEE_COOLDOWN_TICKS = 100;
    private int meleeCooldown = 0;
    private int attackCounter = 0;
    private boolean isMeleeMode = false;
    private boolean isBackstepping = false;
    private RangedBowAttackGoal<CyberSkeletonEntity> bowGoal;
    private CyberMeleeGoal meleeGoal;
    private CyberBackstepGoal backstepGoal;

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
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 10, 15.0F);
        this.meleeGoal = new CyberMeleeGoal(this, 1.2D, false);
        this.backstepGoal = new CyberBackstepGoal(this, 1.0D);
        this.goalSelector.addGoal(3, new TemptToBlockGoal(this, 1.0D, ModBlocks.RADIO_KIT_BLOCK.get(), 64));
        this.goalSelector.addGoal(4, this.backstepGoal);
        this.goalSelector.addGoal(5, this.meleeGoal);
        this.goalSelector.addGoal(6, this.bowGoal);
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
        if (this.level().isClientSide) return;
        if (this.meleeCooldown > 0) {
            this.meleeCooldown--;

        }
        LivingEntity target = this.getTarget();
        if (target == null || isBackstepping) return;
        double distSqr = this.distanceToSqr(target);
        if (!isMeleeMode && meleeCooldown == 0 && distSqr <= MELEE_TRIGGER_DIST_SQR) {
            startMeleeMode();

        }
        if (isMeleeMode) {
            if (distSqr > MELEE_EXIT_DIST_SQR || attackCounter >= MAX_MELEE_ATTACKS || !this.hasLineOfSight(target)) {
                endMeleeMode();

            }
        }
    }

    private void startMeleeMode() {
        this.isMeleeMode = true;
        this.attackCounter = 0;
        this.setAggressive(true);

    }

    public void endMeleeMode() {
        this.isMeleeMode = false;
        this.meleeCooldown = MELEE_COOLDOWN_TICKS;
        this.isBackstepping = true;
        this.setAggressive(true);

    }

    public void incrementAttackCounter() {
        this.attackCounter++;

    }

    public boolean isMeleeMode() {
        return isMeleeMode;
    }

    public boolean isBackstepping() {
        return isBackstepping;
    }

    public void setBackstepping(boolean backstepping) {
        this.isBackstepping = backstepping;
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

    class CyberMeleeGoal extends MeleeAttackGoal {
        public CyberMeleeGoal(CyberSkeletonEntity mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);

        }

        @Override
        public boolean canUse() {
            return super.canUse() && ((CyberSkeletonEntity) this.mob).isMeleeMode();

        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && ((CyberSkeletonEntity) this.mob).isMeleeMode();

        }

        @Override
        protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
            double d0 = this.getAttackReachSqr(pEnemy);
            if (pDistToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.OFF_HAND);
                this.mob.doHurtTarget(pEnemy);
                ((CyberSkeletonEntity) this.mob).incrementAttackCounter();

            }
        }
    }

    class CyberBackstepGoal extends Goal {
        private final CyberSkeletonEntity mob;

        private final double speedModifier;

        private int timer;

        private double targetX, targetY, targetZ;

        public CyberBackstepGoal(CyberSkeletonEntity mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Flag.MOVE));

        }

        @Override
        public boolean canUse() {
            return this.mob.isBackstepping() && this.mob.getTarget() != null;

        }

        @Override
        public void start() {
            this.timer = 40;
            LivingEntity target = this.mob.getTarget();
            if (target != null) {
                Vec3 direction = this.mob.position().subtract(target.position()).normalize();
                Vec3 targetPos = this.mob.position().add(direction.scale(5.0));
                this.targetX = targetPos.x;
                this.targetY = targetPos.y;
                this.targetZ = targetPos.z;
                this.mob.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speedModifier * 1.5);

            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.timer > 0 && this.mob.isBackstepping();

        }

        @Override
        public void tick() {
            this.timer--;
            if (this.mob.getTarget() != null) {
                this.mob.getLookControl().setLookAt(this.mob.getTarget(), 30.0F, 30.0F);

            }
        }

        @Override
        public void stop() {
            this.mob.setBackstepping(false);
            this.mob.getNavigation().stop();

        }
    }
}