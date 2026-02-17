package com.maxwell.cyber_ware_port.compat;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModRecipes;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@SuppressWarnings("removal")
@dev.emi.emi.api.EmiEntrypoint
public class CyberwareEmiPlugin implements EmiPlugin {
    public static final EmiStack WORKBENCH = EmiStack.of(ModBlocks.CYBERWARE_WORKBENCH.get());
    public static final EmiRecipeCategory ASSEMBLY = new EmiRecipeCategory(
            new ResourceLocation(CyberWare.MODID, "assembly"), WORKBENCH,
            new EmiTexture(new ResourceLocation(CyberWare.MODID, "textures/gui/engineering.png"), 0, 0, 16, 16));
    public static final EmiRecipeCategory ENGINEERING = new EmiRecipeCategory(
            new ResourceLocation(CyberWare.MODID, "engineering"), WORKBENCH,
            new EmiTexture(new ResourceLocation(CyberWare.MODID, "textures/gui/engineering.png"), 0, 0, 16, 16));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(ASSEMBLY);
        registry.addCategory(ENGINEERING);
        registry.addWorkstation(ASSEMBLY, WORKBENCH);
        registry.addWorkstation(ENGINEERING, WORKBENCH);
        RecipeManager manager = registry.getRecipeManager();
        manager.getAllRecipesFor(ModRecipes.ASSEMBLY_TYPE.get())
                .forEach(recipe -> registry.addRecipe(new CyberwareEmiRecipes.Assembly(recipe)));
        manager.getAllRecipesFor(ModRecipes.ENGINEERING_TYPE.get())
                .forEach(recipe -> registry.addRecipe(new CyberwareEmiRecipes.Engineering(recipe)));
    }
}
