package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.blueprintchest.BlueprintChestBlock;
import com.maxwell.cyber_ware_port.common.block.charger.ChargerBlock;
import com.maxwell.cyber_ware_port.common.block.component_box.ComponentBoxBlock;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberwareWorkbenchBlock;
import com.maxwell.cyber_ware_port.common.block.cyberskull.CyberSkullBlock;
import com.maxwell.cyber_ware_port.common.block.cyberskull.CyberWallSkullBlock;
import com.maxwell.cyber_ware_port.common.block.radio.RadioKitBlock;
import com.maxwell.cyber_ware_port.common.block.radio.tower.RadioTowerCoreBlock;
import com.maxwell.cyber_ware_port.common.block.radio.tower.RadioTowerFenceBlock;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlock;
import com.maxwell.cyber_ware_port.common.block.scanner.ScannerBlock;
import com.maxwell.cyber_ware_port.common.block.surgerychamber.SurgeryChamberBlock;
import com.maxwell.cyber_ware_port.common.item.CyberSkullType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CyberWare.MODID);
    public static final DeferredHolder<Block, CyberSkullBlock> CYBER_WITHER_SKELETON_SKULL = BLOCKS.register("cyber_wither_skeleton_skull",
            () -> new CyberSkullBlock(CyberSkullType.CYBER_WITHER_SKELETON,
                    BlockBehaviour.Properties.of()
                            .strength(1.0F)
                            .pushReaction(PushReaction.DESTROY)
            ));
    public static final DeferredHolder<Block, CyberWallSkullBlock> CYBER_WITHER_SKELETON_WALL_SKULL = BLOCKS.register("cyber_wither_skeleton_wall_skull",
            () -> new CyberWallSkullBlock(CyberSkullType.CYBER_WITHER_SKELETON,
                    BlockBehaviour.Properties.of()
                            .strength(1.0F)
                            .lootFrom(CYBER_WITHER_SKELETON_SKULL)
                            .pushReaction(PushReaction.DESTROY)
            ));
    public static final DeferredHolder<Block, RobosurgeonBlock> ROBO_SURGEON = registerBlock("robo_surgeon",
            () -> new RobosurgeonBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0f, 6.0f)
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, SurgeryChamberBlock> SURGERY_CHAMBER = registerBlock("surgery_chamber",
            () -> new SurgeryChamberBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, RadioTowerFenceBlock> RADIO_TOWER_COMPONENT = registerBlock("radio_tower_component",
            () -> new RadioTowerFenceBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, RadioTowerCoreBlock> RADIO_TOWER_CORE = registerBlock("radio_tower_core",
            () -> new RadioTowerCoreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, CyberwareWorkbenchBlock> CYBERWARE_WORKBENCH = registerBlock("cyberware_workbench",
            () -> new CyberwareWorkbenchBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, ComponentBoxBlock> COMPONENT_BOX = BLOCKS.register("component_box",
            () -> new ComponentBoxBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(0.4f, 6.0f)
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, ScannerBlock> SCANNER = registerBlock("scanner",
            () -> new ScannerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, ChargerBlock> CHARGER = registerBlock("charger",
            () -> new ChargerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, BlueprintChestBlock> BLUEPRINT_CHEST = registerBlock("blueprint_chest",
            () -> new BlueprintChestBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(4.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, RadioKitBlock> RADIO_KIT_BLOCK = registerBlock("radio_kit",
            () -> new RadioKitBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0f, 5.0f)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()));

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredHolder<Block, T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
