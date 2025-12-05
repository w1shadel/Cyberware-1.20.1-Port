package com.Maxwell.cyber_ware_port.Common.Item.Base;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Collections;
import java.util.Set;

public interface ICyberware {

    int getEssenceCost(ItemStack stack);
    int getSlot(ItemStack stack);
    boolean isPristine(ItemStack stack);
    void setPristine(ItemStack stack, boolean isPristine);
    int getMaxInstallAmount(ItemStack stack);

    Set<Item> getPrerequisites(ItemStack stack);

    default Set<Item> getIncompatibleItems(ItemStack stack) {
        return Collections.emptySet();
    }

    boolean hasEnergyProperties(ItemStack stack);
    int getEnergyConsumption(ItemStack stack);
    int getEnergyGeneration(ItemStack stack);
    int getEnergyStorage(ItemStack stack);
    StackingRule getStackingEnergyRule(ItemStack stack);

    enum StackingRule {
        LINEAR, DIMINISHING, STATIC;
        public int calculate(int baseCost, int count) {
            return switch (this) {
                case LINEAR -> baseCost * count;
                case DIMINISHING -> (int) (baseCost * (1 + Math.log(count)));
                case STATIC -> count > 0 ? baseCost : 0;
            };
        }
    }

    /**
     * 競合チェックロジック
     * デフォルト実装では getIncompatibleItems のリストに含まれているかを確認します。
     */
    default boolean isIncompatible(ItemStack self, ItemStack other) {
        if (self.getItem() == other.getItem()) {
            return this.getMaxInstallAmount(self) <= 1; 
        }

        if (getIncompatibleItems(self).contains(other.getItem())) {
            return true;
        }

        return false;
    }
    default BodyPartType getBodyPartType(ItemStack stack) {
        return BodyPartType.NONE;
    }
    default void onWornTick(LivingEntity entity, ItemStack stack, IEnergyStorage energyStorage) {}
    default boolean canToggle(ItemStack stack) { return false; }
    default boolean isActive(ItemStack stack) {
        return !stack.hasTag() || !stack.getTag().contains("active") || stack.getTag().getBoolean("active");
    }
    default void toggle(ItemStack stack) {
        boolean currentState = isActive(stack);
        stack.getOrCreateTag().putBoolean("active", !currentState);
    }
    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        return com.google.common.collect.ArrayListMultimap.create();
    }
}