package com.Maxwell.cyber_ware_port.Datagen;import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModEntities;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;

@SuppressWarnings("remeoval")
public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> CYBER_ZOMBIE_SPAWN = registerKey("cyber_zombie_spawn");

    public static final ResourceKey<BiomeModifier> CYBER_SKELETON_SPAWN = registerKey("cyber_skeleton_spawn");

    public static final ResourceKey<BiomeModifier> CYBER_CREEPER_SPAWN = registerKey("cyber_creeper_spawn");

    public static final ResourceKey<BiomeModifier> CYBER_WITHER_SKELETON_SPAWN = registerKey("cyber_wither_skeleton_spawn");public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var biomes = context.lookup(Registries.BIOME);context.register(CYBER_ZOMBIE_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                Collections.singletonList(new MobSpawnSettings.SpawnerData(ModEntities.CYBER_ZOMBIE.get(), 50, 2, 4))
        ));context.register(CYBER_SKELETON_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                (HolderSet<Biome>) biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                Collections.singletonList(new MobSpawnSettings.SpawnerData(ModEntities.CYBER_SKELETON.get(), 50, 2, 4))
        ));context.register(CYBER_CREEPER_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                (HolderSet<Biome>) biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                Collections.singletonList(new MobSpawnSettings.SpawnerData(ModEntities.CYBER_CREEPER.get(), 50, 2, 4))
        ));context.register(CYBER_WITHER_SKELETON_SPAWN, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(
                (HolderSet<Biome>) biomes.getOrThrow(BiomeTags.IS_NETHER),
                Collections.singletonList(new MobSpawnSettings.SpawnerData(ModEntities.CYBER_WITHER_SKELETON.get(), 10, 1, 2))
        ));

    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(CyberWare.MODID, name));

    }
}