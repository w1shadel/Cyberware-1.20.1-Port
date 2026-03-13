package com.maxwell.cyber_ware_port.compat.Proxy;

import com.maxwell.cyber_ware_port.compat.CyberwareEmiPlugin;
import dev.emi.emi.api.EmiApi;

public class EmiProxy {
    public static void showAssemblyCategory() {
        EmiApi.displayRecipeCategory(CyberwareEmiPlugin.ASSEMBLY);
    }

    public static void showEngineeringCategory() {
        EmiApi.displayRecipeCategory(CyberwareEmiPlugin.ENGINEERING);
    }
}