package com.Maxwell.cyber_ware_port.Common.Plugin;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.AssemblyRecipe;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
@SuppressWarnings("removal")
public class AssemblyRecipeCategory implements IRecipeCategory<AssemblyRecipe> {
    private final IDrawable background;
    private final IDrawable icon;

    public AssemblyRecipeCategory(IGuiHelper helper) {this.background = helper.createDrawable(
                new ResourceLocation(CyberWare.MODID, "textures/gui/engineering.png"),
                0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()));
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<AssemblyRecipe> getRecipeType() {
        return CyberwareJeiPlugin.ASSEMBLY_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.cyber_ware_port.assemble");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AssemblyRecipe recipe, IFocusGroup focuses) {int gridStartX = 71;
        int gridStartY = 17;

        int slotIndex = 0;
        for (AssemblyRecipe.SizedIngredient input : recipe.getInputs()) {
            if (slotIndex >= 6) break; int x = gridStartX + (slotIndex % 2) * 18;
            int y = gridStartY + (slotIndex / 2) * 18;

            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addIngredients(input.ingredient);

            slotIndex++;
        }

        ItemStack blueprint = BlueprintItem.createBlueprintFor(recipe.getResultItem(null).getItem());
        builder.addSlot(RecipeIngredientRole.INPUT, 115, 53)
                .addItemStack(blueprint);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 141, 21)
                .addItemStack(recipe.getResultItem(null));
    }
}