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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
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
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CyberwareUserData extends SnapshotJournal<Integer> implements EnergyHandler, ValueIOSerializable {
    public static final StreamCodec<RegistryFriendlyByteBuf, CyberwareUserData> STREAM_CODEC = StreamCodec.of(
            (buf, data) -> {
                TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, buf.registryAccess());
                data.serialize(output);
                buf.writeNbt(output.buildResult());
            },
            buf -> {
                CompoundTag tag = buf.readNbt();
                CyberwareUserData data = new CyberwareUserData();
                if (tag != null) {
                    var input = TagValueInput.create(ProblemReporter.DISCARDING, buf.registryAccess(), tag);
                    data.deserialize(input);
                }
                return data;
            }
    );
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

    private int empTicks = 0;

    private final ItemStacksResourceHandler installedCyberware = new ItemStacksResourceHandler(RobosurgeonBlockEntity.TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            updateBodyStatus();
            needsCapacityUpdate = true;
        }
    };

    public CyberwareUserData() {
        super();
        this.currentEnergy = 0;
    }

    public int getEmpTicks() {
        return this.empTicks;
    }

    public void setEmpTicks(int ticks) {
        this.empTicks = ticks;
        this.needsCapacityUpdate = true;
    }

    public int getImmunityTime() {
        return this.toleranceImmunityTime;
    }

    public boolean isPowered() {
        return this.isPowered && this.empTicks <= 0;
    }

    public static boolean isItemPowered(CyberwareUserData data, ICyberware cw, ItemStack stack) {
        if (!cw.isActive(stack)) return false;
        if (data.getEmpTicks() > 0) return false;
        if (cw.hasEnergyProperties(stack) && cw.getEnergyConsumption(stack) > 0) {
            return data.isPowered();
        }
        return true;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, provider, tag);
        this.deserialize(input);
    }

    public void applyImmunity(int ticks) {
        this.toleranceImmunityTime = Math.max(this.toleranceImmunityTime, ticks);
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

    private ItemStack getStack(int slot) {
        return installedCyberware.getResource(slot).toStack(installedCyberware.getAmountAsInt(slot));
    }

    private void setStack(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            installedCyberware.set(slot, ItemResource.EMPTY, 0);
        } else {
            installedCyberware.set(slot, ItemResource.of(stack), stack.getCount());
        }
    }

    public void recalculateCapacity(ServerPlayer player) {
        float oldMaxHealth = player.getHealth();
        float oldMaxHealthVal = player.getMaxHealth();
        float healthRatio = (oldMaxHealthVal > 0) ? oldMaxHealth / oldMaxHealthVal : 1.0F;
        AttributeMap attributeMap = player.getAttributes();
        for (AttributeInstance instance : attributeMap.getSyncableAttributes()) {
            List<Identifier> toRemove = new ArrayList<>();
            instance.getModifiers().forEach(mod -> {
                if (mod.id().getNamespace().equals(CyberWare.MODID) && mod.id().getPath().startsWith("slot_")) {
                    toRemove.add(mod.id());
                }
            });
            toRemove.forEach(instance::removeModifier);
        }
        int totalCapacity = 0;
        for (int i = 0; i < installedCyberware.size(); i++) {
            ItemStack stack = getStack(i);
            ICyberware cyberware = CyberwareAPI.getCyberware(stack);
            if (cyberware != null) {
                int count = stack.getCount();
                if (cyberware.hasEnergyProperties(stack)) {
                    totalCapacity += cyberware.getEnergyStorage(stack) * count;
                }
                if (cyberware.isActive(stack)) {
                    boolean consumesEnergy = cyberware.hasEnergyProperties(stack) && cyberware.getEnergyConsumption(stack) > 0;
                    if (!consumesEnergy || this.isPowered()) {
                        int finalI = i;
                        cyberware.getAttributeModifiers(stack).forEach((attribute, originalModifier) -> {
                            AttributeInstance instance = attributeMap.getInstance(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(Objects.requireNonNull(attribute).value()));
                            if (instance != null) {
                                Identifier modId = Identifier.fromNamespaceAndPath(CyberWare.MODID, "slot_" + finalI + "_" + originalModifier.id().getPath());
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
        if (this.empTicks > 0) this.empTicks--; 
        if (this.needsCapacityUpdate) {
            recalculateCapacity(player);
            this.needsCapacityUpdate = false;
        }
        CyberwareBodyStatus status = new CyberwareBodyStatus(installedCyberware);
        checkSurvival(player, status);
        checkRejection(player);
        for (int i = 0; i < installedCyberware.size(); i++) {
            ItemStack stack = getStack(i);
            ICyberware cyberware = CyberwareAPI.getCyberware(stack);

            if (cyberware != null && isItemPowered(this, cyberware, stack)) {
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
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2, false, false));
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
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, false));
            if (player.getRandom().nextFloat() < 0.01f && !player.getMainHandItem().isEmpty()) {
                ItemStack stackToDrop = player.getMainHandItem().copy();
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                player.drop(stackToDrop, false, false);
            }
        }
    }

    private void processPowerTick(ServerPlayer player) {
        int prod = 0, cons = 0;
        for (int i = 0; i < installedCyberware.size(); i++) {
            ItemStack stack = getStack(i);
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null && cw.hasEnergyProperties(stack)) {
                int count = stack.getCount();
                ICyberware.StackingRule rule = cw.getStackingEnergyRule(stack);
                prod += rule.calculate(cw.getEnergyGeneration(stack), count);
                cons += rule.calculate(cw.getEnergyConsumption(stack), count);
            }
        }
        this.lastProduction = prod;
        this.lastConsumption = cons;

        boolean currentlyPowered = false;
        if (this.maxEnergy > 0) {
            try (Transaction tx = Transaction.openRoot()) {
                this.insert(prod, tx);
                currentlyPowered = (this.getAmountAsLong() >= cons);
                if (cons > 0) {
                    this.extract(currentlyPowered ? cons : (int) getAmountAsLong(), tx);
                }
                tx.commit();
            }
        } else {

            currentlyPowered = (cons == 0) || (prod >= cons);
        }

        if (this.isPowered != currentlyPowered) {
            this.isPowered = currentlyPowered;
            recalculateCapacity(player);
        }
        syncToClient(player);
    }

    public int getTolerance(LivingEntity entity) {
        int consumed = 0;
        for (int i = 0; i < installedCyberware.size(); i++) {
            ItemStack stack = getStack(i);
            ICyberware cyberware = CyberwareAPI.getCyberware(stack);
            if (cyberware != null) consumed += cyberware.getEssenceCost(stack) * stack.getCount();
        }
        CyberwareToleranceEvent event = new CyberwareToleranceEvent(entity, this.maxTolerance);
        NeoForge.EVENT_BUS.post(event);
        return event.getNewTolerance() - consumed;
    }

    public void syncToClient(ServerPlayer player) {
        if (player == null) return;
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
        this.serialize(output);
        CompoundTag tag = output.buildResult();
        int effectiveMax = getTolerance(player) + (maxTolerance - getTolerance(player));
        tag.putInt("MaxTolerance", effectiveMax);
        PacketDistributor.sendToPlayer(player, new SyncCyberwareDataPacket(tag, player.getId()));
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
        setStack(RobosurgeonBlockEntity.SLOT_BRAIN, new ItemStack(ModItems.HUMAN_BRAIN.get()));
        setStack(RobosurgeonBlockEntity.SLOT_HEART, new ItemStack(ModItems.HUMAN_HEART.get()));
        setStack(RobosurgeonBlockEntity.SLOT_STOMACH, new ItemStack(ModItems.HUMAN_STOMACH.get()));
        setStack(RobosurgeonBlockEntity.SLOT_SKIN, new ItemStack(ModItems.HUMAN_SKIN.get()));
        setStack(RobosurgeonBlockEntity.SLOT_MUSCLE, new ItemStack(ModItems.HUMAN_MUSCLE.get()));
        setStack(RobosurgeonBlockEntity.SLOT_BONES, new ItemStack(ModItems.HUMAN_BONE.get()));
        setStack(RobosurgeonBlockEntity.SLOT_EYES, new ItemStack(ModItems.HUMAN_EYES.get()));
        setStack(RobosurgeonBlockEntity.SLOT_LUNGS, new ItemStack(ModItems.HUMAN_LUNGS.get()));
        setStack(RobosurgeonBlockEntity.SLOT_ARMS, new ItemStack(ModItems.HUMAN_LEFT_ARM.get()));
        setStack(RobosurgeonBlockEntity.SLOT_ARMS + 1, new ItemStack(ModItems.HUMAN_RIGHT_ARM.get()));
        setStack(RobosurgeonBlockEntity.SLOT_HANDS, new ItemStack(ModItems.HUMAN_RIGHT_HAND.get()));
        setStack(RobosurgeonBlockEntity.SLOT_HANDS + 1, new ItemStack(ModItems.HUMAN_LEFT_HAND.get()));
        setStack(RobosurgeonBlockEntity.SLOT_LEGS, new ItemStack(ModItems.HUMAN_LEFT_LEG.get()));
        setStack(RobosurgeonBlockEntity.SLOT_LEGS + 1, new ItemStack(ModItems.HUMAN_RIGHT_LEG.get()));
        setStack(RobosurgeonBlockEntity.SLOT_BOOTS, new ItemStack(ModItems.HUMAN_RIGHT_FOOT.get()));
        setStack(RobosurgeonBlockEntity.SLOT_BOOTS + 1, new ItemStack(ModItems.HUMAN_LEFT_FOOT.get()));
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
            setStack(RobosurgeonBlockEntity.SLOT_BRAIN, new ItemStack(ModItems.HUMAN_BRAIN.get()));
        if (!status.hasPart(BodyPartType.HEART))
            setStack(RobosurgeonBlockEntity.SLOT_HEART, new ItemStack(ModItems.HUMAN_HEART.get()));
        if (!status.hasPart(BodyPartType.MUSCLE))
            setStack(RobosurgeonBlockEntity.SLOT_MUSCLE, new ItemStack(ModItems.HUMAN_MUSCLE.get()));
        if (!status.hasPart(BodyPartType.BONES))
            setStack(RobosurgeonBlockEntity.SLOT_BONES, new ItemStack(ModItems.HUMAN_BONE.get()));
    }

    public void resetToHuman() {
        for (int i = 0; i < installedCyberware.size(); i++) setStack(i, ItemStack.EMPTY);
        this.isInitialized = false;
        currentEnergy = 0;
        fillWithHumanParts();
    }

    public void copyFrom(CyberwareUserData other) {
        for (int i = 0; i < this.installedCyberware.size(); i++)
            setStack(i, other.getStack(i).copy());
        this.maxTolerance = other.maxTolerance;
        this.currentEnergy = other.currentEnergy;
        this.maxEnergy = other.maxEnergy;
        this.isInitialized = other.isInitialized;
    }


    public boolean isInitialized() {
        return isInitialized;
    }

    public void setRespawnGracePeriod(int ticks) {
        this.respawnGracePeriod = ticks;
    }

    public ItemStacksResourceHandler getInstalledCyberware() {
        return installedCyberware;
    }

    public boolean isCyberwareInstalled(Item item) {
        for (int i = 0; i < installedCyberware.size(); i++)
            if (getStack(i).is(item)) return true;
        return false;
    }

    @Override
    public void serialize(ValueOutput output) {
        this.installedCyberware.serialize(output);
        output.putInt("MaxTolerance", maxTolerance);
        output.putBoolean("IsInitialized", isInitialized);
        output.putInt("ImmunityTime", toleranceImmunityTime);
        output.putInt("MaxEnergy", maxEnergy);
        output.putInt("CurrentEnergy", currentEnergy);
        output.putInt("LastProd", lastProduction);
        output.putInt("LastCons", lastConsumption);

        output.putInt("EmpTicks", empTicks);
        output.putBoolean("IsPowered", isPowered);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.installedCyberware.deserialize(input);
        this.maxTolerance = input.getIntOr("MaxTolerance", 100);
        this.isInitialized = input.getBooleanOr("IsInitialized", false);
        this.toleranceImmunityTime = input.getIntOr("ImmunityTime", 0);
        this.maxEnergy = input.getIntOr("MaxEnergy", 0);
        this.currentEnergy = input.getIntOr("CurrentEnergy", 0);
        this.lastProduction = input.getIntOr("LastProd", 0);
        this.lastConsumption = input.getIntOr("LastCons", 0);

        this.empTicks = input.getIntOr("EmpTicks", 0);
        this.isPowered = input.getBooleanOr("IsPowered", true);
        updateBodyStatus();
    }

    @Override
    public long getAmountAsLong() {
        return (long) this.currentEnergy;
    }

    @Override
    public long getCapacityAsLong() {
        return (long) this.maxEnergy;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        if (amount <= 0) return 0;
        int inserted = Math.min(amount, this.maxEnergy - this.currentEnergy);
        if (inserted > 0) {
            this.updateSnapshots(transaction);
            this.currentEnergy += inserted;
        }
        return inserted;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        if (amount <= 0) return 0;
        int extracted = Math.min(this.currentEnergy, amount);
        if (extracted > 0) {
            this.updateSnapshots(transaction);
            this.currentEnergy -= extracted;
        }
        return extracted;
    }

    @Override
    protected Integer createSnapshot() {
        return this.currentEnergy;
    }

    @Override
    protected void revertToSnapshot(Integer snapshot) {
        this.currentEnergy = snapshot;
    }

    public int getEnergyStored() {
        return currentEnergy;
    }

    public int getMaxEnergyStored() {
        return maxEnergy;
    }

    public boolean consumeEnergy(int amount) {
        if (amount <= 0) return true;
        try (Transaction tx = Transaction.openRoot()) {
            int extracted = this.extract(amount, tx);
            if (extracted >= amount) {
                tx.commit();
                return true;
            }
        }
        return false;
    }

    public int extractEnergy(int amount, boolean simulate) {
        if (simulate) {
            return Math.min(this.currentEnergy, amount);
        } else {
            try (Transaction tx = Transaction.openRoot()) {
                int extracted = this.extract(amount, tx);
                tx.commit();
                return extracted;
            }
        }
    }

    public int receiveEnergy(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        if (simulate) {
            return Math.min(amount, this.maxEnergy - this.currentEnergy);
        } else {
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = this.insert(amount, tx);
                tx.commit();
                return inserted;
            }
        }
    }
}