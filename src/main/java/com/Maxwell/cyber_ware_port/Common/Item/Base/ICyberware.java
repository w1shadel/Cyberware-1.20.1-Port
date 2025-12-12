package com.Maxwell.cyber_ware_port.Common.Item.Base;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

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
        return !stack.hasTag() || !stack.getTag().contains("active") || stack.getTag().getBoolean("active");
    }

    default void toggle(ItemStack stack) {
        boolean currentState = isActive(stack);
        stack.getOrCreateTag().putBoolean("active", !currentState);
    }

    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        return com.google.common.collect.ArrayListMultimap.create();
    }

    default void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
    }

    default void onItemUseTick(LivingEntityUseItemEvent.Tick event, ItemStack stack, LivingEntity wearer) {
    }

    default void onLivingAttack(LivingAttackEvent event, ItemStack stack, LivingEntity wearer) {
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