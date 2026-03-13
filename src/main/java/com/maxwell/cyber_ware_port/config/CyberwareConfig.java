package com.maxwell.cyber_ware_port.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CyberwareConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec.BooleanValue KEEP_CYBERWARE_ON_DEATH;
    public static final ModConfigSpec.IntValue MAX_TOLERANCE;
    public static final ModConfigSpec.IntValue CRITICAL_ESSENCE;
    public static final ModConfigSpec.BooleanValue CONSUME_DEFIBRILLATOR_ON_USE;
    public static final ModConfigSpec.DoubleValue WITHER_CONVERSION_CHANCE;
    public static final ModConfigSpec.DoubleValue ZOMBIE_CONVERSION_CHANCE;
    public static final ModConfigSpec.DoubleValue SKELETON_CONVERSION_CHANCE;
    public static final ModConfigSpec.DoubleValue CREEPER_CONVERSION_CHANCE;
    public static final ModConfigSpec.BooleanValue CONSUME_BLUEPRINT;

    static {
        BUILDER.push("Behavior");
        KEEP_CYBERWARE_ON_DEATH = BUILDER
                .comment("Whether players should keep their cyberware after death.", "True: Keep cyberware (Default).", "False: Lose all cyberware on death.")
                .define("keepCyberwareOnDeath", true);
        MAX_TOLERANCE = BUILDER
                .comment("The maximum tolerance a player has.", "Default: 100")
                .defineInRange("maxTolerance", 100, 1, 1000);
        CRITICAL_ESSENCE = BUILDER
                .comment("The amount of essence remaining at which rejection effects start occurring.",
                        "If set to 25, players will suffer rejection when they have less than 25 essence left.",
                        "Set to 0 to disable rejection effects until hitting 0.")
                .defineInRange("critical_essence_threshold", 25, 0, 1000);
        CONSUME_DEFIBRILLATOR_ON_USE = BUILDER
                .comment("Whether the Internal Defibrillator is consumed after saving the player from death.",
                        "True: The item is destroyed upon use (Default).",
                        "False: The item can be used indefinitely, only consuming energy.")
                .define("consumeDefibrillatorOnUse", true);
        CONSUME_BLUEPRINT = BUILDER
                .comment("Whether blueprints are consumed when crafting in the Cyberware Workbench.")
                .define("consumeBlueprint", false);
        BUILDER.pop();

        BUILDER.push("Spawning");
        WITHER_CONVERSION_CHANCE = BUILDER
                .comment("Chance (0.0 to 1.0) for a Wither Skeleton to become a Cyber Wither Skeleton.")
                .defineInRange("witherConversionChance", 0.2, 0.0, 1.0);
        ZOMBIE_CONVERSION_CHANCE = BUILDER
                .comment("Chance (0.0 to 1.0) for a Zombie to spawn as a Cyber Zombie")
                .defineInRange("zombieConversionChance", 0.1, 0.0, 1.0);
        SKELETON_CONVERSION_CHANCE = BUILDER
                .comment("Chance (0.0 to 1.0) for a Skeleton to spawn as a Cyber Skeleton")
                .defineInRange("skeletonConversionChance", 0.1, 0.0, 1.0);
        CREEPER_CONVERSION_CHANCE = BUILDER
                .comment("Chance (0.0 to 1.0) for a Creeper to spawn as a Cyber Creeper")
                .defineInRange("creeperConversionChance", 0.1, 0.0, 1.0);
        BUILDER.pop();

        COMMON_CONFIG = BUILDER.build();
    }
}