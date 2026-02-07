package com.maxwell.cyber_ware_port.api.json;

import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class CyberwareAPI {
    @Nullable
    public static ICyberware getCyberware(ItemStack stack) {
        if (stack.isEmpty()) return null;

        if (stack.getItem() instanceof ICyberware cyber) {
            return cyber;
        }

        return CyberwareDataManager.DYNAMIC_CYBERWARE.get(stack.getItem());
    }

    public static boolean isCyberware(ItemStack stack) {
        return getCyberware(stack) != null;
    }
}