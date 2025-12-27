package com.maxwell.cyber_ware_port.common.risk;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.ChatFormatting;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class SurgeryAnalyzer {

    public static SurgeryAlert check(List<Slot> slots, int maxTolerance) {
        Set<BodyPartType> futureParts = EnumSet.noneOf(BodyPartType.class);
        List<ItemStack> futureItems = new java.util.ArrayList<>();
        int projectedCost = 0;
        int armCount = 0;
        int legCount = 0;
        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            if (i >= slots.size()) break;
            ItemStack stack = slots.get(i).getItem();
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ICyberware cyberware) {
                projectedCost += cyberware.getEssenceCost(stack) * stack.getCount();
                BodyPartType type = cyberware.getBodyPartType(stack);
                if (type != BodyPartType.NONE) {
                    futureParts.add(type);
                }
                futureItems.add(stack);
                int targetSlot = cyberware.getSlot(stack);
                if (targetSlot == RobosurgeonBlockEntity.SLOT_ARMS || targetSlot == RobosurgeonBlockEntity.SLOT_ARMS + 1) {
                    armCount++;
                } else if (targetSlot == RobosurgeonBlockEntity.SLOT_LEGS || targetSlot == RobosurgeonBlockEntity.SLOT_LEGS + 1) {
                    legCount++;
                }
            }
        }
        for (ItemStack stack : futureItems) {
            if (stack.getItem() instanceof ICyberware cw) {
                for (net.minecraft.world.item.Item req : cw.getPrerequisites(stack)) {
                    boolean found = false;
                    for (ItemStack other : futureItems) {
                        if (other.getItem() == req) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        return SurgeryAlert.create("cyberware.risk.missing_requirement", ChatFormatting.RED);
                    }
                }
            }
        }
        int remainingTolerance = maxTolerance - projectedCost;
        if (!futureParts.contains(BodyPartType.BRAIN)) {
            return SurgeryAlert.create("cyberware.risk.missing_brain", ChatFormatting.RED);
        }
        if (!futureParts.contains(BodyPartType.HEART)) {
            return SurgeryAlert.create("cyberware.risk.missing_heart", ChatFormatting.RED);
        }
        if (!futureParts.contains(BodyPartType.MUSCLE)) {
            return SurgeryAlert.create("cyberware.risk.missing_muscle", ChatFormatting.RED);
        }
        if (remainingTolerance <= 0) {
            return SurgeryAlert.create("cyberware.risk.zero_tolerance", ChatFormatting.RED);
        }
        if (!futureParts.contains(BodyPartType.LUNGS)) {
            return SurgeryAlert.create("cyberware.risk.missing_lungs", ChatFormatting.GOLD);
        }
        if (legCount == 0) {
            return SurgeryAlert.create("cyberware.risk.missing_legs_both", ChatFormatting.GOLD);
        }
        if (!futureParts.contains(BodyPartType.SKIN)) {
            return SurgeryAlert.create("cyberware.risk.missing_skin", ChatFormatting.YELLOW);
        }
        if (!futureParts.contains(BodyPartType.BONES)) {
            return SurgeryAlert.create("cyberware.risk.missing_bones", ChatFormatting.RED);
        }
        if (!futureParts.contains(BodyPartType.EYES)) {
            return SurgeryAlert.create("cyberware.risk.missing_eyes", ChatFormatting.YELLOW);
        }
        if (armCount == 0) {
            return SurgeryAlert.create("cyberware.risk.missing_arm_left", ChatFormatting.YELLOW);
        }
        if (armCount == 1) {
            return SurgeryAlert.create("cyberware.risk.missing_arm_right", ChatFormatting.YELLOW);
        }
        if (legCount == 1) {
            return SurgeryAlert.create("cyberware.risk.missing_leg_single", ChatFormatting.YELLOW);
        }
        if (!futureParts.contains(BodyPartType.STOMACH)) {
            return SurgeryAlert.create("cyberware.risk.missing_stomach", ChatFormatting.YELLOW);
        }
        int dangerThreshold = (int) (maxTolerance * 0.25f);
        if (remainingTolerance < dangerThreshold) {
            return SurgeryAlert.create("cyberware.risk.low_tolerance", ChatFormatting.YELLOW);
        }
        return null;
    }
}