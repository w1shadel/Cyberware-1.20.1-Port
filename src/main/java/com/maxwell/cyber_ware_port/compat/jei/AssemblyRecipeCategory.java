package com.maxwell.cyber_ware_port.compat.jei;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public final class AssemblyRecipeCategory implements IRecipeCategory<RecipeHolder<AssemblyRecipe>> {
    private static final Identifier BACKGROUND_LOC = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/engineering.png");
    private final IDrawable background;
    private final IDrawable icon;

    public AssemblyRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()));
    }

    @Override
    public IRecipeType<RecipeHolder<AssemblyRecipe>> getRecipeType() {
        return JeiRecipeTypes.ASSEMBLY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.cyber_ware_port.assemble");
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AssemblyRecipe> holder, IFocusGroup focuses) {
        AssemblyRecipe recipe = holder.value();
        var provider = net.minecraft.client.Minecraft.getInstance().level.registryAccess();
        int gridStartX = 71;
        int gridStartY = 17;
        List<AssemblyRecipe.SizedIngredient> inputs = recipe.getInputs();
        for (int i = 0; i < Math.min(inputs.size(), 6); i++) {
            AssemblyRecipe.SizedIngredient sizedIng = inputs.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, gridStartX + (i % 2) * 18, gridStartY + (i / 2) * 18)
                    .addIngredients(sizedIng.ingredient());
        }
        ItemStack result = recipe.getResultItem(provider);
        ItemStack blueprint = BlueprintItem.createBlueprintFor(result.getItem());
        builder.addSlot(RecipeIngredientRole.INPUT, 115, 53).addItemStack(blueprint);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 141, 21).addItemStack(result);
    }

    @Override
    public void draw(RecipeHolder<AssemblyRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}