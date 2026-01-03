package com.maxwell.cyber_ware_port.client;

public class ClientCyberwareSettings {
    public static int hudX = 10;
    public static int hudY = 10;
    public static int hudColor = 0xFF00FFFF;

    public static void setHudColorFromHex(String hex) {
        if (hex == null || hex.isEmpty()) return;
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        try {
            long parsedValue;
            if (hex.length() == 6) {
                parsedValue = Long.parseLong(hex, 16);
                hudColor = (int) (0xFF000000 | parsedValue);
            } else if (hex.length() == 8) {
                parsedValue = Long.parseLong(hex, 16);
                hudColor = (int) parsedValue;
            }
        } catch (NumberFormatException e) {
        }
    }

    public static String getHudColorAsHex() {
        return String.format("%06X", (0xFFFFFF & hudColor));
    }

    public static float[] getColorFloats() {
        float a = ((hudColor >> 24) & 0xFF) / 255f;
        float r = ((hudColor >> 16) & 0xFF) / 255f;
        float g = ((hudColor >> 8) & 0xFF) / 255f;
        float b = (hudColor & 0xFF) / 255f;
        return new float[]{r, g, b, a};
    }
}