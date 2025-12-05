package com.Maxwell.cyber_ware_port.Common.Risk;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import net.minecraft.ChatFormatting;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class SurgeryAnalyzer {

    /**
     * 手術台をスキャンし、最も優先度の高い警告を1つだけ返します。
     * 問題がなければ null を返します。
     */
    public static SurgeryAlert check(List<Slot> slots, int maxTolerance) {

        Set<BodyPartType> futureParts = EnumSet.noneOf(BodyPartType.class);
        int projectedCost = 0;

        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            if (i >= slots.size()) break;
            ItemStack stack = slots.get(i).getItem();

            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cyberware) {
                projectedCost += cyberware.getEssenceCost(stack) * stack.getCount();

                BodyPartType type = cyberware.getBodyPartType(stack);
                if (type != BodyPartType.NONE) {
                    futureParts.add(type);
                }
            }
        }

        int remainingTolerance = maxTolerance - projectedCost;if (!futureParts.contains(BodyPartType.BRAIN)) {
            return SurgeryAlert.create("cyberware.risk.missing_brain", ChatFormatting.RED);
        }

        if (!futureParts.contains(BodyPartType.HEART)) {
            return SurgeryAlert.create("cyberware.risk.missing_heart", ChatFormatting.RED);
        }

        if (remainingTolerance <= 0) {
            return SurgeryAlert.create("cyberware.risk.zero_tolerance", ChatFormatting.RED);
        }

        if (!futureParts.contains(BodyPartType.LUNGS)) {
            return SurgeryAlert.create("cyberware.risk.missing_lungs", ChatFormatting.GOLD);
        }

        if (!futureParts.contains(BodyPartType.SKIN)) {
            return SurgeryAlert.create("cyberware.risk.missing_skin", ChatFormatting.GOLD);
        }

        if (!futureParts.contains(BodyPartType.STOMACH)) {
            return SurgeryAlert.create("cyberware.risk.missing_stomach", ChatFormatting.YELLOW);
        }

        if (!futureParts.contains(BodyPartType.MUSCLE)) {
            return SurgeryAlert.create("cyberware.risk.missing_muscle", ChatFormatting.YELLOW);
        }

        if (!futureParts.contains(BodyPartType.BONES)) {
            return SurgeryAlert.create("cyberware.risk.missing_bones", ChatFormatting.YELLOW);
        }

        if (!futureParts.contains(BodyPartType.EYES)) {
            return SurgeryAlert.create("cyberware.risk.missing_eyes", ChatFormatting.YELLOW);
        }

        if (remainingTolerance < 25) {
            return SurgeryAlert.create("cyberware.risk.low_tolerance", ChatFormatting.YELLOW);
        }

        return null;
    }
}