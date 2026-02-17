package com.maxwell.cyber_ware_port.compat;

import com.maxwell.cyber_ware_port.compat.Proxy.EmiProxy;
import com.maxwell.cyber_ware_port.compat.Proxy.JeiProxy;
import net.minecraftforge.fml.ModList;

public class CompatHandler {
    public static void showAssemblyRecipes() {
        if (ModList.get().isLoaded("jei")) {
            JeiProxy.showAssemblyCategory();
        } else if (ModList.get().isLoaded("emi")) {
            EmiProxy.showAssemblyCategory();
        }
    }

    public static void showEngineeringRecipes() {
        if (ModList.get().isLoaded("jei")) {
            JeiProxy.showEngineeringCategory();
        } else if (ModList.get().isLoaded("emi")) {
            EmiProxy.showEngineeringCategory();
        }
    }
}
