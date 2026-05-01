package com.maxwell.cyber_ware_port.compat.jei;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class JeiRecipeTypes {
    @SuppressWarnings("unchecked")
    public static final IRecipeType<RecipeHolder<AssemblyRecipe>> ASSEMBLY =
            IRecipeType.create(Identifier.fromNamespaceAndPath(CyberWare.MODID, "assembly"), (Class<RecipeHolder<AssemblyRecipe>>) (Class<?>) RecipeHolder.class);
    @SuppressWarnings("unchecked")
    public static final IRecipeType<RecipeHolder<EngineeringRecipe>> ENGINEERING =
            IRecipeType.create(Identifier.fromNamespaceAndPath(CyberWare.MODID, "engineering"), (Class<RecipeHolder<EngineeringRecipe>>) (Class<?>) RecipeHolder.class);
}