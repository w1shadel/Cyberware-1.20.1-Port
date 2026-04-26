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
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EngineeringRecipeCategory implements IRecipeCategory<EngineeringRecipe> {
    public static final IRecipeType<EngineeringRecipe> TYPE =
            IRecipeType.create(CyberWare.MODID, "engineering", EngineeringRecipe.class);

    private static final Identifier BACKGROUND_LOC = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/engineering.png");
    private final IDrawable background;
    private final IDrawable icon;

    public EngineeringRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 176, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CYBERWARE_WORKBENCH.get()));
    }

    @Override public @NotNull IRecipeType<EngineeringRecipe> getRecipeType() { return TYPE; }
    @Override public @NotNull Component getTitle() { return Component.translatable("gui.cyber_ware_port.deconstruct"); }
    @Override public int getWidth() { return 176; }
    @Override public int getHeight() { return 80; }
    @Override public @NotNull IDrawable getIcon() { return icon; }

    @Override
    public void draw(EngineeringRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EngineeringRecipe recipe, IFocusGroup focuses) {
        Ingredient inputIng = recipe.input();

        builder.addSlot(RecipeIngredientRole.INPUT, 15, 20)
                .add(inputIng);


        builder.addSlot(RecipeIngredientRole.INPUT, 15, 53)
                .add(new ItemStack(Items.PAPER))

                .addRichTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("gui.cyber_ware_port.need_paper").withStyle(ChatFormatting.GRAY))
                );

        int outputX = 71;
        int outputY = 17;
        List<EngineeringRecipe.OutputEntry> outputs = recipe.outputs();
        for (int i = 0; i < Math.min(outputs.size(), 6); i++) {
            EngineeringRecipe.OutputEntry entry = outputs.get(i);
            int x = outputX + (i % 2) * 18;
            int y = outputY + (i / 2) * 18;

            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .add(entry.stack())
                    .addRichTooltipCallback((view, tooltip) -> {
                        float chance = entry.chance() * 100;
                        tooltip.add(Component.literal(String.format("%.0f%% Chance", chance)).withStyle(ChatFormatting.YELLOW));
                    });
        }

        float bpChance = recipe.blueprintChance();
        if (bpChance > 0) {
            ItemStack blueprint = inputIng.items()
                    .findFirst()
                    .map(Holder::value)
                    .map(BlueprintItem::createBlueprintFor)
                    .orElse(ItemStack.EMPTY);

            if (!blueprint.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 53)
                        .add(blueprint)
                        .addRichTooltipCallback((view, tooltip) ->
                                tooltip.add(Component.translatable("gui.cyber_ware_port.blueprint_chance", String.format("%.0f", bpChance * 100)).withStyle(ChatFormatting.BLUE))
                        );
            }
        }
    }
}