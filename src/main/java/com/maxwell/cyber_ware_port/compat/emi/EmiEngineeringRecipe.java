package com.maxwell.cyber_ware_port.compat.emi;

import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class EmiEngineeringRecipe implements EmiRecipe {
    private final EngineeringRecipe recipe;
    private final EmiIngredient input;
    private final List<EmiStack> outputs;
    private final EmiStack blueprint;

    public EmiEngineeringRecipe(EngineeringRecipe recipe) {
        this.recipe = recipe;
        this.input = EmiIngredient.of(recipe.getIngredients().get(0));
        this.outputs = new ArrayList<>();
        for (EngineeringRecipe.OutputEntry entry : recipe.getOutputs()) {
            this.outputs.add(EmiStack.of(entry.stack()));
        }

        ItemStack bpStack = ItemStack.EMPTY;
        if (recipe.getIngredients().get(0).getItems().length > 0) {
            bpStack = BlueprintItem.createBlueprintFor(recipe.getIngredients().get(0).getItems()[0].getItem());
        }
        this.blueprint = EmiStack.of(bpStack);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CyberwareEmiPlugin.ASSEMBLY_CATEGORY;
    }

    @Override
    public ResourceLocation getId() {
        return recipe.getId();
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        List<EmiStack> totalOutputs = new ArrayList<>(outputs);
        if (!blueprint.isEmpty()) totalOutputs.add(blueprint);
        return totalOutputs;
    }

    @Override
    public int getDisplayWidth() {
        return 176;
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(new EmiTexture(CyberwareEmiPlugin.GUI_TEXTURE, 0, 0, 176, 80), 0, 0);
        widgets.addSlot(input, 14, 19).drawBack(false);
        widgets.addSlot(EmiStack.of(Items.PAPER), 15, 53).drawBack(false)
                .appendTooltip(Component.translatable("gui.cyber_ware_port.need_paper").withStyle(ChatFormatting.GRAY));

        int gridX = 70;
        int gridY = 16;
        for (int i = 0; i < Math.min(outputs.size(), 6); i++) {
            float chance = recipe.getOutputs().get(i).chance() * 100;
            widgets.addSlot(outputs.get(i), gridX + (i % 2) * 18, gridY + (i / 2) * 18).drawBack(false)
                    .appendTooltip(Component.literal(String.format("%.0f%% Chance", chance)).withStyle(ChatFormatting.YELLOW));
        }

        if (!blueprint.isEmpty()) {
            float bpChance = recipe.getBlueprintChance() * 100;
            widgets.addSlot(blueprint, 114, 52).drawBack(false)
                    .appendTooltip(Component.translatable("gui.cyber_ware_port.blueprint_chance", String.format("%.0f", bpChance)).withStyle(ChatFormatting.BLUE));
        }
    }
}