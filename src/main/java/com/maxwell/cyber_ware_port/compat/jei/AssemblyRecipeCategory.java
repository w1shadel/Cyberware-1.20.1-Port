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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AssemblyRecipeCategory implements IRecipeCategory<AssemblyRecipe> {
    public static final IRecipeType<AssemblyRecipe> TYPE =
            IRecipeType.create(CyberWare.MODID, "assembly", AssemblyRecipe.class);

    private static final Identifier BACKGROUND_LOC = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/engineering.png");
    private final IDrawable background;
    private final IDrawable icon;

    public AssemblyRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()));
    }

    @Override public @NotNull IRecipeType<AssemblyRecipe> getRecipeType() { return TYPE; }
    @Override public @NotNull Component getTitle() { return Component.translatable("gui.cyber_ware_port.assemble"); }
    @Override public int getWidth() { return 176; }
    @Override public int getHeight() { return 80; }
    @Override public @NotNull IDrawable getIcon() { return icon; }

    @Override
    public void draw(AssemblyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
            background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AssemblyRecipe recipe, IFocusGroup focuses) {
        HolderLookup.Provider registries = Minecraft.getInstance().level.registryAccess();
        int gridStartX = 71;
        int gridStartY = 17;

        List<AssemblyRecipe.SizedIngredient> inputs = recipe.getInputs();
        for (int i = 0; i < Math.min(inputs.size(), 6); i++) {
            AssemblyRecipe.SizedIngredient sizedIng = inputs.get(i);
            int x = gridStartX + (i % 2) * 18;
            int y = gridStartY + (i / 2) * 18;

            List<ItemStack> itemsWithCount = sizedIng.ingredient().items()
                    .map(holder -> {
                        ItemStack stack = new ItemStack(holder);
                        stack.setCount(sizedIng.count());
                        return stack;
                    }).toList();

            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addItemStacks(itemsWithCount);
        }

        ItemStack result = recipe.getResultItem(registries);
        ItemStack blueprint = BlueprintItem.createBlueprintFor(result.getItem());

        builder.addSlot(RecipeIngredientRole.INPUT, 115, 53)
                .add(blueprint);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 141, 21)
                .add(result);
    }
}