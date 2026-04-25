package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.blueprintchest.BlueprintChestBlockEntity;
import com.maxwell.cyber_ware_port.common.block.charger.ChargerBlockEntity;
import com.maxwell.cyber_ware_port.common.block.component_box.ComponentBoxBlockEntity;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberwareWorkbenchBlockEntity;
import com.maxwell.cyber_ware_port.common.block.cyberskull.CyberSkullBlockEntity;
import com.maxwell.cyber_ware_port.common.block.radio.tower.RadioTowerCoreBlockEntity;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.block.scanner.ScannerBlockEntity;
import com.maxwell.cyber_ware_port.common.block.surgerychamber.SurgeryChamberBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CyberWare.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CyberSkullBlockEntity>> CYBER_SKULL =
            BLOCK_ENTITIES.register("cyber_wither_skeleton_skull",
                    () -> new BlockEntityType<>( // Builder ではなくコンストラクタを直接呼ぶ
                            CyberSkullBlockEntity::new,
                            ModBlocks.CYBER_WITHER_SKELETON_SKULL.get(),
                            ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get()
                    ));
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurgeryChamberBlockEntity>> SURGERY_CHAMBER =
            BLOCK_ENTITIES.register("surgery_chamber",
                    () -> new BlockEntityType<>(
                            SurgeryChamberBlockEntity::new,
                            ModBlocks.SURGERY_CHAMBER.get()
                    ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RobosurgeonBlockEntity>> ROBO_SURGEON =
            BLOCK_ENTITIES.register("robo_surgeon",
                    () -> new BlockEntityType<>(
                            RobosurgeonBlockEntity::new,
                            ModBlocks.ROBO_SURGEON.get()
                    ));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadioTowerCoreBlockEntity>> RADIO_TOWER_CORE =
            BLOCK_ENTITIES.register("radio_tower_core",
                    () -> new BlockEntityType<>(
                            RadioTowerCoreBlockEntity::new,
                            ModBlocks.RADIO_TOWER_CORE.get()
                    ));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CyberwareWorkbenchBlockEntity>> CYBERWARE_WORKBENCH =
            BLOCK_ENTITIES.register("cyberware_workbench",
                    () -> new BlockEntityType<>(
                            CyberwareWorkbenchBlockEntity::new,
                            ModBlocks.CYBERWARE_WORKBENCH.get()
                    ));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ComponentBoxBlockEntity>> COMPONENT_BOX =
            BLOCK_ENTITIES.register("component_box",
                    () -> new BlockEntityType<>(
                            ComponentBoxBlockEntity::new,
                            ModBlocks.COMPONENT_BOX.get()
                    ));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ScannerBlockEntity>> SCANNER =
            BLOCK_ENTITIES.register("scanner",
                    () -> new BlockEntityType<>(
                            ScannerBlockEntity::new,
                            ModBlocks.SCANNER.get()
                    ));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargerBlockEntity>> CHARGER =
            BLOCK_ENTITIES.register("chager",
                    () -> new BlockEntityType<>(
                            ChargerBlockEntity::new,
                            ModBlocks.CHARGER.get()
                    ));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlueprintChestBlockEntity>> BLUEPRINT_CHEST =
            BLOCK_ENTITIES.register("blueprint_chest",
                    () -> new BlockEntityType<>(
                            BlueprintChestBlockEntity::new,
                            ModBlocks.BLUEPRINT_CHEST.get()
                    ));

}
