package com.maxwell.cyber_ware_port.common.capability;

import com.maxwell.cyber_ware_port.api.event.CyberwareRejectionEvent;
import com.maxwell.cyber_ware_port.api.event.CyberwareToleranceEvent;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.A_PacketHandler;
import com.maxwell.cyber_ware_port.common.network.SyncCyberwareDataPacket;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.maxwell.cyber_ware_port.common.util.CyberwareBodyStatus;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class CyberwareUserData implements INBTSerializable<CompoundTag>, IEnergyStorage {
    // Basic State
    private boolean isInitialized = false;
    private boolean isPowered = true;
    private boolean needsCapacityUpdate = true;
    private int respawnGracePeriod = 0;

    // Body Status (Managed by CyberwareBodyStatus)
    private boolean hasCyberLeftArm = false;
    private boolean hasCyberRightArm = false;
    private boolean hasCyberLeftLeg = false;
    private boolean hasCyberRightLeg = false;

    // Essence / Tolerance
    private int maxTolerance = CyberwareConfig.MAX_TOLERANCE.get();
    private int toleranceImmunityTime = 0;

    // Energy
    private int currentEnergy = 0;
    private int maxEnergy = 0;
    private int lastProduction = 0;
    private int lastConsumption = 0;

    // Inventory
    private final ItemStackHandler installedCyberware = new ItemStackHandler(RobosurgeonBlockEntity.TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            updateBodyStatus();
            needsCapacityUpdate = true;
        }
    };

    private void enforceLimbExclusivity() {
        java.util.Map<BodyPartType, Integer> bestSlotMap = new java.util.HashMap<>();
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            if (stack.isEmpty())
                continue;
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw == null)
                continue;
            BodyPartType type = cw.getBodyPartType(stack);
            if (type == BodyPartType.NONE || cw.getMaxInstallAmount(stack) > 1)
                continue;
            int currentQuality = cw.getQuality(stack);
            if (bestSlotMap.containsKey(type)) {
                int existingSlot = bestSlotMap.get(type);
                ItemStack existingStack = installedCyberware.getStackInSlot(existingSlot);
                int existingQuality = 0;
                ICyberware existingCw = CyberwareAPI.getCyberware(existingStack);
                if (existingCw != null) {
                    existingQuality = existingCw.getQuality(existingStack);
                }
                if (currentQuality > existingQuality) {
                    installedCyberware.setStackInSlot(existingSlot, ItemStack.EMPTY);
                    bestSlotMap.put(type, i);
                } else {
                    installedCyberware.setStackInSlot(i, ItemStack.EMPTY);
                }
            } else {
                bestSlotMap.put(type, i);
            }
        }
    }

    public void recalculateCapacity(ServerPlayer player) {
        float oldMaxHealth = player.getHealth();
        float oldMaxHealthVal = player.getMaxHealth();
        float healthRatio = (oldMaxHealthVal > 0) ? oldMaxHealth / oldMaxHealthVal : 1.0F;
        AttributeMap attributeMap = player.getAttributes();
        for (var instance : attributeMap.getSyncableAttributes()) {
            List<UUID> toRemove = new ArrayList<>();
            for (var mod : instance.getModifiers()) {
                if (mod.getName().startsWith("Cyberware Slot ")) {
                    toRemove.add(mod.getId());
                }
            }
            toRemove.forEach(instance::removeModifier);
        }
        int totalCapacity = 0;
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            final int slotIndex = i;
            ItemStack stack = installedCyberware.getStackInSlot(i);
            ICyberware cyberware = CyberwareAPI.getCyberware(stack);
            if (cyberware != null) {
                int count = stack.getCount();
                if (cyberware.hasEnergyProperties(stack)) {
                    totalCapacity += cyberware.getEnergyStorage(stack) * count;
                }
                if (cyberware.isActive(stack)) {
                    boolean consumesEnergy = cyberware.hasEnergyProperties(stack)
                            && cyberware.getEnergyConsumption(stack) > 0;
                    if (!consumesEnergy || this.isPowered) {
                        cyberware.getAttributeModifiers(stack).forEach((attribute, originalModifier) -> {
                            var instance = attributeMap.getInstance(attribute);
                            if (instance != null) {
                                UUID slotUUID = generateUUID(slotIndex, originalModifier.getId());
                                AttributeModifier newModifier = new AttributeModifier(
                                        slotUUID,
                                        "Cyberware Slot " + slotIndex,
                                        originalModifier.getAmount() * count,
                                        originalModifier.getOperation());
                                if (instance.getModifier(slotUUID) == null) {
                                    instance.addTransientModifier(newModifier);
                                } else {
                                    instance.removeModifier(slotUUID);
                                    instance.addTransientModifier(newModifier);
                                }
                            }
                        });
                    }
                }
            }
        }
        this.maxEnergy = totalCapacity;
        if (this.currentEnergy > this.maxEnergy) {
            this.currentEnergy = this.maxEnergy;
        }
        player.setHealth(player.getMaxHealth() * Math.min(healthRatio, 1.0F));
    }

    public boolean hasCyberLeftArm() {
        return this.hasCyberLeftArm;
    }

    public boolean hasCyberRightArm() {
        return this.hasCyberRightArm;
    }

    public boolean hasCyberLeftLeg() {
        return this.hasCyberLeftLeg;
    }

    public boolean hasCyberRightLeg() {
        return this.hasCyberRightLeg;
    }

    private UUID generateUUID(int slot, UUID originalId) {
        return UUID.nameUUIDFromBytes((originalId.toString() + "_" + slot).getBytes());
    }

    public void setRespawnGracePeriod(int ticks) {
        this.respawnGracePeriod = ticks;
    }

    public int getMaxTolerance() {
        return this.maxTolerance;
    }

    // --- Energy Management ---

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;
        int energyReceived = Math.min(maxEnergy - currentEnergy, maxReceive);
        if (!simulate) {
            currentEnergy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        int energyExtracted = Math.min(currentEnergy, maxExtract);
        if (!simulate) {
            currentEnergy -= energyExtracted;
        }
        return energyExtracted;
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

    // --- Tolerance & Essence Management ---

    public int getMaxTolerance(LivingEntity entity) {
        CyberwareToleranceEvent event = new CyberwareToleranceEvent(entity, this.maxTolerance);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getNewTolerance();
    }

    public int getTolerance(LivingEntity entity) {
        int consumed = 0;
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            ICyberware cyberware = CyberwareAPI.getCyberware(stack);
            if (cyberware != null) {
                consumed += cyberware.getEssenceCost(stack) * stack.getCount();
            }
        }
        return getMaxTolerance(entity) - consumed;
    }

    // --- Survival & Condition Checks ---

    public void tick(ServerPlayer player) {
        if (this.toleranceImmunityTime > 0) {
            this.toleranceImmunityTime--;
        }
        if (this.respawnGracePeriod > 0) {
            this.respawnGracePeriod--;
        }
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

        if (player.tickCount % 20 == 0) {
            processPowerTick(player);
        }
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

        // Non-lethal conditions
        if (!status.hasPart(BodyPartType.EYES)) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
        }
        if (!status.hasPart(BodyPartType.LUNGS)) {
            int air = player.getAirSupply();
            if (air > -20) {
                player.setAirSupply(air - 1);
                if (air <= 0 && player.tickCount % 20 == 0) {
                    player.hurt(player.damageSources().drown(), 2.0F);
                }
            }
        }
        if (!status.hasPart(BodyPartType.STOMACH)) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 1, false, false));
        }
        if (status.getLegCount() == 0) {
            player.setForcedPose(Pose.SWIMMING);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false));
        } else {
            player.setForcedPose(null);
        }
    }

    private void checkRejection(ServerPlayer player) {
        int currentTolerance = getTolerance(player);
        if (currentTolerance <= 0) {
            if (this.respawnGracePeriod <= 0) {
                killPlayer(player, "cyberware.noessence");
            } else if (player.tickCount % 400 == 0) {
                player.sendSystemMessage(Component.translatable("cyberware.message.critical_condition")
                        .withStyle(net.minecraft.ChatFormatting.RED));
            }
            return;
        }

        if (this.toleranceImmunityTime > 0)
            return;

        int rejectionThreshold = CyberwareConfig.CRITICAL_ESSENCE.get();
        if (MinecraftForge.EVENT_BUS.post(new CyberwareRejectionEvent(player, currentTolerance)))
            return;

        if (currentTolerance < rejectionThreshold) {
            if (player.tickCount % 100 == 0)
                player.setHealth(player.getHealth() - 2.0f);
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, false));
            if (player.getRandom().nextFloat() < 0.01f && !player.getMainHandItem().isEmpty()) {
                ItemStack stackToDrop = player.getMainHandItem().copy();
                player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                player.drop(stackToDrop, false, false);
            }
        }
    }

    private void processPowerTick(ServerPlayer player) {
        if (maxEnergy <= 0) {
            lastProduction = lastConsumption = 0;
            return;
        }

        int totalProduction = 0;
        int totalConsumption = 0;
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null && cw.hasEnergyProperties(stack)) {
                int count = stack.getCount();
                ICyberware.StackingRule rule = cw.getStackingEnergyRule(stack);
                totalProduction += rule.calculate(cw.getEnergyGeneration(stack), count);
                totalConsumption += rule.calculate(cw.getEnergyConsumption(stack), count);
            }
        }

        lastProduction = totalProduction;
        lastConsumption = totalConsumption;
        receiveEnergy(totalProduction, false);

        boolean currentlyPowered = currentEnergy >= totalConsumption;
        if (totalConsumption > 0) {
            if (currentlyPowered)
                extractEnergy(totalConsumption, false);
            else
                currentEnergy = 0;
        }

        if (isPowered != currentlyPowered) {
            isPowered = currentlyPowered;
            recalculateCapacity(player);
        }
        syncToClient(player);
    }

    private void updateBodyStatus() {
        CyberwareBodyStatus status = new CyberwareBodyStatus(installedCyberware);
        this.hasCyberLeftArm = status.getArmCount() >= 1;
        this.hasCyberRightArm = status.getArmCount() >= 2;
        this.hasCyberLeftLeg = status.getLegCount() >= 1;
        this.hasCyberRightLeg = status.getLegCount() >= 2;
    }

    public void syncToClient(ServerPlayer player) {
        if (player == null)
            return;
        CompoundTag tag = this.serializeNBT();
        tag.putInt("MaxTolerance", getMaxTolerance(player));
        A_PacketHandler.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncCyberwareDataPacket(tag));
    }

    public void ensureEssentialPartsAfterDeath() {
        Set<BodyPartType> presentParts = EnumSet.noneOf(BodyPartType.class);
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (!stack.isEmpty() && cw != null) {
                BodyPartType type = cw.getBodyPartType(stack);
                if (type != BodyPartType.NONE) {
                    presentParts.add(type);
                }
            }
        }
        if (!presentParts.contains(BodyPartType.BRAIN)) {
            installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BRAIN,
                    new ItemStack(ModItems.HUMAN_BRAIN.get()));
        }
        if (!presentParts.contains(BodyPartType.HEART)) {
            installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HEART,
                    new ItemStack(ModItems.HUMAN_HEART.get()));
        }
        if (!presentParts.contains(BodyPartType.MUSCLE)) {
            installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_MUSCLE,
                    new ItemStack(ModItems.HUMAN_MUSCLE.get()));
        }
        if (!presentParts.contains(BodyPartType.BONES)) {
            installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BONES,
                    new ItemStack(ModItems.HUMAN_BONE.get()));
        }
    }

    public void resetToHuman() {
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            installedCyberware.setStackInSlot(i, ItemStack.EMPTY);
        }
        this.isInitialized = false;
        this.currentEnergy = 0;
        this.maxEnergy = 0;
        this.lastProduction = 0;
        this.lastConsumption = 0;
        fillWithHumanParts();
    }

    public void applyImmunity(int ticks) {
        this.toleranceImmunityTime = Math.max(this.toleranceImmunityTime, ticks);
    }

    private void killPlayer(ServerPlayer player, String suffix) {
        DamageSource source = new DamageSource(
                player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)) {
            @Override
            public Component getLocalizedDeathMessage(LivingEntity entity) {
                return Component.translatable("death.attack." + suffix, entity.getDisplayName());
            }
        };
        player.hurt(source, Float.MAX_VALUE);
    }

    public void copyFrom(CyberwareUserData other) {
        for (int i = 0; i < this.installedCyberware.getSlots(); i++) {
            this.installedCyberware.setStackInSlot(i, other.installedCyberware.getStackInSlot(i).copy());
        }
        this.maxTolerance = other.maxTolerance;
        this.currentEnergy = other.currentEnergy;
        this.maxEnergy = other.maxEnergy;
        this.lastProduction = other.lastProduction;
        this.lastConsumption = other.lastConsumption;
        this.isInitialized = other.isInitialized;
    }

    public int getLastProduction() {
        return lastProduction;
    }

    public int getLastConsumption() {
        return lastConsumption;
    }

    public void fillWithHumanParts() {
        if (isInitialized)
            return;
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BRAIN, new ItemStack(ModItems.HUMAN_BRAIN.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HEART, new ItemStack(ModItems.HUMAN_HEART.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_STOMACH,
                new ItemStack(ModItems.HUMAN_STOMACH.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_SKIN, new ItemStack(ModItems.HUMAN_SKIN.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_MUSCLE,
                new ItemStack(ModItems.HUMAN_MUSCLE.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BONES, new ItemStack(ModItems.HUMAN_BONE.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_EYES, new ItemStack(ModItems.HUMAN_EYES.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_LUNGS, new ItemStack(ModItems.HUMAN_LUNGS.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_ARMS,
                new ItemStack(ModItems.HUMAN_LEFT_ARM.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_ARMS + 1,
                new ItemStack(ModItems.HUMAN_RIGHT_ARM.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HANDS + 1,
                new ItemStack(ModItems.HUMAN_LEFT_HAND.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HANDS,
                new ItemStack(ModItems.HUMAN_RIGHT_HAND.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_LEGS,
                new ItemStack(ModItems.HUMAN_LEFT_LEG.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_LEGS + 1,
                new ItemStack(ModItems.HUMAN_RIGHT_LEG.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BOOTS + 1,
                new ItemStack(ModItems.HUMAN_LEFT_FOOT.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BOOTS,
                new ItemStack(ModItems.HUMAN_RIGHT_FOOT.get()));
        this.isInitialized = true;
        updateBodyStatus();
    }

    public ItemStackHandler getInstalledCyberware() {
        return installedCyberware;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("InstalledCyberware", installedCyberware.serializeNBT());
        tag.putInt("MaxTolerance", maxTolerance);
        tag.putBoolean("IsInitialized", isInitialized);
        tag.putInt("ImmunityTime", toleranceImmunityTime);
        tag.putInt("MaxEnergy", maxEnergy);
        tag.putInt("LastProd", lastProduction);
        tag.putInt("LastCons", lastConsumption);
        tag.putInt("CurrentEnergy", currentEnergy);
        return tag;
    }

    public boolean isCyberwareInstalled(Item item) {
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("InstalledCyberware")) {
            installedCyberware.deserializeNBT(nbt.getCompound("InstalledCyberware"));
        }
        this.maxTolerance = CyberwareConfig.MAX_TOLERANCE.get();
        if (nbt.contains("IsInitialized"))
            isInitialized = nbt.getBoolean("IsInitialized");
        if (nbt.contains("MaxEnergy"))
            maxEnergy = nbt.getInt("MaxEnergy");
        if (nbt.contains("CurrentEnergy"))
            currentEnergy = nbt.getInt("CurrentEnergy");
        if (nbt.contains("LastProd"))
            lastProduction = nbt.getInt("LastProd");
        if (nbt.contains("LastCons"))
            lastConsumption = nbt.getInt("LastCons");
        if (nbt.contains("ImmunityTime"))
            this.toleranceImmunityTime = nbt.getInt("ImmunityTime");
        updateBodyStatus();
    }

    public boolean isInitialized() {
        return isInitialized;
    }

}