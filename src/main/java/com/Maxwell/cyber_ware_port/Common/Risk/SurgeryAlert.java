package com.Maxwell.cyber_ware_port.Common.Risk;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public record SurgeryAlert(Component message, int color) {

    public static SurgeryAlert create(String key, ChatFormatting colorFormat) {
        int colorHex = (colorFormat.getColor() != null) ? colorFormat.getColor() : 0xFFFFFF;
        Component text = Component.translatable(key).withStyle(colorFormat);
        return new SurgeryAlert(text, colorHex);
    }
}