package com.Maxwell.cyber_ware_port.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CyberwareConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.BooleanValue KEEP_CYBERWARE_ON_DEATH;

    public static final ForgeConfigSpec.IntValue MAX_TOLERANCE;
    public static final ForgeConfigSpec.BooleanValue CONSUME_DEFIBRILLATOR_ON_USE;

    static {
        BUILDER.push("Behavior");
        KEEP_CYBERWARE_ON_DEATH = BUILDER
                .comment("Whether players should keep their cyberware after death.")
                .comment("True: Keep cyberware (Default).")
                .comment("False: Lose all cyberware on death.")
                .define("keepCyberwareOnDeath", true);
        MAX_TOLERANCE = BUILDER
                .comment("The maximum tolerance (essence) a player has.")
                .comment("Default: 100")
                .defineInRange("maxTolerance", 100, 1, 1000);
        CONSUME_DEFIBRILLATOR_ON_USE = BUILDER
                .comment("Whether the Internal Defibrillator is consumed after saving the player from death.")
                .comment("True: The item is destroyed upon use (Default).")
                .comment("False: The item can be used indefinitely, only consuming energy.")
                .define("consumeDefibrillatorOnUse", true);
        BUILDER.pop();
        COMMON_CONFIG = BUILDER.build();
    }
}