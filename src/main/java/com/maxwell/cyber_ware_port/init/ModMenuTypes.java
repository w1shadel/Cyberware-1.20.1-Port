package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.container.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CyberWare.MODID);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    public static final DeferredHolder<MenuType<?>, MenuType<RobosurgeonMenu>> ROBO_SURGEON_MENU =
            registerMenuType("robosurgeon_menu", RobosurgeonMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<CyberwareWorkbenchMenu>> CYBERWARE_WORKBENCH_MENU =
            registerMenuType("cyberware_workbench_menu", CyberwareWorkbenchMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ScannerMenu>> SCANNER_MENU =
            registerMenuType("scanner_menu", ScannerMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ComponentBoxMenu>> COMPONENT_BOX_MENU =
            registerMenuType("component_menu", ComponentBoxMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<BlueprintChestMenu>> BLUEPRINT_CHEST_MENU =
            registerMenuType("blueprint_chest_menu", BlueprintChestMenu::new);

}