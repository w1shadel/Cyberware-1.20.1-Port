package com.Maxwell.cyber_ware_port.Config;


import net.minecraftforge.common.ForgeConfigSpec;


public class CyberwareConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec COMMON_CONFIG;


    public static final ForgeConfigSpec.BooleanValue KEEP_CYBERWARE_ON_DEATH;


    static {
        BUILDER.push("Behavior");


        KEEP_CYBERWARE_ON_DEATH = BUILDER
                .comment("Whether players should keep their cyberware after death.")
                .comment("True: Keep cyberware (Default).")
                .comment("False: Lose all cyberware on death.")
                .define("keepCyberwareOnDeath", true);


        BUILDER.pop();

        COMMON_CONFIG = BUILDER.build();

    }
}