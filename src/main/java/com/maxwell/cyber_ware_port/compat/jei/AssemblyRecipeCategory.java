package com.maxwell.cyber_ware_port.compat.jei;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class AssemblyRecipeCategory implements IRecipeCategory<AssemblyRecipe> {
    public static final RecipeType<AssemblyRecipe> RECIPE_TYPE = RecipeType.create(CyberWare.MODID, "assembly", AssemblyRecipe.class);
    private static final Identifier BACKGROUND_LOC = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/engineering.png");
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
        HolderLookup.Provider registries = Minecraft.getInstance().level.registryAccess();
        int gridStartX = 71;
        int gridStartY = 17;
        for (int i = 0; i < Math.min(recipe.getInputs().size(), 6); i++) {
            AssemblyRecipe.SizedIngredient input = recipe.getInputs().get(i);
            int x = gridStartX + (i % 2) * 18;
            int y = gridStartY + (i / 2) * 18;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addIngredients(input.ingredient());
        }
        ItemStack result = recipe.getResultItem(registries);
        ItemStack blueprint = BlueprintItem.createBlueprintFor(result.getItem());
        builder.addSlot(RecipeIngredientRole.INPUT, 115, 53)
                .addItemStack(blueprint);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 141, 21)
                .addItemStack(result);
    }
}