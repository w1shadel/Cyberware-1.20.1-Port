package com.maxwell.cyber_ware_port.api.json;

import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class CyberwareAPI {
    @Nullable
    public static ICyberware getCyberware(ItemStack stack) {
        if (stack.isEmpty()) return null;
        // 1. クラスが直接 ICyberware を実装している場合
        if (stack.getItem() instanceof ICyberware cyber) {
            return cyber;
        }
        // 2. データパック（DynamicCyberware）で定義されている場合
        return CyberwareDataManager.DYNAMIC_CYBERWARE.get(stack.getItem());
    }

    public static boolean isCyberware(ItemStack stack) {
        return getCyberware(stack) != null;
    }
}