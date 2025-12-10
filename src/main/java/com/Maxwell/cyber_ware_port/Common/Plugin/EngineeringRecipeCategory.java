package com.Maxwell.cyber_ware_port.Common.Plugin;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.EngineeringRecipe;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

@SuppressWarnings("removal")
public class EngineeringRecipeCategory implements IRecipeCategory<EngineeringRecipe> {
    private final IDrawable background;

    private final IDrawable icon;

    public EngineeringRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(
                new ResourceLocation(CyberWare.MODID, "textures/gui/engineering.png"),
                0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()));

    }

    @Override
    public mezz.jei.api.recipe.RecipeType<EngineeringRecipe> getRecipeType() {
        return CyberwareJeiPlugin.ENGINEERING_TYPE;

    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.cyber_ware_port.deconstruct");

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
    public void setRecipe(IRecipeLayoutBuilder builder, EngineeringRecipe recipe, IFocusGroup focuses) {
        if (!recipe.getIngredients().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 15, 20)
                    .addIngredients(recipe.getIngredients().get(0));

        }
        builder.addSlot(RecipeIngredientRole.CATALYST, 15, 53)
                .addItemStack(new ItemStack(Items.PAPER))
                .addTooltipCallback((view, tooltip) -> {
                    tooltip.add(Component.translatable("gui.cyber_ware_port.need_paper").withStyle(ChatFormatting.GRAY));

                });
        int outputX = 71;
        int outputY = 17;
        List<EngineeringRecipe.OutputEntry> outputs = recipe.getOutputs();
        if (outputs != null) {
            int slotIdx = 0;
            for (EngineeringRecipe.OutputEntry entry : outputs) {
                if (slotIdx >= 6) break;
                int x = outputX + (slotIdx % 2) * 18;
                int y = outputY + (slotIdx / 2) * 18;
                builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                        .addItemStack(entry.stack())
                        .addTooltipCallback((view, tooltip) -> {
                            float chance = entry.chance() * 100;
                            tooltip.add(Component.literal(String.format("%.0f%% Chance", chance))
                                    .withStyle(ChatFormatting.YELLOW));

                        });
                slotIdx++;

            }
        }
        float bpChance = recipe.getBlueprintChance();
        if (bpChance > 0) {
            if (!recipe.getIngredients().isEmpty()) {
                Ingredient inputIng = recipe.getIngredients().get(0);
                ItemStack[] inputItems = inputIng.getItems();
                if (inputItems.length > 0) {
                    ItemStack blueprint = BlueprintItem.createBlueprintFor(inputItems[0].getItem());
                    if (!blueprint.isEmpty()) {
                        builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 53)
                                .addItemStack(blueprint)
                                .addTooltipCallback((view, tooltip) -> {
                                    tooltip.add(Component.translatable("gui.cyber_ware_port.blueprint_chance", String.format("%.0f", bpChance * 100))
                                            .withStyle(ChatFormatting.BLUE));

                                });

                    }
                }
            }
        }
    }
}