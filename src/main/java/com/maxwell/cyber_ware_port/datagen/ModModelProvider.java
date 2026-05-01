package com.maxwell.cyber_ware_port.datagen;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, CyberWare.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockGenerators, ItemModelGenerators itemGenerators) {
        for (DeferredHolder<Block, ? extends Block> entry : ModBlocks.BLOCKS.getEntries()) {
            Block block = entry.get();
            if (entry.getId().getPath().contains("skull")) {
                blockGenerators.createTrivialBlock(block, TexturedModel.CUBE);
            } else {
                blockGenerators.createTrivialCube(block);
            }
        }
        for (DeferredHolder<Item, ? extends Item> entry : ModItems.ITEMS.getEntries()) {
            Item item = entry.get();
            String name = entry.getId().getPath();
            if (item instanceof SpawnEggItem) {
                itemGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            } else if (name.contains("katana")) {
                itemGenerators.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
            } else {
                itemGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            }
        }
    }
}