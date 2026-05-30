package com.maxwell.cyber_ware_port.common.capability;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.event.CyberwareRejectionEvent;
import com.maxwell.cyber_ware_port.api.event.CyberwareToleranceEvent;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.SyncCyberwareDataPacket;
import com.maxwell.cyber_ware_port.common.util.CyberwareBodyStatus;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CyberwareUserData implements INBTSerializable<CompoundTag>, IEnergyStorage {
    public boolean hasCyberLeftArm = false;
    public boolean hasCyberRightArm = false;
    public boolean hasCyberLeftLeg = false;
    public boolean hasCyberRightLeg = false;
    private boolean isInitialized = false;
    private boolean isPowered = true;
    private boolean needsCapacityUpdate = true;
    private int respawnGracePeriod = 0;
    private int maxTolerance = CyberwareConfig.MAX_TOLERANCE.get();
    private int toleranceImmunityTime = 0;
    private int currentEnergy = 0;
    private int maxEnergy = 0;
    private int lastProduction = 0;
    private int lastConsumption = 0;

    public void applyImmunity(int ticks) {
        this.toleranceImmunityTime = Math.max(this.toleranceImmunityTime, ticks);
    }

    public boolean hasCyberLeftArm() {
        return this.hasCyberLeftArm;
    }    private final ItemStackHandler installedCyberware = new ItemStackHandler(RobosurgeonBlockEntity.TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            updateBodyStatus();
            needsCapacityUpdate = true;
        }
    };

    public boolean hasCyberRightArm() {
        return this.hasCyberRightArm;
    }

    public boolean hasCyberLeftLeg() {
        return this.hasCyberLeftLeg;
    }

    public boolean hasCyberRightLeg() {
        return this.hasCyberRightLeg;
    }

    public int getLastProduction() {
        return this.lastProduction;
    }

    public int getLastConsumption() {
        return this.lastConsumption;
    }

    public int getMaxTolerance(LivingEntity entity) {
        CyberwareToleranceEvent event = new CyberwareToleranceEvent(entity, this.maxTolerance);
        NeoForge.EVENT_BUS.post(event);
        return event.getNewTolerance();
    }

    public void recalculateCapacity(ServerPlayer player) {
        float oldMaxHealth = player.getHealth();
        float oldMaxHealthVal = player.getMaxHealth();
        float healthRatio = (oldMaxHealthVal > 0) ? oldMaxHealth / oldMaxHealthVal : 1.0F;
        AttributeMap attributeMap = player.getAttributes();
        for (AttributeInstance instance : attributeMap.getSyncableAttributes()) {
            List<ResourceLocation> toRemove = new ArrayList<>();
            instance.getModifiers().forEach(mod -> {
                if (mod.id().getNamespace().equals(CyberWare.MODID) && mod.id().getPath().startsWith("slot_")) {
                    toRemove.add(mod.id());
                }
            });
            toRemove.forEach(instance::removeModifier);
        }
        int totalCapacity = 0;
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            ICyberware cyberware = CyberwareAPI.getCyberware(stack);
            if (cyberware != null) {
                int count = stack.getCount();
                if (cyberware.hasEnergyProperties(stack)) {
                    totalCapacity += cyberware.getEnergyStorage(stack) * count;
                }
                if (cyberware.isActive(stack)) {
                    boolean consumesEnergy = cyberware.hasEnergyProperties(stack) && cyberware.getEnergyConsumption(stack) > 0;
                    if (!consumesEnergy || this.isPowered) {
                        int finalI = i;
                        cyberware.getAttributeModifiers(stack).forEach((attribute, originalModifier) -> {
                            AttributeInstance instance = attributeMap.getInstance(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(Objects.requireNonNull(attribute).value()));
                            if (instance != null) {
                                ResourceLocation modId = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "slot_" + finalI + "_" + originalModifier.id().getPath());
                                AttributeModifier newModifier = new AttributeModifier(modId, originalModifier.amount() * count, originalModifier.operation());
                                instance.addOrUpdateTransientModifier(newModifier);
                            }
                        });
                    }
                }
            }
        }
        this.maxEnergy = totalCapacity;
        this.currentEnergy = Math.min(this.currentEnergy, this.maxEnergy);
        player.setHealth(player.getMaxHealth() * Math.min(healthRatio, 1.0F));
    }

    public void tick(ServerPlayer player) {
        if (this.toleranceImmunityTime > 0) this.toleranceImmunityTime--;
        if (this.respawnGracePeriod > 0) this.respawnGracePeriod--;
        if (this.needsCapacityUpdate) {
            recalculateCapacity(player);
            this.needsCapacityUpdate = false;
        }
        CyberwareBodyStatus status = new CyberwareBodyStatus(installedCyberware);
        checkSurvival(player, status);
        checkRejection(player);
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            ICyberware cyberware = CyberwareAPI.getCyberware(stack);
            if (cyberware != null) {
                cyberware.onSystemTick(player, stack);
            }
        }
        if (player.tickCount % 20 == 0) processPowerTick(player);
    }

    private void checkSurvival(ServerPlayer player, CyberwareBodyStatus status) {
        if (!status.hasPart(BodyPartType.BRAIN)) {
            killPlayer(player, "cyberware.brainless");
            return;
        }
        if (!status.hasPart(BodyPartType.HEART)) {
            killPlayer(player, "cyberware.heartless");
            return;
        }
        if (!status.hasPart(BodyPartType.MUSCLE)) {
            killPlayer(player, "cyberware.nomuscles");
            return;
        }
        if (!status.hasPart(BodyPartType.BONES)) {
            killPlayer(player, "cyberware.cyberware_missing_bone");
            return;
        }
        if (!status.hasPart(BodyPartType.EYES))
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
        if (!status.hasPart(BodyPartType.LUNGS)) {
            int air = player.getAirSupply();
            if (air > -20) {
                player.setAirSupply(air - 1);
                if (air <= 0 && player.tickCount % 20 == 0) player.hurt(player.level().damageSources().drown(), 2.0F);
            }
        }
        if (!status.hasPart(BodyPartType.STOMACH))
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 1, false, false));
        if (status.getLegCount() == 0) {
            player.setForcedPose(Pose.SWIMMING);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false));
        } else player.setForcedPose(null);
    }

    private void checkRejection(ServerPlayer player) {
        int currentTolerance = getTolerance(player);
        if (currentTolerance <= 0) {
            if (this.respawnGracePeriod <= 0) {
                killPlayer(player, "cyberware.noessence");
            } else if (player.tickCount % 400 == 0) {
                player.sendSystemMessage(Component.translatable("cyberware.message.critical_condition").withStyle(ChatFormatting.RED));
            }
            return;
        }
        if (this.toleranceImmunityTime > 0) return;
        CyberwareRejectionEvent event = new CyberwareRejectionEvent(player, currentTolerance);
        NeoForge.EVENT_BUS.post(event);
        if (currentTolerance < CyberwareConfig.CRITICAL_ESSENCE.get()) {
            if (player.tickCount % 100 == 0) {
                player.setHealth(player.getHealth() - 2.0f);
            }
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, false));
            if (player.getRandom().nextFloat() < 0.01f && !player.getMainHandItem().isEmpty()) {
                ItemStack stackToDrop = player.getMainHandItem().copy();
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                player.drop(stackToDrop, false, false);
            }
        }
    }

    private void processPowerTick(ServerPlayer player) {
        if (maxEnergy <= 0) {
            lastProduction = lastConsumption = 0;
            return;
        }
        int prod = 0, cons = 0;
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null && cw.hasEnergyProperties(stack)) {
                int count = stack.getCount();
                ICyberware.StackingRule rule = cw.getStackingEnergyRule(stack);
                prod += rule.calculate(cw.getEnergyGeneration(stack), count);
                cons += rule.calculate(cw.getEnergyConsumption(stack), count);
            }
        }
        lastProduction = prod;
        lastConsumption = cons;
        receiveEnergy(prod, false);
        boolean currentlyPowered = currentEnergy >= cons;
        if (cons > 0) {
            if (currentlyPowered) extractEnergy(cons, false);
            else currentEnergy = 0;
        }
        if (isPowered != currentlyPowered) {
            isPowered = currentlyPowered;
            recalculateCapacity(player);
        }
        syncToClient(player);
    }

    public int getTolerance(LivingEntity entity) {
        int consumed = 0;
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            ICyberware cyberware = CyberwareAPI.getCyberware(stack);
            if (cyberware != null) consumed += cyberware.getEssenceCost(stack) * stack.getCount();
        }
        CyberwareToleranceEvent event = new CyberwareToleranceEvent(entity, this.maxTolerance);
        NeoForge.EVENT_BUS.post(event);
        return event.getNewTolerance() - consumed;
    }

    public void syncToClient(ServerPlayer player) {
        if (player == null || player.level().isClientSide) return;

        CompoundTag tag = this.serializeNBT(player.registryAccess());
        SyncCyberwareDataPacket packet = new SyncCyberwareDataPacket(tag, player.getId());
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
    }
    private void killPlayer(ServerPlayer player, String suffix) {
        Holder<@NotNull DamageType> fellOutOfWorldHolder =
                player.damageSources().fellOutOfWorld().typeHolder();
        DamageSource source = new DamageSource(fellOutOfWorldHolder) {
            @Override
            public Component getLocalizedDeathMessage(LivingEntity entity) {
                return Component.translatable("death.attack." + suffix, entity.getDisplayName());
            }
        };
        player.hurt(source, Float.MAX_VALUE);
    }

    public void fillWithHumanParts() {
        if (isInitialized) return;
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BRAIN, new ItemStack(ModItems.HUMAN_BRAIN.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HEART, new ItemStack(ModItems.HUMAN_HEART.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_STOMACH, new ItemStack(ModItems.HUMAN_STOMACH.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_SKIN, new ItemStack(ModItems.HUMAN_SKIN.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_MUSCLE, new ItemStack(ModItems.HUMAN_MUSCLE.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BONES, new ItemStack(ModItems.HUMAN_BONE.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_EYES, new ItemStack(ModItems.HUMAN_EYES.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_LUNGS, new ItemStack(ModItems.HUMAN_LUNGS.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_ARMS, new ItemStack(ModItems.HUMAN_LEFT_ARM.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_ARMS + 1, new ItemStack(ModItems.HUMAN_RIGHT_ARM.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HANDS, new ItemStack(ModItems.HUMAN_RIGHT_HAND.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HANDS + 1, new ItemStack(ModItems.HUMAN_LEFT_HAND.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_LEGS, new ItemStack(ModItems.HUMAN_LEFT_LEG.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_LEGS + 1, new ItemStack(ModItems.HUMAN_RIGHT_LEG.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BOOTS, new ItemStack(ModItems.HUMAN_RIGHT_FOOT.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BOOTS + 1, new ItemStack(ModItems.HUMAN_LEFT_FOOT.get()));
        this.isInitialized = true;
        updateBodyStatus();
    }

    private void updateBodyStatus() {
        this.hasCyberLeftArm = isCyberwareInstalled(ModItems.CYBER_ARM_LEFT.get());
        this.hasCyberRightArm = isCyberwareInstalled(ModItems.CYBER_ARM_RIGHT.get());

        this.hasCyberLeftLeg = isCyberwareInstalled(ModItems.CYBER_LEG_LEFT.get());
        this.hasCyberRightLeg = isCyberwareInstalled(ModItems.CYBER_LEG_RIGHT.get());
    }

    public void ensureEssentialPartsAfterDeath() {
        CyberwareBodyStatus status = new CyberwareBodyStatus(installedCyberware);
        if (!status.hasPart(BodyPartType.BRAIN))
            installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BRAIN, new ItemStack(ModItems.HUMAN_BRAIN.get()));
        if (!status.hasPart(BodyPartType.HEART))
            installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HEART, new ItemStack(ModItems.HUMAN_HEART.get()));
        if (!status.hasPart(BodyPartType.MUSCLE))
            installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_MUSCLE, new ItemStack(ModItems.HUMAN_MUSCLE.get()));
        if (!status.hasPart(BodyPartType.BONES))
            installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BONES, new ItemStack(ModItems.HUMAN_BONE.get()));
    }

    public void resetToHuman() {
        for (int i = 0; i < installedCyberware.getSlots(); i++) installedCyberware.setStackInSlot(i, ItemStack.EMPTY);
        this.isInitialized = false;
        currentEnergy = 0;
        fillWithHumanParts();
    }

    public void copyFrom(CyberwareUserData other) {
        for (int i = 0; i < this.installedCyberware.getSlots(); i++)
            this.installedCyberware.setStackInSlot(i, other.installedCyberware.getStackInSlot(i).copy());
        this.maxTolerance = other.maxTolerance;
        this.currentEnergy = other.currentEnergy;
        this.maxEnergy = other.maxEnergy;
        this.isInitialized = other.isInitialized;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = Math.min(maxEnergy - currentEnergy, maxReceive);
        if (!simulate) currentEnergy += received;
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = Math.min(currentEnergy, maxExtract);
        if (!simulate) currentEnergy -= extracted;
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return currentEnergy;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxEnergy;
    }

    @Override
    public boolean canExtract() {
        return maxEnergy > 0;
    }

    @Override
    public boolean canReceive() {
        return maxEnergy > 0;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setRespawnGracePeriod(int ticks) {
        this.respawnGracePeriod = ticks;
    }

    public ItemStackHandler getInstalledCyberware() {
        return installedCyberware;
    }

    public boolean isCyberwareInstalled(Item item) {
        for (int i = 0; i < installedCyberware.getSlots(); i++)
            if (installedCyberware.getStackInSlot(i).is(item)) return true;
        return false;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put("InstalledCyberware", installedCyberware.serializeNBT(provider));
        tag.putInt("MaxTolerance", maxTolerance);
        tag.putBoolean("IsInitialized", isInitialized);
        tag.putInt("ImmunityTime", toleranceImmunityTime);
        tag.putInt("MaxEnergy", maxEnergy);
        tag.putInt("CurrentEnergy", currentEnergy);
        tag.putInt("LastProd", lastProduction);
        tag.putInt("LastCons", lastConsumption);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt.contains("InstalledCyberware"))
            installedCyberware.deserializeNBT(provider, nbt.getCompound("InstalledCyberware"));
        this.maxTolerance = nbt.contains("MaxTolerance") ? nbt.getInt("MaxTolerance") : CyberwareConfig.MAX_TOLERANCE.get();
        isInitialized = nbt.getBoolean("IsInitialized");
        maxEnergy = nbt.getInt("MaxEnergy");
        currentEnergy = nbt.getInt("CurrentEnergy");
        lastProduction = nbt.getInt("LastProd");
        lastConsumption = nbt.getInt("LastCons");
        toleranceImmunityTime = nbt.getInt("ImmunityTime");
        updateBodyStatus();
    }


}