package com.maxwell.cyber_ware_port.compat.jei;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModRecipes;
import com.mojang.logging.LogUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

@JeiPlugin
public final class CyberwareJeiPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier PLUGIN_UID = Identifier.fromNamespaceAndPath(CyberWare.MODID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new AssemblyRecipeCategory(guiHelper));
        registration.addRecipeCategories(new EngineeringRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        net.minecraft.world.item.crafting.RecipeMap recipes = mezz.jei.common.Internal.getClientSyncedRecipes();
        Identifier assemblyTypeId = net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE.getKey(ModRecipes.ASSEMBLY_TYPE.get());
        Identifier engineeringTypeId = net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE.getKey(ModRecipes.ENGINEERING_TYPE.get());
        var assemblyRecipes = recipes.values().stream()
                .filter(holder -> {
                    Identifier typeId = net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType());
                    return typeId != null && typeId.equals(assemblyTypeId);
                })
                .map(holder -> (net.minecraft.world.item.crafting.RecipeHolder<AssemblyRecipe>) (Object) holder)
                .toList();
        var engineeringRecipes = recipes.values().stream()
                .filter(holder -> {
                    Identifier typeId = net.minecraft.core.registries.BuiltInRegistries.RECIPE_TYPE.getKey(holder.value().getType());
                    return typeId != null && typeId.equals(engineeringTypeId);
                })
                .map(holder -> (net.minecraft.world.item.crafting.RecipeHolder<EngineeringRecipe>) (Object) holder)
                .toList();
        registration.addRecipes(JeiRecipeTypes.ASSEMBLY, assemblyRecipes);
        registration.addRecipes(JeiRecipeTypes.ENGINEERING, engineeringRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()), JeiRecipeTypes.ASSEMBLY);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()), JeiRecipeTypes.ENGINEERING);
    }
}