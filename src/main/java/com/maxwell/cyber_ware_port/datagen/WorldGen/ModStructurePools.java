package com.maxwell.cyber_ware_port.datagen.WorldGen;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class ModStructurePools {

    public static final ResourceKey<StructureTemplatePool> START = createKey("cyber_lab/start_pool");

    public static void bootstrap(BootstapContext<StructureTemplatePool> context) {
        var templateHolder = context.lookup(Registries.TEMPLATE_POOL);
        context.register(START, new StructureTemplatePool(
                templateHolder.getOrThrow(Pools.EMPTY),
                com.google.common.collect.ImmutableList.of(
                        Pair.of(StructurePoolElement.single("cyber_ware_port:cyber_lab/base"), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));
    }

    private static ResourceKey<StructureTemplatePool> createKey(String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(CyberWare.MODID, name));
    }
}