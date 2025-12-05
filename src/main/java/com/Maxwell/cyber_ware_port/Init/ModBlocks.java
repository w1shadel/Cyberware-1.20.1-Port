package com.Maxwell.cyber_ware_port.Init;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberwareWorkbenchBlock;
import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlock;
import com.Maxwell.cyber_ware_port.Common.Block.Scanner.ScannerBlock;
import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberBlock;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CyberWare.MODID);
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    public static final RegistryObject<Block> ROBO_SURGEON = registerBlock("robo_surgeon",
            () -> new RobosurgeonBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SURGERY_CHAMBER = registerBlock("surgery_chamber",
            () -> new SurgeryChamberBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion() 
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CYBERWARE_WORKBENCH = registerBlock("cyberware_workbench",
            () -> new CyberwareWorkbenchBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> SCANNER = registerBlock("scanner",
            () -> new ScannerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}