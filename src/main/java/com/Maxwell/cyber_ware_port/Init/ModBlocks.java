package com.Maxwell.cyber_ware_port.Init;

import com.Maxwell.cyber_ware_port.Common.Block.BlueprintChest.BlueprintChestBlock;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberwareWorkbenchBlock;
import com.Maxwell.cyber_ware_port.Common.Block.Component_Box.ComponentBoxBlock;
import com.Maxwell.cyber_ware_port.Common.Block.CyberSkull.CyberSkullBlock;
import com.Maxwell.cyber_ware_port.Common.Block.CyberSkull.CyberWallSkullBlock;
import com.Maxwell.cyber_ware_port.Common.Block.Radio.RadioKitBlock;
import com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower.RadioTowerCoreBlock;
import com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower.RadioTowerFenceBlock;
import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlock;
import com.Maxwell.cyber_ware_port.Common.Block.Scanner.ScannerBlock;
import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberBlock;
import com.Maxwell.cyber_ware_port.Common.Item.CyberSkullType;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CyberWare.MODID);
    public static final RegistryObject<Block> CYBER_WITHER_SKELETON_SKULL = BLOCKS.register("cyber_wither_skeleton_skull",
            () -> new CyberSkullBlock(CyberSkullType.CYBER_WITHER_SKELETON,
                    BlockBehaviour.Properties.of()
                            .strength(1.0F)
                            .pushReaction(PushReaction.DESTROY)
            ));
    public static final RegistryObject<Block> CYBER_WITHER_SKELETON_WALL_SKULL = BLOCKS.register("cyber_wither_skeleton_wall_skull",
            () -> new CyberWallSkullBlock(CyberSkullType.CYBER_WITHER_SKELETON,
                    BlockBehaviour.Properties.of()
                            .strength(1.0F)
                            .dropsLike(CYBER_WITHER_SKELETON_SKULL.get())
                            .pushReaction(PushReaction.DESTROY)
            ));
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
    public static final RegistryObject<Block> RADIO_TOWER_COMPONENT = registerBlock("radio_tower_component",
            () -> new RadioTowerFenceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RADIO_TOWER_CORE = registerBlock("radio_tower_core",
            () -> new RadioTowerCoreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> CYBERWARE_WORKBENCH = registerBlock("cyberware_workbench",
            () -> new CyberwareWorkbenchBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> COMPONENT_BOX = BLOCKS.register("component_box",
            () -> new ComponentBoxBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(0.4f, 6.0f)
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SCANNER = registerBlock("scanner",
            () -> new ScannerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> BLUEPRINT_CHEST = registerBlock("blueprint_chest",
            () -> new BlueprintChestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> RADIO_KIT_BLOCK = registerBlock("radio_kit",
            () -> new RadioKitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}