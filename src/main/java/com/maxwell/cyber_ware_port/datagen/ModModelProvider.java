package com.maxwell.cyber_ware_port.datagen;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
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

            if (item instanceof BlockItem) {


                itemGenerators.declareCustomModelItem(item);
            } else if (item instanceof SpawnEggItem) {

                itemGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            } else if (name.contains("katana")) {

                itemGenerators.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
            } else if (isScavengedItem(item, name)) {

                generateScavengedItem(itemGenerators, item);
            } else {

                itemGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            }
        }
    }

    
    private boolean isScavengedItem(Item item, String name) {

        return !(item instanceof BlockItem)
                && !(item instanceof SpawnEggItem)
                && !name.contains("katana")
                && !name.contains("blueprint");
    }

    
    private void generateScavengedItem(ItemModelGenerators generators, Item item) {


        net.minecraft.resources.Identifier normalModelId = generators.createFlatItemModel(item, ModelTemplates.FLAT_ITEM);
        net.minecraft.client.renderer.item.ItemModel.Unbaked normalModel =
                net.minecraft.client.data.models.model.ItemModelUtils.plainModel(normalModelId);



        net.minecraft.resources.Identifier scavengedModelId = generators.createFlatItemModel(item, "_scavenged", ModelTemplates.FLAT_ITEM);
        net.minecraft.client.renderer.item.ItemModel.Unbaked scavengedModel =
                net.minecraft.client.data.models.model.ItemModelUtils.plainModel(scavengedModelId);

        generators.generateBooleanDispatch(
                item,
                new IsScavengedProperty(),
                scavengedModel,
                normalModel
        );
    }
}