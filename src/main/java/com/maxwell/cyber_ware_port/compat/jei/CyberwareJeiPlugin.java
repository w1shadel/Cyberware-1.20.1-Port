package com.maxwell.cyber_ware_port.compat.jei;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import com.maxwell.cyber_ware_port.init.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class CyberwareJeiPlugin implements IModPlugin {

    @Override
    public @NotNull Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(CyberWare.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AssemblyRecipeCategory(guiHelper));
        registration.addRecipeCategories(new EngineeringRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        var recipeRegistry = level.registryAccess().lookupOrThrow(Registries.RECIPE);

        List<AssemblyRecipe> assemblyRecipes = recipeRegistry.listElements()
                .map(holder -> (Recipe<?>) holder.value())
                .filter(recipe -> recipe.getType() == ModRecipes.ASSEMBLY_TYPE.get())
                .map(recipe -> (AssemblyRecipe) recipe)
                .toList();
        registration.addRecipes(AssemblyRecipeCategory.TYPE, assemblyRecipes);

        List<EngineeringRecipe> engineeringRecipes = recipeRegistry.listElements()
                .map(holder -> (Recipe<?>) holder.value())
                .filter(recipe -> recipe.getType() == ModRecipes.ENGINEERING_TYPE.get())
                .map(recipe -> (EngineeringRecipe) recipe)
                .toList();
        registration.addRecipes(EngineeringRecipeCategory.TYPE, engineeringRecipes);
    }
}