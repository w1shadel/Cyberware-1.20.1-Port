package com.maxwell.cyber_ware_port.datagen;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CyberWare.MODID, exFileHelper);

    }

    @Override
    protected void registerStatesAndModels() {
        fenceBlock((FenceBlock) ModBlocks.RADIO_TOWER_COMPONENT.get(), modLoc("block/radio_tower_texture"));
        registerSkull(ModBlocks.CYBER_WITHER_SKELETON_SKULL, ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL);

    }

    private void registerSkull(Supplier<Block> skull, Supplier<Block> wallSkull) {
        ModelFile skullModel = models().getBuilder(skull.getId().getPath())
                .texture("particle", ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "entity/cyber_wither_skeleton"));
        simpleBlock(skull.get(), skullModel);
        registerComponentBox();
        simpleBlock(wallSkull.get(), skullModel);

    }

    private void registerComponentBox() {
        ModelFile customModel = models().getExistingFile(
                ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "block/component_box"));
        simpleBlock(ModBlocks.COMPONENT_BOX.get(), customModel);

    }
}
