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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("cyber_ware_port");

    public static final DeferredBlock<CyberSkullBlock> CYBER_WITHER_SKELETON_SKULL = BLOCKS.register("cyber_wither_skeleton_skull",
            (location) -> new CyberSkullBlock(CyberSkullType.CYBER_WITHER_SKELETON,
                    BlockBehaviour.Properties.of()
                            .setId(ResourceKey.create(Registries.BLOCK, location))
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(1.0F)
                            .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<CyberWallSkullBlock> CYBER_WITHER_SKELETON_WALL_SKULL = BLOCKS.register("cyber_wither_skeleton_wall_skull",
            (location) -> new CyberWallSkullBlock(CyberSkullType.CYBER_WITHER_SKELETON,
                    BlockBehaviour.Properties.of()
                            .setId(ResourceKey.create(Registries.BLOCK, location))
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(1.0F)
                            .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<RobosurgeonBlock> ROBO_SURGEON = BLOCKS.register("robo_surgeon",
            (location) -> new RobosurgeonBlock(BlockBehaviour.Properties.of()
                            .setId(ResourceKey.create(Registries.BLOCK, location))
                            .mapColor(MapColor.METAL)
                                    .strength(5.0f, 6.0f)
                                    .requiresCorrectToolForDrops()));

    public static final DeferredBlock<SurgeryChamberBlock> SURGERY_CHAMBER = BLOCKS.register("surgery_chamber",
            (location) -> new SurgeryChamberBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(4.0f, 5.0f).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<RadioTowerFenceBlock> RADIO_TOWER_COMPONENT = BLOCKS.register("radio_tower_component",
            (location) -> new RadioTowerFenceBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(2.0f, 5.0f).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<RadioTowerCoreBlock> RADIO_TOWER_CORE = BLOCKS.register("radio_tower_core",
            (location) -> new RadioTowerCoreBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(2.0f, 5.0f).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<CyberwareWorkbenchBlock> CYBERWARE_WORKBENCH = BLOCKS.register("cyberware_workbench",
            (location) -> new CyberwareWorkbenchBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(4.0f, 5.0f).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<ComponentBoxBlock> COMPONENT_BOX = BLOCKS.register("component_box",
            (location) -> new ComponentBoxBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(0.4f, 6.0f).requiresCorrectToolForDrops()));

    public static final DeferredBlock<ScannerBlock> SCANNER = BLOCKS.register("scanner",
            (location) -> new ScannerBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(4.0f, 5.0f).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<ChargerBlock> CHARGER = BLOCKS.register("charger",
            (location) -> new ChargerBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(4.0f, 5.0f).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<BlueprintChestBlock> BLUEPRINT_CHEST = BLOCKS.register("blueprint_chest",
            (location) -> new BlueprintChestBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(4.0f, 5.0f).noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<RadioKitBlock> RADIO_KIT_BLOCK = BLOCKS.register("radio_kit",
            (location) -> new RadioKitBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, location)).mapColor(MapColor.METAL).strength(2.0f, 5.0f).noOcclusion().requiresCorrectToolForDrops()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
    }
