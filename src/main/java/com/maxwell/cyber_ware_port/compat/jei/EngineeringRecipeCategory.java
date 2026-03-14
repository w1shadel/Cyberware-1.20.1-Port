package com.maxwell.cyber_ware_port.compat.jei;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class EngineeringRecipeCategory implements IRecipeCategory<EngineeringRecipe> {
    public static final RecipeType<EngineeringRecipe> RECIPE_TYPE = RecipeType.create(CyberWare.MODID, "engineering", EngineeringRecipe.class);
    private static final ResourceLocation BACKGROUND_LOC = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/engineering.png");

    private final IDrawable background;
    private final IDrawable icon;

    public EngineeringRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()));
    }

    @Override
    public @NotNull RecipeType<EngineeringRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.cyber_ware_port.deconstruct");
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
    public void setRecipe(IRecipeLayoutBuilder builder, EngineeringRecipe recipe, IFocusGroup focuses) {
        Ingredient inputIng = recipe.getIngredients().get(0);
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 20)
                .addIngredients(inputIng);

        builder.addSlot(RecipeIngredientRole.CATALYST, 15, 53)
                .addItemStack(new ItemStack(Items.PAPER))
                .addTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("gui.cyber_ware_port.need_paper").withStyle(ChatFormatting.GRAY))
                );

        int outputX = 71;
        int outputY = 17;
        for (int i = 0; i < Math.min(recipe.outputs().size(), 6); i++) {
            EngineeringRecipe.OutputEntry entry = recipe.outputs().get(i);
            int x = outputX + (i % 2) * 18;
            int y = outputY + (i / 2) * 18;

            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStack(entry.stack())
                    .addTooltipCallback((view, tooltip) -> {
                        float chance = entry.chance() * 100;
                        tooltip.add(Component.literal(String.format("%.0f%% Chance", chance)).withStyle(ChatFormatting.YELLOW));
                    });
        }

        float bpChance = recipe.getBlueprintChance();
        if (bpChance > 0 && inputIng.getItems().length > 0) {
            ItemStack blueprint = BlueprintItem.createBlueprintFor(inputIng.getItems()[0].getItem());
            if (!blueprint.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 53)
                        .addItemStack(blueprint)
                        .addTooltipCallback((view, tooltip) ->
                                tooltip.add(Component.translatable("gui.cyber_ware_port.blueprint_chance", String.format("%.0f", bpChance * 100)).withStyle(ChatFormatting.BLUE))
                        );
            }
        }
    }
}