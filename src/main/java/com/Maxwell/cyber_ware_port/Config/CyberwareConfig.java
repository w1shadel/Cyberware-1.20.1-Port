package com.Maxwell.cyber_ware_port.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CyberwareConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.BooleanValue KEEP_CYBERWARE_ON_DEATH;
    public static final ForgeConfigSpec.IntValue SPAWN_INTERVAL;
    public static final ForgeConfigSpec.IntValue MAX_NEARBY_MOBS;
    public static final ForgeConfigSpec.DoubleValue WITHER_CONVERSION_CHANCE;

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
        BUILDER.push("Spawning");
        SPAWN_INTERVAL = BUILDER
                .comment("How many ticks between cyber mob spawn attempts (20 ticks = 1 second). Default: 600 (30 seconds)")
                .defineInRange("spawnInterval", 600, 1, 12000);
        MAX_NEARBY_MOBS = BUILDER
                .comment("Maximum number of monsters allowed around the player before stopping custom spawning. Prevents overcrowding.")
                .defineInRange("maxNearbyMobs", 30, 0, 100);
        WITHER_CONVERSION_CHANCE = BUILDER
                .comment("Chance (0.0 to 1.0) for a Wither Skeleton to become a Cyber Wither Skeleton.")
                .defineInRange("witherConversionChance", 0.2, 0.0, 1.0);
        BUILDER.pop();
        COMMON_CONFIG = BUILDER.build();
    }
}