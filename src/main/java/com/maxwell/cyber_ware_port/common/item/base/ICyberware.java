package com.maxwell.cyber_ware_port.common.item.base;

import com.google.common.collect.Multimap;
import com.maxwell.cyber_ware_port.init.ModDataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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

    default int getEventConsumption(ItemStack stack) {
        return 0;
    }

    int getEnergyGeneration(ItemStack stack);

    int getEnergyStorage(ItemStack stack);

    StackingRule getStackingEnergyRule(ItemStack stack);

    default boolean isIncompatible(ItemStack self, ItemStack other) {
        if (self.getItem() == other.getItem()) {
            return this.getMaxInstallAmount(self) <= 1;
        }
        return getIncompatibleItems(self).contains(other.getItem());
    }

    default BodyPartType getBodyPartType(ItemStack stack) {
        return BodyPartType.NONE;
    }

    default boolean canToggle(ItemStack stack) {
        return false;
    }

    default boolean isActive(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ACTIVE.get(), true);
    }

    default int getQuality(ItemStack stack) {
        return 1;
    }

    default void toggle(ItemStack stack) {
        stack.set(ModDataComponents.ACTIVE.get(), !isActive(stack));
    }

    Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack);

    default void onPlayerTick(PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
    }

    default void onItemUseTick(LivingEntityUseItemEvent.Tick event, ItemStack stack, LivingEntity wearer) {
    }

    default void onLivingIncomingDamage(LivingIncomingDamageEvent event, ItemStack stack, LivingEntity wearer) {
    }

    default void onLivingDamagePre(LivingDamageEvent.Pre event, ItemStack stack, LivingEntity wearer) {
    }

    default void onEntityTeleport(EntityTeleportEvent event, ItemStack stack, LivingEntity wearer) {
    }

    default void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, ItemStack stack, LivingEntity wearer) {
    }

    default void onPotionApplicable(MobEffectEvent.Applicable event, ItemStack stack, LivingEntity wearer) {
    }

    default void onLivingDeath(LivingDeathEvent event, ItemStack stack, LivingEntity wearer) {
    }

    default void onHarvestCheck(PlayerEvent.HarvestCheck event, ItemStack stack, LivingEntity wearer) {
    }

    default void onBreakSpeed(PlayerEvent.BreakSpeed event, ItemStack stack, LivingEntity wearer) {
    }

    default void onLivingFall(LivingFallEvent event, ItemStack stack, LivingEntity wearer) {
    }

    default void onLivingJump(LivingEvent.LivingJumpEvent event, ItemStack stack, LivingEntity wearer) {
    }

    default void onSystemTick(LivingEntity entity, ItemStack stack) {
    }

    default void onInstalled(LivingEntity entity, ItemStack stack) {
    }

    default void onRemoved(LivingEntity entity, ItemStack stack) {
    }

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
}