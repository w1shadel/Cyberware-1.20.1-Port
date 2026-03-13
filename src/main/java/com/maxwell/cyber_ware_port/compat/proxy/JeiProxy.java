package com.maxwell.cyber_ware_port.compat.Proxy;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import com.maxwell.cyber_ware_port.compat.CyberwareJeiPlugin;
import mezz.jei.api.recipe.RecipeType;

public class JeiProxy {
    public static final RecipeType<AssemblyRecipe> ASSEMBLY_TYPE =
            RecipeType.create(CyberWare.MODID, "assembly", AssemblyRecipe.class);
    public static final RecipeType<EngineeringRecipe> ENGINEERING_TYPE =
            RecipeType.create(CyberWare.MODID, "engineering", EngineeringRecipe.class);

    public static void showAssemblyCategory() {
        CyberwareJeiPlugin.showRecipeCategory(ASSEMBLY_TYPE);
    }

    public static void showEngineeringCategory() {
        CyberwareJeiPlugin.showRecipeCategory(ENGINEERING_TYPE);
    }
}