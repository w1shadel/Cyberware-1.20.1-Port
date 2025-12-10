package com.Maxwell.cyber_ware_port.Datagen;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;


public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CyberWare.MODID, exFileHelper);

    }

    @Override
    protected void registerStatesAndModels() {
        fenceBlock((FenceBlock) ModBlocks.RADIO_TOWER_COMPONENT.get(), modLoc("block/radio_tower_texture"));
        registerSkull(ModBlocks.CYBER_WITHER_SKELETON_SKULL, ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL);

    }

    private void registerSkull(RegistryObject<Block> skull, RegistryObject<Block> wallSkull) {
        ModelFile skullModel = models().getBuilder(skull.getId().getPath())
                .texture("particle", new ResourceLocation(CyberWare.MODID, "entity/cyber_wither_skeleton"));
        simpleBlock(skull.get(), skullModel);
        registerComponentBox();
        simpleBlock(wallSkull.get(), skullModel);

    }

    private void registerComponentBox() {
        ModelFile customModel = models().getExistingFile(
                new ResourceLocation(CyberWare.MODID, "block/component_box"));
        simpleBlock(ModBlocks.COMPONENT_BOX.get(), customModel);

    }
}
