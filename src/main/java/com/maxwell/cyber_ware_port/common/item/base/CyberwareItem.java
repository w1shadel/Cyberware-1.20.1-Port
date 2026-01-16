package com.maxwell.cyber_ware_port.common.item.base;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CyberwareItem extends Item implements ICyberware {
    private static final String NBT_KEY_PRISTINE = "IsPristine";
    // =====================================================================================
    //  DOCUMENTATION CONSTANTS (Protected from regex deletion)
    // =====================================================================================
    private static final String DOC_essenceCost = "Essence cost to install this cyberware.";
    private final int essenceCost;
    private static final String DOC_slotId = "The internal inventory slot ID (0-based) where this item belongs.";
    private final int slotId;
    private static final String DOC_maxInstallAmount = "Maximum number of this item that can be installed in the same slot.";
    private final int maxInstallAmount;
    private static final String DOC_hasEnergyProperties = "Whether this cyberware uses or generates energy (Forge Energy).";
    private final boolean hasEnergyProperties;
    private static final String DOC_energyConsumption = "Amount of energy consumed per tick or operation.";
    private final int energyConsumption;
    private static final String DOC_energyGeneration = "Amount of energy generated per tick.";
    private final int energyGeneration;
    private static final String DOC_quality = "Priority level for installation exclusivity. 0=Human parts (Lowest), 1=Standard Cyberware, 2+=High-tier (Overrides lower).";
    private final int quality;
    private static final String DOC_eventConsumption = "Energy cost for special events or active abilities.";
    private final int eventConsumption;
    private static final String DOC_energyStorage = "Internal energy storage capacity of this item.";
    private final int energyStorage;
    private static final String DOC_stackingRule = "Logic for how energy properties stack when multiple items are installed.";
    private final StackingRule stackingRule;
    private static final String DOC_incompatible = "List of items that cannot be installed alongside this one.";
    private final Set<RegistryObject<Item>> incompatibleRegistryObjects;
    private static final String DOC_prerequisite = "List of items required before this one can be installed.";
    private final Set<RegistryObject<Item>> prerequisiteRegistryObjects;
    private static final String DOC_bodyPartType = "Specific body part type (e.g., ARM_LEFT, LEG_RIGHT) for precise exclusivity checks.";
    private final BodyPartType bodyPartType;
    private static final String DOC_modifiers = "Attribute modifiers (e.g., strength boost) applied when installed.";
    private final Multimap<Attribute, AttributeModifier> baseAttributeModifiers;

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
        this.baseAttributeModifiers = builder.attributeModifiers;
        this.eventConsumption = builder.eventConsumption;
        this.quality = builder.quality;
    }

    @Override
    public int getEssenceCost(ItemStack stack) {
        return this.essenceCost;
    }

    @Override
    public int getQuality(ItemStack stack) {
        return this.quality;
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
    }

    @Override
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
        int base = this.energyConsumption;
        return isPristine(stack) ? base : base * 2;
    }

    @Override
    public int getEventConsumption(ItemStack stack) {
        int base = this.eventConsumption;
        return isPristine(stack) ? base : base * 2;
    }

    @Override
    public int getEnergyGeneration(ItemStack stack) {
        int base = this.energyGeneration;
        return isPristine(stack) ? base : base / 2;
    }

    public boolean tryConsumeEventEnergy(net.minecraftforge.energy.IEnergyStorage energyStorage, ItemStack stack) {
        int cost = this.getEventConsumption(stack);
        if (cost <= 0) return true;
        if (energyStorage.extractEnergy(cost, true) == cost) {
            energyStorage.extractEnergy(cost, false);
            return true;
        }
        return false;
    }

    @Override
    public int getEnergyStorage(ItemStack stack) {
        int base = this.energyStorage;
        return isPristine(stack) ? base : base / 2;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (!isPristine(stack)) {
            return Component.translatable(this.getDescriptionId(stack))
                    .withStyle(ChatFormatting.DARK_GRAY);
        }
        return Component.translatable(this.getDescriptionId(stack))
                .withStyle(ChatFormatting.AQUA);
    }

    @Override
    public StackingRule getStackingEnergyRule(ItemStack stack) {
        return this.stackingRule;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        if (isPristine(stack)) {
            return this.baseAttributeModifiers;
        }
        Multimap<Attribute, AttributeModifier> modified = ArrayListMultimap.create();
        this.baseAttributeModifiers.forEach((attr, mod) -> {
            double newValue = mod.getAmount() * 0.5;
            AttributeModifier newMod = new AttributeModifier(
                    mod.getId(),
                    mod.getName() + " (Damaged)",
                    newValue,
                    mod.getOperation()
            );
            modified.put(attr, newMod);
        });
        return modified;
    }

    @Override
    public void onSystemTick(LivingEntity entity, ItemStack stack) {
    }

    public static class Builder {
        private final Properties properties;
        private final int essenceCost;
        private final int slotId;
        private final Set<RegistryObject<Item>> prerequisites = new HashSet<>();
        private final Set<RegistryObject<Item>> incompatibleItems = new HashSet<>();
        private final Multimap<Attribute, AttributeModifier> attributeModifiers = ArrayListMultimap.create();
        private int maxInstallAmount = 1;
        private boolean hasEnergyProperties = false;
        private int energyConsumption = 0;
        private int energyGeneration = 0;
        private int energyStorage = 0;
        private int eventConsumption = 0;
        // protected documentation strings
        private static final String DOC_quality = "Default quality is 1 (Standard Cyberware). Set to 0 for Human parts.";
        private int quality = 1;
        private StackingRule stackingRule = StackingRule.LINEAR;
        private static final String DOC_bodyPartType = "Default is NONE. Needs to be set for Limbs (ARM_LEFT, etc.).";
        private BodyPartType bodyPartType = BodyPartType.NONE;

        public Builder(int essenceCost, int slotId) {
            this.properties = new Properties();
            this.essenceCost = essenceCost;
            this.slotId = slotId;
        }

        private static final String DOC_method_quality = "Sets the priority quality. Higher quality items replace lower quality items in the same body slot.";

        public Builder quality(int quality) {
            this.quality = quality;
            return this;
        }

        private static final String DOC_method_bodyPart = "Defines the specific body part type (e.g., ARM_LEFT, LEG_RIGHT). Essential for exclusivity.";

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

        public Builder eventCost(int cost) {
            this.eventConsumption = cost;
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

        public Builder addAttribute(Attribute attribute, String uuidStr, double amount, AttributeModifier.Operation operation) {
            this.attributeModifiers.put(attribute, new AttributeModifier(UUID.fromString(uuidStr), "Cyberware modifier", amount, operation));
            return this;
        }

        public CyberwareItem build() {
            this.properties.stacksTo(Math.max(this.maxInstallAmount, 1));
            return new CyberwareItem(this);
        }
    }
}