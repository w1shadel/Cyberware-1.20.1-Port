package com.Maxwell.cyber_ware_port.Init;

import com.Maxwell.cyber_ware_port.Common.Block.BlueprintChest.BlueprintChestBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberwareWorkbenchBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.Component_Box.ComponentBoxBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.CyberSkull.CyberSkullBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower.RadioTowerCoreBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.Scanner.ScannerBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberBlockEntity;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CyberWare.MODID);
    public static final RegistryObject<BlockEntityType<CyberSkullBlockEntity>> CYBER_SKULL = BLOCK_ENTITIES.register("cyber_wither_skeleton_skull",
            () -> BlockEntityType.Builder.of(
                    CyberSkullBlockEntity::new,
                    ModBlocks.CYBER_WITHER_SKELETON_SKULL.get(),
                    ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get()
            ).build(null));    public static final RegistryObject<BlockEntityType<SurgeryChamberBlockEntity>> SURGERY_CHAMBER =
            BLOCK_ENTITIES.register("surgery_chamber",
                    () -> BlockEntityType.Builder.of(
                            SurgeryChamberBlockEntity::new,
                            ModBlocks.SURGERY_CHAMBER.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }    public static final RegistryObject<BlockEntityType<RobosurgeonBlockEntity>> ROBO_SURGEON =
            BLOCK_ENTITIES.register("robo_surgeon",
                    () -> BlockEntityType.Builder.of(
                            RobosurgeonBlockEntity::new,
                            ModBlocks.ROBO_SURGEON.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<RadioTowerCoreBlockEntity>> RADIO_TOWER_CORE =
            BLOCK_ENTITIES.register("radio_tower_core",
                    () -> BlockEntityType.Builder.of(
                            RadioTowerCoreBlockEntity::new,
                            ModBlocks.RADIO_TOWER_CORE.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<CyberwareWorkbenchBlockEntity>> CYBERWARE_WORKBENCH =
            BLOCK_ENTITIES.register("cyberware_workbench", () ->
                    BlockEntityType.Builder.of(CyberwareWorkbenchBlockEntity::new,
                            ModBlocks.CYBERWARE_WORKBENCH.get()).build(null));
    public static final RegistryObject<BlockEntityType<ComponentBoxBlockEntity>> COMPONENT_BOX =
            BLOCK_ENTITIES.register("component_box", () ->
                    BlockEntityType.Builder.of(ComponentBoxBlockEntity::new,
                            ModBlocks.COMPONENT_BOX.get()).build(null));

    public static final RegistryObject<BlockEntityType<ScannerBlockEntity>> SCANNER =
            BLOCK_ENTITIES.register("scanner", () ->
                    BlockEntityType.Builder.of(ScannerBlockEntity::new,
                            ModBlocks.SCANNER.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlueprintChestBlockEntity>> BLUEPRINT_CHEST =
            BLOCK_ENTITIES.register("blueprint_chest", () ->
                    BlockEntityType.Builder.of(BlueprintChestBlockEntity::new,
                            ModBlocks.BLUEPRINT_CHEST.get()).build(null));



}