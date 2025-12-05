package com.Maxwell.cyber_ware_port.Common.Capability;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import com.Maxwell.cyber_ware_port.Common.Network.PacketHandler;
import com.Maxwell.cyber_ware_port.Common.Network.SyncCyberwareDataPacket;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import com.google.common.collect.Multimap;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;

import java.util.EnumSet;
import java.util.Set;

public class CyberwareUserData implements INBTSerializable<CompoundTag>, IEnergyStorage {
    private boolean isInitialized = false;
    private int maxTolerance = 100;

    private int currentEnergy = 0;
    private int maxEnergy = 0;

    private int lastProduction = 0;
    private int lastConsumption = 0;

    private final ItemStackHandler installedCyberware = new ItemStackHandler(RobosurgeonBlockEntity.TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {}
    };

    /**
     * エネルギー容量とプレイヤーの属性(Attributes)を再計算して適用する。
     * 手術完了時、ログイン時、トグル切り替え時などに呼び出すこと。
     * @param player 対象のプレイヤー
     */
    public void recalculateCapacity(ServerPlayer player) {
        int totalCapacity = 0;

        AttributeMap attributeMap = player.getAttributes();

        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cyberware) {

                int count = stack.getCount();
                if (cyberware.hasEnergyProperties(stack)) {
                    int singleStorage = cyberware.getEnergyStorage(stack);
                    totalCapacity += singleStorage * count;
                }
                if (cyberware.isActive(stack)) {
                    cyberware.getAttributeModifiers(stack).forEach((attribute, modifier) -> {
                        if (attributeMap.hasAttribute(attribute)) {
                            AttributeInstance instance = attributeMap.getInstance(attribute);
                            if (!instance.hasModifier(modifier)) {
                                instance.addTransientModifier(modifier);
                            }
                        }
                    });
                }
            }
        }

        this.maxEnergy = totalCapacity;
        if (this.currentEnergy > this.maxEnergy) {
            this.currentEnergy = this.maxEnergy;
        }

        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    public void tick(ServerPlayer player) {if (this.maxEnergy == 0 && this.isInitialized) {
            recalculateCapacity(player);
        }

        if (maxEnergy <= 0) {
            this.lastProduction = 0;
            this.lastConsumption = 0;
            return;
        }

        int totalProduction = 0;
        int totalConsumption = 0;

        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cyberware) {
                cyberware.onWornTick(player, stack, this);
                if (cyberware.hasEnergyProperties(stack)) {
                    int count = stack.getCount();
                    ICyberware.StackingRule rule = cyberware.getStackingEnergyRule(stack);
                    totalProduction += rule.calculate(cyberware.getEnergyGeneration(stack), count);
                    totalConsumption += rule.calculate(cyberware.getEnergyConsumption(stack), count);
                }
            }
        }
        this.lastProduction = totalProduction;
        this.lastConsumption = totalConsumption;

        if (totalProduction > 0) receiveEnergy(totalProduction, false);
        if (totalConsumption > 0) {
            if (this.currentEnergy >= totalConsumption) {
                extractEnergy(totalConsumption, false);
            }
        }

        if (player.tickCount % 20 == 0) {
            syncToClient(player);
        }
    }
    public void setInitialized() {
        this.isInitialized = true;
    }
    private void checkBodyCondition(ServerPlayer player) {

        Set<BodyPartType> presentParts = EnumSet.noneOf(BodyPartType.class);

        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cyberware) {
                BodyPartType type = cyberware.getBodyPartType(stack);
                if (type != BodyPartType.NONE) {
                    presentParts.add(type);
                }
            }
        }

        Level level = player.level();DamageSource organFailure = player.damageSources().dragonBreath();
        DamageSource lowTolerance = player.damageSources().starve();  

        if (!presentParts.contains(BodyPartType.BRAIN)) {
            killPlayer(player, "cyberware_organ_failure");
            return;
        }
        if (!presentParts.contains(BodyPartType.HEART)) {
            killPlayer(player, "cyberware_organ_failure");
            return;
        }
        if (getTolerance() <= 0) {
            killPlayer(player, "cyberware_low_tolerance");
            return;
        }if (!presentParts.contains(BodyPartType.EYES)) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, false));
        }

        if (!presentParts.contains(BodyPartType.LUNGS)) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1, false, false));
        }

        if (!presentParts.contains(BodyPartType.STOMACH)) {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1, false, false));
        }

        if (!presentParts.contains(BodyPartType.BONES)) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, false));
        }

        if (!presentParts.contains(BodyPartType.MUSCLE)) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2, false, false));
        }

        if (!presentParts.contains(BodyPartType.SKIN)) {

            player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0, false, false));
        }
    }

    private void killPlayer(ServerPlayer player, String msgId) {DamageSource source = new DamageSource(
                player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.OUTSIDE_BORDER)
        ) {
            @Override
            public String getMsgId() {
                return msgId;
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
    public int getLastProduction() { return lastProduction; }
    public int getLastConsumption() { return lastConsumption; }
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
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HANDS + 1, new ItemStack(ModItems.HUMAN_LEFT_HAND.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_HANDS, new ItemStack(ModItems.HUMAN_RIGHT_HAND.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_LEGS, new ItemStack(ModItems.HUMAN_LEFT_LEG.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_LEGS + 1, new ItemStack(ModItems.HUMAN_RIGHT_LEG.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BOOTS + 1, new ItemStack(ModItems.HUMAN_LEFT_FOOT.get()));
        installedCyberware.setStackInSlot(RobosurgeonBlockEntity.SLOT_BOOTS, new ItemStack(ModItems.HUMAN_RIGHT_FOOT.get()));

        this.isInitialized = true;
    }

    public int getTolerance() {
        int consumed = 0;
        for (int i = 0; i < installedCyberware.getSlots(); i++) {
            ItemStack stack = installedCyberware.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cyberware) {
                consumed += cyberware.getEssenceCost(stack) * stack.getCount();
            }
        }
        return maxTolerance - consumed;
    }
    public ItemStackHandler getInstalledCyberware() {
        return installedCyberware;
    }

    public void syncToClient(ServerPlayer player) {
        if (player == null) return;
        CompoundTag tag = this.serializeNBT();
        PacketHandler.INSTANCE.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new SyncCyberwareDataPacket(tag)
        );
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("InstalledCyberware", installedCyberware.serializeNBT());
        tag.putInt("MaxTolerance", maxTolerance);
        tag.putBoolean("IsInitialized", isInitialized);
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
        if (nbt.contains("MaxTolerance")) {
            maxTolerance = nbt.getInt("MaxTolerance");
        }
        if (nbt.contains("IsInitialized")) {
            isInitialized = nbt.getBoolean("IsInitialized");
        }
        if (nbt.contains("MaxEnergy")) {
            maxEnergy = nbt.getInt("MaxEnergy");
        }
        if (nbt.contains("CurrentEnergy")) {
            currentEnergy = nbt.getInt("CurrentEnergy");
        }
        if (nbt.contains("LastProd")) lastProduction = nbt.getInt("LastProd");
        if (nbt.contains("LastCons")) lastConsumption = nbt.getInt("LastCons");
    }

    public boolean isInitialized() {
        return isInitialized;
    }
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;

        int energyReceived = Math.min(maxEnergy - currentEnergy, maxReceive);
        if (!simulate) {
            currentEnergy += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

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
}