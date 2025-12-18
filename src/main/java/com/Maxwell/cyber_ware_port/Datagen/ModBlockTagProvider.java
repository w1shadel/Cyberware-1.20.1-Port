package com.Maxwell.cyber_ware_port.Datagen;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CyberWare.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.FENCES)
                .add(ModBlocks.RADIO_TOWER_COMPONENT.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.RADIO_TOWER_COMPONENT.get())
                .add(ModBlocks.RADIO_TOWER_CORE.get())
                .add(ModBlocks.ROBO_SURGEON.get())
                .add(ModBlocks.SURGERY_CHAMBER.get())
                .add(ModBlocks.CYBERWARE_WORKBENCH.get())
                .add(ModBlocks.SCANNER.get())
                .add(ModBlocks.BLUEPRINT_CHEST.get())
                .add(ModBlocks.RADIO_KIT_BLOCK.get())
                .add(ModBlocks.CHARGER.get())
                .add(ModBlocks.COMPONENT_BOX.get())
                .add(ModBlocks.CYBER_WITHER_SKELETON_SKULL.get())
                .add(ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get());
        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.RADIO_TOWER_COMPONENT.get())
                .add(ModBlocks.RADIO_TOWER_CORE.get())
                .add(ModBlocks.ROBO_SURGEON.get())
                .add(ModBlocks.SURGERY_CHAMBER.get())
                .add(ModBlocks.CYBERWARE_WORKBENCH.get())
                .add(ModBlocks.SCANNER.get())
                .add(ModBlocks.CHARGER.get())
                .add(ModBlocks.BLUEPRINT_CHEST.get())
                .add(ModBlocks.RADIO_KIT_BLOCK.get());
    }
}