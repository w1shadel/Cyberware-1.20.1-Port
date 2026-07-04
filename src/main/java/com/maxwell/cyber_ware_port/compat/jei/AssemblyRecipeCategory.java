package com.maxwell.cyber_ware_port.compat.jei;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AssemblyRecipeCategory implements IRecipeCategory<AssemblyRecipe> {
    public static final RecipeType<AssemblyRecipe> RECIPE_TYPE = RecipeType.create(CyberWare.MODID, "assembly", AssemblyRecipe.class);
    private static final ResourceLocation BACKGROUND_LOC = new ResourceLocation(CyberWare.MODID, "textures/gui/engineering.png");
    private final IDrawable background;
    private final IDrawable icon;

    public AssemblyRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()));
    }

    @Override
    public @NotNull RecipeType<AssemblyRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.cyber_ware_port.assemble");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AssemblyRecipe recipe, IFocusGroup focuses) {
        int gridStartX = 71;
        int gridStartY = 17;
        for (int i = 0; i < Math.min(recipe.getInputs().size(), 6); i++) {
            int x = gridStartX + (i % 2) * 18;
            int y = gridStartY + (i / 2) * 18;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y).addIngredients(recipe.getInputs().get(i).ingredient());
        }
        ItemStack result = recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY);
        builder.addSlot(RecipeIngredientRole.INPUT, 115, 53).addItemStack(BlueprintItem.createBlueprintFor(result.getItem()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 141, 21).addItemStack(result);
    }
}
