package com.maxwell.cyber_ware_port.api.json;

import com.google.common.collect.Multimap;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class DynamicCyberwareWrapper implements ICyberware {
    private final CyberwareData data;

    public DynamicCyberwareWrapper(CyberwareData data) {
        this.data = data;
    }

    @Override
    public int getEssenceCost(ItemStack stack) {
        return data.essence;
    }

    @Override
    public int getSlot(ItemStack stack) {
        return data.slotId;
    }

    @Override
    public boolean isPristine(ItemStack stack) {
        return data.isPristine;
    }

    @Override
    public void setPristine(ItemStack stack, boolean isPristine) {
    }

    @Override
    public int getMaxInstallAmount(ItemStack stack) {
        return data.maxInstall;
    }

    @Override
    public Set<Item> getPrerequisites(ItemStack stack) {
        return data.prerequisites;
    }

    @Override
    public Set<Item> getIncompatibleItems(ItemStack stack) {
        return data.incompatibleItems;
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        return data.attributeModifiers;
    }

    @Override
    public boolean hasEnergyProperties(ItemStack stack) {
        return data.hasEnergyProperties;
    }

    @Override
    public StackingRule getStackingEnergyRule(ItemStack stack) {
        return data.stackingRule;
    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        int base = data.energyConsumption;
        return isPristine(stack) ? base : base * 2;
    }

    @Override
    public int getEnergyGeneration(ItemStack stack) {
        int base = data.energyGeneration;
        return isPristine(stack) ? base : base / 2;
    }

    @Override
    public int getEnergyStorage(ItemStack stack) {
        int base = data.energyStorage;
        return isPristine(stack) ? base : base / 2;
    }
}