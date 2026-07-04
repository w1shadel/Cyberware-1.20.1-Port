package com.maxwell.cyber_ware_port.datagen;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagProvider extends BiomeTagsProvider {
    public static final ResourceKey<Biome> HAS_CYBER_LAB = ResourceKey.create(Registries.BIOME,
            new ResourceLocation(CyberWare.MODID, "has_structure/cyber_lab"));

    public ModBiomeTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, CyberWare.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(net.minecraft.tags.TagKey.create(Registries.BIOME,
                        new ResourceLocation(CyberWare.MODID, "has_structure/cyber_lab")))
                .addTag(BiomeTags.IS_OVERWORLD)
                .remove(BiomeTags.IS_OCEAN)
                .remove(BiomeTags.IS_RIVER);
    }
}
