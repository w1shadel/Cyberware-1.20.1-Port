package com.maxwell.cyber_ware_port.common.util;

import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.EnumSet;
import java.util.Set;

public class CyberwareBodyStatus {
    private final Set<BodyPartType> presentParts = EnumSet.noneOf(BodyPartType.class);
    private int armCount = 0;
    private int legCount = 0;
    private int cyberArmCount = 0;
    private int cyberLegCount = 0;

    public CyberwareBodyStatus(ItemStacksResourceHandler handler) {
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stack = handler.getResource(i).toStack(handler.getAmountAsInt(i));
            if (stack.isEmpty()) {
                continue;
            }
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null) {
                BodyPartType type = cw.getBodyPartType(stack);
                if (type != BodyPartType.NONE) {
                    presentParts.add(type);
                }
            }
            if (isArm(stack, cw)) {
                armCount++;
                if (isCybernetic(stack, cw)) {
                    cyberArmCount++;
                }
            } else if (isLeg(stack, cw)) {
                legCount++;
                if (isCybernetic(stack, cw)) {
                    cyberLegCount++;
                }
            }
        }
    }

    private boolean isCybernetic(ItemStack stack, ICyberware cw) {
        return cw != null && cw.getQuality(stack) > 0;
    }

    private boolean isArm(ItemStack stack, ICyberware cw) {
        if (stack.is(ModItems.HUMAN_LEFT_ARM.get()) || stack.is(ModItems.HUMAN_RIGHT_ARM.get())) {
            return true;
        }
        if (cw != null) {
            int slot = cw.getSlot(stack);
            return slot == RobosurgeonBlockEntity.SLOT_ARMS || slot == RobosurgeonBlockEntity.SLOT_ARMS + 1;
        }
        return false;
    }

    private boolean isLeg(ItemStack stack, ICyberware cw) {
        if (stack.is(ModItems.HUMAN_LEFT_LEG.get()) || stack.is(ModItems.HUMAN_RIGHT_LEG.get())) {
            return true;
        }
        if (cw != null) {
            int slot = cw.getSlot(stack);
            return slot == RobosurgeonBlockEntity.SLOT_LEGS || slot == RobosurgeonBlockEntity.SLOT_LEGS + 1;
        }
        return false;
    }

    public boolean hasPart(BodyPartType type) {
        return presentParts.contains(type);
    }

    public int getArmCount() {
        return armCount;
    }

    public int getLegCount() {
        return legCount;
    }

    public int getCyberArmCount() {
        return cyberArmCount;
    }

    public int getCyberLegCount() {
        return cyberLegCount;
    }

    public boolean isHandFunctional() {
        return armCount >= 1;
    }
}