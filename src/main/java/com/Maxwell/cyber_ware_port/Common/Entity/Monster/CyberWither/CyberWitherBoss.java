package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWither;


import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;

import com.Maxwell.cyber_ware_port.Common.Entity.ICyberwareMob;

import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWitherSkeleton.CyberWitherSkeletonEntity;

import com.Maxwell.cyber_ware_port.Init.ModEntities;

import com.Maxwell.cyber_ware_port.Init.ModItems;

import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.chat.Component;

import net.minecraft.network.syncher.EntityDataAccessor;

import net.minecraft.network.syncher.EntityDataSerializers;

import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.server.level.ServerBossEvent;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.sounds.SoundEvent;

import net.minecraft.sounds.SoundEvents;

import net.minecraft.tags.DamageTypeTags;

import net.minecraft.util.Mth;

import net.minecraft.world.BossEvent;

import net.minecraft.world.Difficulty;

import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.effect.MobEffectInstance;

import net.minecraft.world.effect.MobEffects;

import net.minecraft.world.entity.*;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraft.world.entity.ai.control.FlyingMoveControl;

import net.minecraft.world.entity.ai.goal.*;

import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;

import net.minecraft.world.entity.ai.navigation.PathNavigation;

import net.minecraft.world.entity.monster.Monster;

import net.minecraft.world.entity.monster.RangedAttackMob;

import net.minecraft.world.entity.player.Player;

import net.minecraft.world.entity.projectile.WitherSkull;

import net.minecraft.world.item.Item;

import net.minecraft.world.level.Level;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;


import java.util.EnumSet;

import java.util.List;

import java.util.function.Predicate;


public class CyberWitherBoss extends Monster implements PowerableMob, RangedAttackMob, ICyberwareMob {
    @Override
    public List<Item> getForbiddenDrops() {
        return java.util.Arrays.asList(

                ModItems.CYBER_ARM_LEFT.get(), ModItems.CYBER_ARM_RIGHT.get(),
                ModItems.CYBER_LEG_LEFT.get(), ModItems.CYBER_LEG_RIGHT.get(),

                ModItems.RETRACTABLE_CLAWS.get(),
                ModItems.REINFORCED_FIST.get(),
                ModItems.FINE_MANIPULATORS.get(),

                ModItems.RAPID_FIRE_FLYWHEEL.get(),

                ModItems.LINEAR_ACTUATORS.get(),
                ModItems.FALL_BRACERS.get(),

                ModItems.AQUATIC_PROPULSION.get(),
                ModItems.DEPLOYABLE_WHEELS.get(),
                ModItems.IMPLANTED_SPURS.get()
        );

    }

    @Override
    public boolean isHighTierMob() {
        return true;

    }
    private static final EntityDataAccessor<Integer> DATA_TARGET_A = SynchedEntityData.defineId(CyberWitherBoss.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_TARGET_B = SynchedEntityData.defineId(CyberWitherBoss.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_TARGET_C = SynchedEntityData.defineId(CyberWitherBoss.class, EntityDataSerializers.INT);

    private static final List<EntityDataAccessor<Integer>> DATA_TARGETS = List.of(DATA_TARGET_A, DATA_TARGET_B, DATA_TARGET_C);

    private static final EntityDataAccessor<Integer> DATA_ID_INV = SynchedEntityData.defineId(CyberWitherBoss.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_MINION_1 = SynchedEntityData.defineId(CyberWitherBoss.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_MINION_2 = SynchedEntityData.defineId(CyberWitherBoss.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_MINION_3 = SynchedEntityData.defineId(CyberWitherBoss.class, EntityDataSerializers.INT);

    private boolean hasSummonedMinions = false;
 
    private int shieldTimer = 0;
                
    private final float[] xRotHeads = new float[2];

    private final float[] yRotHeads = new float[2];

    private final float[] xRotOHeads = new float[2];

    private final float[] yRotOHeads = new float[2];


    private final ServerBossEvent bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);


    private int empCooldown = 200;

    private boolean hasExplodedAtHalfHealth = false;


    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (entity) -> {
        return entity.getMobType() != MobType.UNDEAD && entity.attackable();

    };


    public CyberWitherBoss(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.moveControl = new FlyingMoveControl(this, 10, false);

        this.setHealth(this.getMaxHealth());

        this.xpReward = 100;

    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, pLevel);

        nav.setCanOpenDoors(false);

        nav.setCanFloat(true);

        nav.setCanPassDoors(true);

        return nav;

    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(0, new CyberWitherDoNothingGoal());

        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 40, 20.0F));

        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 800.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.6D)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_TARGET_A, 0);

        this.entityData.define(DATA_TARGET_B, 0);

        this.entityData.define(DATA_TARGET_C, 0);

        this.entityData.define(DATA_ID_INV, 0);

        this.entityData.define(DATA_MINION_1, -1);

        this.entityData.define(DATA_MINION_2, -1);

        this.entityData.define(DATA_MINION_3, -1);

    }

    @Override
    public void aiStep() {
        Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);

        if (!this.level().isClientSide && this.getAlternativeTarget(0) > 0) {
            Entity entity = this.level().getEntity(this.getAlternativeTarget(0));

            if (entity != null) {
                double d0 = vec3.y;
if (this.getY() < entity.getY() || !this.isPowered() && this.getY() < entity.getY() + 2.0D) {
                    d0 = Math.max(0.0D, d0);

                    d0 += 0.3D - d0 * 0.6F;

                }

                vec3 = new Vec3(vec3.x, d0, vec3.z);

                Vec3 vec31 = new Vec3(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());

                if (vec31.horizontalDistanceSqr() > 9.0D) {
                    Vec3 vec32 = vec31.normalize();

                    vec3 = vec3.add(vec32.x * 0.3D - vec3.x * 0.6D, 0.0D, vec32.z * 0.3D - vec3.z * 0.6D);

                }
            }
        }
        this.setDeltaMovement(vec3);

        if (vec3.horizontalDistanceSqr() > 0.05D) {
            this.setYRot((float)Mth.atan2(vec3.z, vec3.x) * (180F / (float)Math.PI) - 90.0F);

        }
        super.aiStep();


        for(int i = 0;
 i < 2;
 ++i) {
            this.yRotOHeads[i] = this.yRotHeads[i];

            this.xRotOHeads[i] = this.xRotHeads[i];

        }
        for(int j = 0;
 j < 2;
 ++j) {
            int k = this.getAlternativeTarget(j + 1);

            Entity entity1 = null;

            if (k > 0) {
                entity1 = this.level().getEntity(k);

            }
            if (entity1 != null) {
                double d9 = this.getHeadX(j + 1);

                double d1 = this.getHeadY(j + 1);

                double d3 = this.getHeadZ(j + 1);

                double d4 = entity1.getX() - d9;

                double d5 = entity1.getEyeY() - d1;

                double d6 = entity1.getZ() - d3;

                double d7 = Math.sqrt(d4 * d4 + d6 * d6);

                float f = (float)(Mth.atan2(d6, d4) * (180F / (float)Math.PI)) - 90.0F;

                float f1 = (float)(-(Mth.atan2(d5, d7) * (180F / (float)Math.PI)));

                this.xRotHeads[j] = this.rotlerp(this.xRotHeads[j], f1, 40.0F);

                this.yRotHeads[j] = this.rotlerp(this.yRotHeads[j], f, 10.0F);

            } else {
                this.yRotHeads[j] = this.rotlerp(this.yRotHeads[j], this.yBodyRot, 10.0F);

            }
        }

        boolean flag = this.isPowered();

        for(int l = 0;
 l < 3;
 ++l) {
            double d8 = this.getHeadX(l);

            double d10 = this.getHeadY(l);

            double d2 = this.getHeadZ(l);

            this.level().addParticle(ParticleTypes.SMOKE, d8 + this.random.nextGaussian() * 0.3D, d10 + this.random.nextGaussian() * 0.3D, d2 + this.random.nextGaussian() * 0.3D, 0.0D, 0.0D, 0.0D);

            if (flag && this.level().random.nextInt(4) == 0) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT, d8 + this.random.nextGaussian() * 0.3D, d10 + this.random.nextGaussian() * 0.3D, d2 + this.random.nextGaussian() * 0.3D, 0.7D, 0.7D, 0.5D);

            }
        }

        if (this.getInvulnerableTicks() > 0) {
            for(int i1 = 0;
 i1 < 3;
 ++i1) {
                this.level().addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double)(this.random.nextFloat() * 3.3F), this.getZ() + this.random.nextGaussian(), 0.7D, 0.7D, 0.9D);

            }
        }

        if (!this.level().isClientSide) {
            this.customServerAiStep();

        }
    }
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();


        if (this.getInvulnerableTicks() > 0) {
            int k1 = this.getInvulnerableTicks() - 1;


            this.bossEvent.setProgress(1.0F - (float)k1 / 220.0F);


            if (k1 <= 0) {
                this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, Level.ExplosionInteraction.MOB);

                if (!this.isSilent()) {
                    this.level().globalLevelEvent(1023, this.blockPosition(), 0);

                }
            }

            this.setInvulnerableTicks(k1);


            if (this.tickCount % 10 == 0 && !this.hasExplodedAtHalfHealth) {
                this.heal(10.0F);

            }

        } else {float maxHealth = this.getMaxHealth();

            float currentHealth = this.getHealth();

            float halfHealth = maxHealth / 2.0F;


            if (!this.hasExplodedAtHalfHealth) {float phase1Progress = (currentHealth - halfHealth) / halfHealth;

                this.bossEvent.setProgress(Mth.clamp(phase1Progress, 0.0F, 1.0F));

            } else {float phase2Progress = currentHealth / halfHealth;

                this.bossEvent.setProgress(Mth.clamp(phase2Progress, 0.0F, 1.0F));

            }

            if (this.getTarget() != null) {
                this.setAlternativeTarget(0, this.getTarget().getId());

            } else {
                this.setAlternativeTarget(0, 0);

            }if (!this.hasExplodedAtHalfHealth && currentHealth <= halfHealth) {
                this.hasExplodedAtHalfHealth = true;


                this.setInvulnerableTicks(220);
return;
 
            }

            if (empCooldown > 0) {
                empCooldown--;

            } else {
                performEmpBlast();

                empCooldown = 300;

            }

            if (this.getHealth() <= 150.0F && !this.hasSummonedMinions) {
                startPhaseTwo();

            }

            if (this.shieldTimer > 0) {
                this.shieldTimer--;

                if (this.shieldTimer < 1180) {
                    checkMinionStatus(DATA_MINION_1);

                    checkMinionStatus(DATA_MINION_2);

                    checkMinionStatus(DATA_MINION_3);

                    if (getMinionId(1) == -1 && getMinionId(2) == -1 && getMinionId(3) == -1) {
                        this.shieldTimer = 0;

                    }
                }
                if (this.shieldTimer == 0) {
                    endPhaseTwo();

                } else {
                    this.setDeltaMovement(0, 0, 0);

                }
            }
        }
    }
    private void startPhaseTwo() {
        this.hasSummonedMinions = true;

        this.shieldTimer = 1200;
 

        spawnMinion(DATA_MINION_1, 3.0, 0.0);

        spawnMinion(DATA_MINION_2, -1.5, 2.6);

        spawnMinion(DATA_MINION_3, -1.5, -2.6);


        this.level().globalLevelEvent(1023, this.blockPosition(), 0);

    }

    private void spawnMinion(EntityDataAccessor<Integer> dataAccessor, double offsetX, double offsetZ) {
        if (this.level() instanceof ServerLevel serverLevel) {
            CyberWitherSkeletonEntity minion = ModEntities.CYBER_WITHER_SKELETON.get().create(serverLevel);

            if (minion != null) {
                minion.moveTo(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, this.getYRot(), 0.0F);

                minion.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);

                if (this.getTarget() != null) {
                    minion.setTarget(this.getTarget());

                }
                serverLevel.addFreshEntity(minion);

                this.entityData.set(dataAccessor, minion.getId());

            }
        }
    }

    private void checkMinionStatus(EntityDataAccessor<Integer> accessor) {
        int id = this.entityData.get(accessor);

        if (id != -1) {
            Entity entity = this.level().getEntity(id);

            if (entity == null || !entity.isAlive()) {
                this.entityData.set(accessor, -1);

            }
        }
    }

    private void endPhaseTwo() {
        this.entityData.set(DATA_MINION_1, -1);

        this.entityData.set(DATA_MINION_2, -1);

        this.entityData.set(DATA_MINION_3, -1);

    }

    private void performEmpBlast() {
        if (!this.isAlive()) return;


        List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(20.0D));

        boolean hitAny = false;


        for (Player player : nearbyPlayers) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    int currentEnergy = data.getEnergyStored();

                    if (currentEnergy > 0) {
                        data.extractEnergy(5000, false);

                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));

                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));

                        serverPlayer.sendSystemMessage(Component.literal("§cWARNING: EMP SURGE DETECTED - SYSTEMS OFFLINE"));

                    }
                });

                hitAny = true;

            }
        }

        if (hitAny) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 2.0F, 0.5F);

            this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);

        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {

        if (pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.hurt(pSource, pAmount);

        }

        if (this.getInvulnerableTicks() > 0) {
            return false;

        }

        if (this.shieldTimer > 0) {
            return false;

        }

        if (pSource.getEntity() instanceof CyberWitherBoss) {
            return false;

        }

        return super.hurt(pSource, pAmount);

    }private double getHeadX(int pHead) {
        if (pHead <= 0) return this.getX();

        float f = (this.yBodyRot + (float)(180 * (pHead - 1))) * ((float)Math.PI / 180F);

        return this.getX() + (double)Mth.cos(f) * 1.3D;

    }

    private double getHeadY(int pHead) {
        return pHead <= 0 ? this.getY() + 3.0D : this.getY() + 2.2D;

    }

    private double getHeadZ(int pHead) {
        if (pHead <= 0) return this.getZ();

        float f = (this.yBodyRot + (float)(180 * (pHead - 1))) * ((float)Math.PI / 180F);

        return this.getZ() + (double)Mth.sin(f) * 1.3D;

    }

    private float rotlerp(float pAngle, float pTarget, float pMax) {
        float f = Mth.wrapDegrees(pTarget - pAngle);

        if (f > pMax) f = pMax;

        if (f < -pMax) f = -pMax;

        return pAngle + f;

    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("Invul", this.getInvulnerableTicks());

        pCompound.putBoolean("PhaseTwoUsed", hasSummonedMinions);

        pCompound.putInt("ShieldTimer", shieldTimer);

        pCompound.putBoolean("HalfHealthExploded", hasExplodedAtHalfHealth);

    }

    @Override
    public boolean isNoGravity() {
        return true;

    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setInvulnerableTicks(pCompound.getInt("Invul"));

        this.hasSummonedMinions = pCompound.getBoolean("PhaseTwoUsed");

        this.shieldTimer = pCompound.getInt("ShieldTimer");

        this.hasExplodedAtHalfHealth = pCompound.getBoolean("HalfHealthExploded");


        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());

        }
    }
    public int getMinionId(int index) {
        return switch (index) {
            case 1 -> this.entityData.get(DATA_MINION_1);

            case 2 -> this.entityData.get(DATA_MINION_2);

            case 3 -> this.entityData.get(DATA_MINION_3);

            default -> -1;

        };

    }
    @Override
    public void setCustomName(@Nullable Component pName) {
        super.setCustomName(pName);

        this.bossEvent.setName(this.getDisplayName());

    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);

        this.bossEvent.addPlayer(pPlayer);

    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);

        this.bossEvent.removePlayer(pPlayer);

    }@Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
        this.performRangedAttack(0, pTarget);

    }

    private void performRangedAttack(int pHead, LivingEntity pTarget) {
        this.performRangedAttack(pHead, pTarget.getX(), pTarget.getY() + (double)pTarget.getEyeHeight() * 0.5D, pTarget.getZ(), pHead == 0 && this.random.nextFloat() < 0.001F);

    }

    private void performRangedAttack(int pHead, double pX, double pY, double pZ, boolean pIsDangerous) {
        if (!this.isSilent()) {
            this.level().levelEvent(null, 1024, this.blockPosition(), 0);

        }

        double headX = this.getHeadX(pHead);

        double headY = this.getHeadY(pHead);

        double headZ = this.getHeadZ(pHead);


        double vecX = pX - headX;

        double vecY = pY - headY;

        double vecZ = pZ - headZ;
double distance = Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);

        if (distance == 0) distance = 1.0;
 double offset = 1.5D;


        double spawnX = headX + (vecX / distance) * offset;

        double spawnY = headY + (vecY / distance) * offset;

        double spawnZ = headZ + (vecZ / distance) * offset;


        WitherSkull witherskull = new WitherSkull(this.level(), this, vecX, vecY, vecZ);

        witherskull.setOwner(this);

        if (pIsDangerous) {
            witherskull.setDangerous(true);

        }

        witherskull.setPosRaw(spawnX, spawnY, spawnZ);


        this.level().addFreshEntity(witherskull);

    }

    @Override
    public boolean isPowered() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;

    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;

    }

    public int getInvulnerableTicks() {
        return this.entityData.get(DATA_ID_INV);

    }

    public void setInvulnerableTicks(int pInvulnerableTicks) {
        this.entityData.set(DATA_ID_INV, pInvulnerableTicks);

    }

    public int getAlternativeTarget(int pHead) {
        return this.entityData.get(DATA_TARGETS.get(pHead));

    }

    public void setAlternativeTarget(int pTargetOffset, int pNewId) {
        this.entityData.set(DATA_TARGETS.get(pTargetOffset), pNewId);

    }

    public float getHeadYRot(int pHead) { return this.yRotHeads[pHead];
 }
    public float getHeadXRot(int pHead) { return this.xRotHeads[pHead];
 }

    protected SoundEvent getAmbientSound() { return SoundEvents.WITHER_AMBIENT;
 }
    protected SoundEvent getHurtSound(DamageSource pDamageSource) { return SoundEvents.WITHER_HURT;
 }
    protected SoundEvent getDeathSound() { return SoundEvents.WITHER_DEATH;
 }

    @Override
    public boolean canChangeDimensions() { return false;
 }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();

        } else {
            this.noActionTime = 0;

        }
    }

    class CyberWitherDoNothingGoal extends Goal {
        public CyberWitherDoNothingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));

        }
        public boolean canUse() {
            return CyberWitherBoss.this.getInvulnerableTicks() > 0;

        }
    }
}