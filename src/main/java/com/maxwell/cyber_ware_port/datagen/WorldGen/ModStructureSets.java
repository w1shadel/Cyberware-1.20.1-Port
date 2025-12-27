package com.maxwell.cyber_ware_port.datagen.WorldGen;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public class ModStructureSets {
    public static final ResourceKey<StructureSet> CYBER_LAB_SET = createKey("cyber_lab");

    public static void bootstrap(BootstapContext<StructureSet> context) {
        var structureGetter = context.lookup(Registries.STRUCTURE);
        context.register(CYBER_LAB_SET, new StructureSet(
                structureGetter.getOrThrow(ModStructures.CYBER_LAB),
                new RandomSpreadStructurePlacement(
                        32,
                        8,
                        RandomSpreadType.LINEAR,
                        12345678
                )
        ));
    }

    private static ResourceKey<StructureSet> createKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, new ResourceLocation(CyberWare.MODID, name));
    }
}