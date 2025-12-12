package com.Maxwell.cyber_ware_port.Init;

import com.Maxwell.cyber_ware_port.Common.Container.*;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CyberWare.MODID);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    public static final RegistryObject<MenuType<RobosurgeonMenu>> ROBO_SURGEON_MENU =
            MENUS.register("robosurgeon_menu",
                    () -> IForgeMenuType.create(RobosurgeonMenu::new));

    public static final RegistryObject<MenuType<CyberwareWorkbenchMenu>> CYBERWARE_WORKBENCH_MENU =
            registerMenuType(CyberwareWorkbenchMenu::new, "cyberware_workbench_menu");
    public static final RegistryObject<MenuType<ScannerMenu>> SCANNER_MENU =
            registerMenuType(ScannerMenu::new, "scanner_menu");
    public static final RegistryObject<MenuType<ComponentBoxMenu>> COMPONENT_BOX_MENU =
            registerMenuType(ComponentBoxMenu::new, "component_menu");
    public static final RegistryObject<MenuType<BlueprintChestMenu>> BLUEPRINT_CHEST_MENU =
            registerMenuType(BlueprintChestMenu::new, "blueprint_chest_menu");


}