package com.maxwell.cyber_ware_port.common.item.base;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CyberwareItem extends Item implements ICyberware {
    private final int essenceCost;
    private final int slotId;
    private final int maxInstallAmount;
    private final boolean hasEnergyProperties;
    private final int energyConsumption;
    private final int energyGeneration;
    private final int quality;
    private final int eventConsumption;
    private final int energyStorage;
    private final StackingRule stackingRule;
    private final Set<Supplier<? extends Item>> incompatibleRegistryObjects;
    private final Set<Supplier<? extends Item>> prerequisiteRegistryObjects;
    private final BodyPartType bodyPartType;
    private final Multimap<Holder<Attribute>, AttributeModifier> baseAttributeModifiers;

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
        return stack.getOrDefault(ModDataComponents.PRISTINE.get(), true);
    }

    @Override
    public void setPristine(ItemStack stack, boolean isPristine) {
        stack.set(ModDataComponents.PRISTINE.get(), isPristine);
    }

    @Override
    public int getMaxInstallAmount(ItemStack stack) {
        return this.maxInstallAmount;
    }

    @Override
    public Set<Item> getPrerequisites(ItemStack stack) {
        return this.prerequisiteRegistryObjects.stream().map(Supplier::get).collect(Collectors.toSet());
    }

    @Override
    public Set<Item> getIncompatibleItems(ItemStack stack) {
        return this.incompatibleRegistryObjects.stream().map(Supplier::get).collect(Collectors.toSet());
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

    @Override
    public int getEnergyStorage(ItemStack stack) {
        int base = this.energyStorage;
        return isPristine(stack) ? base : base / 2;
    }

    public boolean tryConsumeEventEnergy(IEnergyStorage energyStorage, ItemStack stack) {
        int cost = this.getEventConsumption(stack);
        if (cost <= 0) return true;
        if (energyStorage.extractEnergy(cost, true) == cost) {
            energyStorage.extractEnergy(cost, false);
            return true;
        }
        return false;
    }

    @Override
    public StackingRule getStackingEnergyRule(ItemStack stack) {
        return this.stackingRule;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        if (isPristine(stack)) {
            return this.baseAttributeModifiers;
        }
        Multimap<Holder<Attribute>, AttributeModifier> modified = ArrayListMultimap.create();
        this.baseAttributeModifiers.forEach((attr, mod) -> {
            double newValue = mod.amount() * 0.5;
            AttributeModifier newMod = new AttributeModifier(
                    mod.id().withSuffix("_damaged"),
                    newValue,
                    mod.operation()
            );
            modified.put(attr, newMod);
        });
        return modified;
    }

    @Override
    public Component getName(ItemStack stack) {
        ChatFormatting style = isPristine(stack) ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY;
        return Component.translatable(this.getDescriptionId()).withStyle(style);
    }

    public static class Builder {
        private final Properties properties = new Properties();
        private final int essenceCost;
        private final int slotId;
        private final Set<Supplier<? extends Item>> prerequisites = new HashSet<>();
        private final Set<Supplier<? extends Item>> incompatibleItems = new HashSet<>();
        private final Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers = ArrayListMultimap.create();
        private int maxInstallAmount = 1;
        private boolean hasEnergyProperties = false;
        private int energyConsumption = 0;
        private int energyGeneration = 0;
        private int energyStorage = 0;
        private int eventConsumption = 0;
        private int quality = 1;
        private StackingRule stackingRule = StackingRule.LINEAR;
        private BodyPartType bodyPartType = BodyPartType.NONE;

        public Builder(int essenceCost, int slotId) {
            this.essenceCost = essenceCost;
            this.slotId = slotId;
        }

        public Builder quality(int quality) {
            this.quality = quality;
            return this;
        }

        public Builder bodyPart(BodyPartType type) {
            this.bodyPartType = type;
            return this;
        }

        public Builder maxInstall(int amount) {
            this.maxInstallAmount = amount;
            return this;
        }

        public Builder eventCost(int cost) {
            this.eventConsumption = cost;
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
        public final Builder requires(Supplier<? extends Item>... items) {
            Collections.addAll(this.prerequisites, items);
            return this;
        }

        @SafeVarargs
        public final Builder incompatible(Supplier<? extends Item>... items) {
            Collections.addAll(this.incompatibleItems, items);
            return this;
        }

        public Builder addAttribute(Holder<Attribute> attribute, String idStr, double amount, AttributeModifier.Operation operation) {
            this.attributeModifiers.put(attribute, new AttributeModifier(Identifier.fromNamespaceAndPath(CyberWare.MODID, idStr), amount, operation));
            return this;
        }

        public Builder rarity(net.minecraft.world.item.Rarity rarity) {
            this.properties.rarity(rarity);
            return this;
        }

        public CyberwareItem build() {
            this.properties.stacksTo(Math.max(this.maxInstallAmount, 1));
            return new CyberwareItem(this);
        }
    }
}