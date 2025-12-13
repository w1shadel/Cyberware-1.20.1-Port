package com.Maxwell.cyber_ware_port.Datagen.WorldGen;

import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Map;

public class ModStructures {

    public static final ResourceKey<Structure> CYBER_LAB = createKey("cyber_lab");

    public static void bootstrap(BootstapContext<Structure> context) {
        HolderGetter<Biome> biomeGetter = context.lookup(Registries.BIOME);
        var poolGetter = context.lookup(Registries.TEMPLATE_POOL);
        context.register(CYBER_LAB, new JigsawStructure(
                structure(
                        biomeGetter.getOrThrow(BiomeTags.IS_OVERWORLD),
                        Map.of(),
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.BEARD_THIN
                ),
                poolGetter.getOrThrow(ModStructurePools.START),
                6,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                false,
                Heightmap.Types.WORLD_SURFACE_WG
        ));
    }

    private static Structure.StructureSettings structure(net.minecraft.core.HolderSet<Biome> biomes, Map<net.minecraft.world.entity.MobCategory, net.minecraft.world.level.levelgen.structure.StructureSpawnOverride> spawnOverrides, GenerationStep.Decoration step, TerrainAdjustment terrainAdjustment) {
        return new Structure.StructureSettings(biomes, spawnOverrides, step, terrainAdjustment);
    }

    private static ResourceKey<Structure> createKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(CyberWare.MODID, name));
    }
}