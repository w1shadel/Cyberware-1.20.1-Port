package com.maxwell.cyber_ware_port.datagen.Loot.Block;

import com.maxwell.cyber_ware_port.init.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.ROBO_SURGEON.get());
        this.add(ModBlocks.SURGERY_CHAMBER.get(), block ->
                createSinglePropConditionTable(block, BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
        this.dropSelf(ModBlocks.RADIO_TOWER_COMPONENT.get());
        this.dropSelf(ModBlocks.RADIO_TOWER_CORE.get());
        this.dropSelf(ModBlocks.CYBERWARE_WORKBENCH.get());
        this.dropSelf(ModBlocks.SCANNER.get());
        this.dropSelf(ModBlocks.CHARGER.get());
        this.dropSelf(ModBlocks.BLUEPRINT_CHEST.get());
        this.dropSelf(ModBlocks.RADIO_KIT_BLOCK.get());
        this.dropSelf(ModBlocks.CYBER_WITHER_SKELETON_SKULL.get());
        this.add(ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get(),
                block -> createSingleItemTable(ModBlocks.CYBER_WITHER_SKELETON_SKULL.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .filter(block -> block != ModBlocks.COMPONENT_BOX.get())
                ::iterator;
    }
}