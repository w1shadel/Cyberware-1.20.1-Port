package com.maxwell.cyber_ware_port.compat.emi;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModRecipes;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@EmiEntrypoint
public class CyberwareEmiPlugin implements EmiPlugin {
    public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/gui/engineering.png");

    public static final EmiRecipeCategory ASSEMBLY_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation(CyberWare.MODID, "assembly"),
            EmiStack.of(ModBlocks.CYBERWARE_WORKBENCH.get()),
            new EmiTexture(GUI_TEXTURE, 0, 0, 16, 16)
    );

    public static final EmiRecipeCategory ENGINEERING_CATEGORY = new EmiRecipeCategory(
            new ResourceLocation(CyberWare.MODID, "engineering"),
            EmiStack.of(ModBlocks.CYBERWARE_WORKBENCH.get()),
            new EmiTexture(GUI_TEXTURE, 16, 0, 16, 16)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(ASSEMBLY_CATEGORY);
        registry.addCategory(ENGINEERING_CATEGORY);

        registry.addWorkstation(ASSEMBLY_CATEGORY, EmiStack.of(ModBlocks.CYBERWARE_WORKBENCH.get()));
        registry.addWorkstation(ENGINEERING_CATEGORY, EmiStack.of(ModBlocks.CYBERWARE_WORKBENCH.get()));

        RecipeManager rm = registry.getRecipeManager();

        // 1.20.1 では RecipeHolder を介さず直接レシピを取得します
        for (AssemblyRecipe recipe : rm.getAllRecipesFor(ModRecipes.ASSEMBLY_TYPE.get())) {
            registry.addRecipe(new EmiAssemblyRecipe(recipe));
        }

        for (EngineeringRecipe recipe : rm.getAllRecipesFor(ModRecipes.ENGINEERING_TYPE.get())) {
            registry.addRecipe(new EmiEngineeringRecipe(recipe));
        }
    }
}