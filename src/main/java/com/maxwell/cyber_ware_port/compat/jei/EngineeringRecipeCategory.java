package com.maxwell.cyber_ware_port.compat.jei;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class EngineeringRecipeCategory implements IRecipeCategory<RecipeHolder<EngineeringRecipe>> {
    private static final Identifier BACKGROUND_LOC = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/engineering.png");
    private final IDrawable background;
    private final IDrawable icon;

    public EngineeringRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()));
    }

    @Override
    public IRecipeType<RecipeHolder<EngineeringRecipe>> getRecipeType() {
        return JeiRecipeTypes.ENGINEERING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.cyber_ware_port.deconstruct");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EngineeringRecipe> holder, IFocusGroup iFocusGroup) {
        EngineeringRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 20).addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.INPUT, 15, 53).addItemStack(new ItemStack(Items.PAPER));
        int outputX = 71;
        int outputY = 17;
        for (int i = 0; i < Math.min(recipe.outputs().size(), 6); i++) {
            var entry = recipe.outputs().get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, outputX + (i % 2) * 18, outputY + (i / 2) * 18)
                    .addItemStack(entry.template().create())
                    .addRichTooltipCallback((view, tooltip) -> {
                        tooltip.add(Component.literal(String.format("%.0f%% Chance", entry.chance() * 100)).withStyle(ChatFormatting.YELLOW));
                    });
        }
        float bpChance = recipe.blueprintChance();
        if (bpChance > 0) {
            recipe.input().items().findFirst().ifPresent(itemHolder -> {
                ItemStack blueprint = BlueprintItem.createBlueprintFor(itemHolder.value());
                builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 53)
                        .addItemStack(blueprint)
                        .addRichTooltipCallback((view, tooltip) ->
                                tooltip.add(Component.translatable("gui.cyber_ware_port.blueprint_chance", String.format("%.0f", bpChance * 100)).withStyle(ChatFormatting.BLUE))
                        );
            });
        }
    }

    @Override
    public void draw(RecipeHolder<EngineeringRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}