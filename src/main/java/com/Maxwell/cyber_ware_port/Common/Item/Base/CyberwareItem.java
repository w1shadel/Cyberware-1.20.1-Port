package com.Maxwell.cyber_ware_port.Common.Item.Base;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashSet;

public class CyberwareItem extends Item implements ICyberware {

    private static final String NBT_KEY_PRISTINE = "IsPristine";

    private final int essenceCost;
    private final int slotId;
    private final int maxInstallAmount;
    private final boolean hasEnergyProperties;
    private final int energyConsumption;
    private final int energyGeneration;
    private final int energyStorage;
    private final StackingRule stackingRule;

    private final Set<RegistryObject<Item>> incompatibleRegistryObjects;
    private final Set<RegistryObject<Item>> prerequisiteRegistryObjects;

    private final BodyPartType bodyPartType;

    public CyberwareItem(Builder builder) {
        super(builder.properties);
        this.essenceCost = builder.essenceCost;
        this.slotId = builder.slotId;
        this.maxInstallAmount = builder.maxInstallAmount;
        this.hasEnergyProperties = builder.hasEnergyProperties;
        this.energyConsumption = builder.energyConsumption;
        this.energyGeneration = builder.energyGeneration;
        this.energyStorage = builder.energyStorage;
        this.stackingRule = builder.stackingRule;
        this.prerequisiteRegistryObjects = Set.copyOf(builder.prerequisites);
        this.incompatibleRegistryObjects = Set.copyOf(builder.incompatibleItems);

        this.bodyPartType = builder.bodyPartType;
    }
    @Override
    public int getEssenceCost(ItemStack stack) {
        return this.essenceCost;
    }
    @Override
    public BodyPartType getBodyPartType(ItemStack stack) {
        return this.bodyPartType;
    }

    @Override
    public int getSlot(ItemStack stack) {
        return this.slotId;
    }

    @Override
    public boolean isPristine(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt == null || !nbt.contains(NBT_KEY_PRISTINE) || nbt.getBoolean(NBT_KEY_PRISTINE);
    }

    @Override
    public void setPristine(ItemStack stack, boolean isPristine) {
        if (isPristine) {
            if (stack.hasTag()) {
                CompoundTag tag = stack.getTag();
                tag.remove(NBT_KEY_PRISTINE);
                if (tag.isEmpty()) stack.setTag(null);
            }
        } else {
            stack.getOrCreateTag().putBoolean(NBT_KEY_PRISTINE, false);
        }
    }

    @Override
    public int getMaxInstallAmount(ItemStack stack) {
        return this.maxInstallAmount;
    }

    @Override
    public Set<Item> getPrerequisites(ItemStack stack) {

        return this.prerequisiteRegistryObjects.stream()
                .map(RegistryObject::get)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Item> getIncompatibleItems(ItemStack stack) {

        return this.incompatibleRegistryObjects.stream()
                .map(RegistryObject::get)
                .collect(Collectors.toSet());
    }@Override
    public boolean isIncompatible(ItemStack self, ItemStack other) {
        if (self.getItem() == other.getItem()) {
            return this.getMaxInstallAmount(self) <= 1;
        }

        return getIncompatibleItems(self).contains(other.getItem());
    }

    @Override
    public boolean hasEnergyProperties(ItemStack stack) {
        return this.hasEnergyProperties;
    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        // 中古なら消費量 2倍
        int base = this.energyConsumption;
        return isPristine(stack) ? base : base * 2;
    }

    @Override
    public int getEnergyGeneration(ItemStack stack) {
        // 中古なら発電量 半分
        int base = this.energyGeneration;
        return isPristine(stack) ? base : base / 2;
    }

    @Override
    public int getEnergyStorage(ItemStack stack) {
        // 中古なら蓄電量 半分
        int base = this.energyStorage;
        return isPristine(stack) ? base : base / 2;
    }
    @Override
    public Component getName(ItemStack stack) {
        // アイテムの基本名を取得
        Component baseName = super.getName(stack);

        // 中古の場合は名前を「濃い灰色」にする
        if (!isPristine(stack)) {
            // "Scavenged Cyber Eyes" のように接頭辞を付けたい場合はここで設定可能
            // 今回は色変えのみ行います（接頭辞は言語ファイル等で調整も可）
            return Component.translatable(this.getDescriptionId(stack))
                    .withStyle(ChatFormatting.DARK_GRAY);
        }

        // 新品の場合はシアン色（レアっぽく）
        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(ChatFormatting.AQUA);
    }

    @Override
    public StackingRule getStackingEnergyRule(ItemStack stack) {
        return this.stackingRule;
    }

    public static class Builder {

        private final Properties properties;
        private final int essenceCost;
        private final int slotId;

        private int maxInstallAmount = 1;
        private boolean hasEnergyProperties = false;
        private int energyConsumption = 0;
        private int energyGeneration = 0;
        private int energyStorage = 0;
        private StackingRule stackingRule = StackingRule.LINEAR;
        private final Set<RegistryObject<Item>> prerequisites = new HashSet<>();
        private final Set<RegistryObject<Item>> incompatibleItems = new HashSet<>();

        private BodyPartType bodyPartType = BodyPartType.NONE;

        public Builder(int essenceCost, int slotId) {
            this.properties = new Properties();
            this.essenceCost = essenceCost;
            this.slotId = slotId;
        }

        /**
         * このアイテムが果たす身体機能を設定します。
         * (例: 人工心臓なら .bodyPart(BodyPartType.HEART))
         */
        public Builder bodyPart(BodyPartType type) {
            this.bodyPartType = type;
            return this;
        }

        public Builder maxInstall(int amount) {
            this.maxInstallAmount = amount;
            return this;
        }

        public Builder energy(int consumption, int generation, int storage, StackingRule rule) {
            this.hasEnergyProperties = true;
            this.energyConsumption = consumption;
            this.energyGeneration = generation;
            this.energyStorage = storage;
            this.stackingRule = rule;
            return this;
        }

        @SafeVarargs
        public final Builder requires(RegistryObject<Item>... items) {
            Collections.addAll(this.prerequisites, items);
            return this;
        }

        public Builder properties(java.util.function.Consumer<Properties> consumer) {
            consumer.accept(this.properties);
            return this;
        }

        @SafeVarargs
        public final Builder incompatible(RegistryObject<Item>... items) {
            Collections.addAll(this.incompatibleItems, items);
            return this;
        }

        public CyberwareItem build() {
            this.properties.stacksTo(Math.max(this.maxInstallAmount, 1));
            return new CyberwareItem(this);
        }
    }
}