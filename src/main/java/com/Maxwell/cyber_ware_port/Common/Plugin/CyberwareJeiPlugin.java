package com.Maxwell.cyber_ware_port.Common.Plugin;


import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.AssemblyRecipe;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.EngineeringRecipe;

import com.Maxwell.cyber_ware_port.CyberWare;

import com.Maxwell.cyber_ware_port.Init.ModBlocks;

import com.Maxwell.cyber_ware_port.Init.ModRecipes;

import mezz.jei.api.IModPlugin;

import mezz.jei.api.JeiPlugin;

import mezz.jei.api.recipe.RecipeType;

import mezz.jei.api.registration.IRecipeCatalystRegistration;

import mezz.jei.api.registration.IRecipeCategoryRegistration;

import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.client.Minecraft;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.crafting.RecipeManager;


import java.util.List;

@SuppressWarnings("removal")
@JeiPlugin
public class CyberwareJeiPlugin implements IModPlugin {

    public static final RecipeType<AssemblyRecipe> ASSEMBLY_TYPE =
            RecipeType.create(CyberWare.MODID, "assembly", AssemblyRecipe.class);


    public static final RecipeType<EngineeringRecipe> ENGINEERING_TYPE =
            RecipeType.create(CyberWare.MODID, "engineering", EngineeringRecipe.class);


    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CyberWare.MODID, "jei_plugin");

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new AssemblyRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new EngineeringRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Minecraft.getInstance().level.getRecipeManager();


        List<AssemblyRecipe> assemblyRecipes = rm.getAllRecipesFor(ModRecipes.ASSEMBLY_TYPE.get());

        registration.addRecipes(ASSEMBLY_TYPE, assemblyRecipes);


        List<EngineeringRecipe> engineeringRecipes = rm.getAllRecipesFor(ModRecipes.ENGINEERING_TYPE.get());

        registration.addRecipes(ENGINEERING_TYPE, engineeringRecipes);

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()), ASSEMBLY_TYPE);

        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()), ENGINEERING_TYPE);

    }
}