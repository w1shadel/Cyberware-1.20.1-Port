package com.maxwell.cyber_ware_port.compat.emi;

import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EmiAssemblyRecipe implements EmiRecipe {
    private final AssemblyRecipe recipe;
    private final List<EmiIngredient> inputs;
    private final EmiStack output;
    private final EmiStack blueprint;

    public EmiAssemblyRecipe(AssemblyRecipe recipe) {
        this.recipe = recipe;
        this.inputs = new ArrayList<>();

        for (AssemblyRecipe.SizedIngredient input : recipe.getInputs()) {
            this.inputs.add(EmiIngredient.of(input.ingredient(), input.count()));
        }

        // 1.20.1 の getResultItem 呼び出し
        ItemStack result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        this.output = EmiStack.of(result);
        this.blueprint = EmiStack.of(BlueprintItem.createBlueprintFor(result.getItem()));
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
        List<EmiIngredient> totalInputs = new ArrayList<>(inputs);
        totalInputs.add(blueprint);
        return totalInputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
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

        int gridX = 71;
        int gridY = 17;
        for (int i = 0; i < Math.min(inputs.size(), 6); i++) {
            widgets.addSlot(inputs.get(i), gridX + (i % 2) * 18, gridY + (i / 2) * 18).drawBack(false);
        }

        widgets.addSlot(blueprint, 115, 53).drawBack(false);
        widgets.addSlot(output, 141, 21).drawBack(false).recipeContext(this);
    }
}