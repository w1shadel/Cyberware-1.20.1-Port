package com.maxwell.cyber_ware_port.compat;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
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

@SuppressWarnings("removal")
public class CyberwareEmiRecipes {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID,
            "textures/gui/engineering.png");
    private static final EmiTexture BACKGROUND = new EmiTexture(TEXTURE, 0, 0, 176, 80);

    public static class Assembly implements EmiRecipe {
        private final ResourceLocation id;
        private final List<EmiIngredient> inputs;
        private final List<EmiStack> outputs;
        private final AssemblyRecipe recipe;

        public Assembly(AssemblyRecipe recipe) {
            this.recipe = recipe;
            this.id = recipe.getId();
            this.inputs = new ArrayList<>();
            for (AssemblyRecipe.SizedIngredient input : recipe.getInputs()) {
                this.inputs.add(EmiIngredient.of(input.ingredient()));
            }
            ItemStack blueprint = BlueprintItem.createBlueprintFor(recipe.getResultItem(null).getItem());
            if (!blueprint.isEmpty()) {
                this.inputs.add(EmiStack.of(blueprint));
            }
            this.outputs = List.of(EmiStack.of(recipe.getResultItem(null)));
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return CyberwareEmiPlugin.ASSEMBLY;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public List<EmiIngredient> getInputs() {
            return inputs;
        }

        @Override
        public List<EmiStack> getOutputs() {
            return outputs;
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
            widgets.addTexture(BACKGROUND, 0, 0);
            int gridStartX = 71;
            int gridStartY = 17;
            int slotIndex = 0;
            for (int i = 0; i < inputs.size(); i++) {
                if (i == inputs.size() - 1
                        && !BlueprintItem.createBlueprintFor(recipe.getResultItem(null).getItem()).isEmpty()) {
                    widgets.addSlot(inputs.get(i), 115, 53).drawBack(false);
                    continue;
                }
                if (slotIndex >= 6)
                    break;
                int x = gridStartX + (slotIndex % 2) * 18;
                int y = gridStartY + (slotIndex / 2) * 18;
                widgets.addSlot(inputs.get(i), x, y).drawBack(false);
                slotIndex++;
            }
            if (!outputs.isEmpty()) {
                widgets.addSlot(outputs.get(0), 141, 21).recipeContext(this).drawBack(false);
            }
        }
    }

    public static class Engineering implements EmiRecipe {
        private final ResourceLocation id;
        private final List<EmiIngredient> inputs;
        private final List<EmiStack> outputs;
        private final EngineeringRecipe recipe;

        public Engineering(EngineeringRecipe recipe) {
            this.recipe = recipe;
            this.id = recipe.getId();
            this.inputs = new ArrayList<>();
            if (!recipe.getIngredients().isEmpty()) {
                this.inputs.add(EmiIngredient.of(recipe.getIngredients().get(0)));
            }
            this.inputs.add(EmiStack.of(Items.PAPER));
            this.outputs = new ArrayList<>();
            if (recipe.getOutputs() != null) {
                for (EngineeringRecipe.OutputEntry entry : recipe.getOutputs()) {
                    this.outputs.add(EmiStack.of(entry.stack()));
                }
            }
            float bpChance = recipe.getBlueprintChance();
            if (bpChance > 0 && !recipe.getIngredients().isEmpty()) {
                ItemStack[] inputItems = recipe.getIngredients().get(0).getItems();
                if (inputItems.length > 0) {
                    ItemStack blueprint = BlueprintItem.createBlueprintFor(inputItems[0].getItem());
                    if (!blueprint.isEmpty()) {
                        this.outputs.add(EmiStack.of(blueprint));
                    }
                }
            }
        }

        @Override
        public EmiRecipeCategory getCategory() {
            return CyberwareEmiPlugin.ENGINEERING;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public List<EmiIngredient> getInputs() {
            return inputs;
        }

        @Override
        public List<EmiStack> getOutputs() {
            return outputs;
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
            widgets.addTexture(BACKGROUND, 0, 0);
            if (!inputs.isEmpty()) {
                widgets.addSlot(inputs.get(0), 15, 20).drawBack(false);
            }
            widgets.addSlot(EmiStack.of(Items.PAPER), 15, 53).drawBack(false)
                    .catalyst(true)
                    .appendTooltip(
                            Component.translatable("gui.cyber_ware_port.need_paper").withStyle(ChatFormatting.GRAY));
            int outputX = 71;
            int outputY = 17;
            int outputIndex = 0;
            List<EngineeringRecipe.OutputEntry> recipeOutputs = recipe.getOutputs();
            if (recipeOutputs != null) {
                for (EngineeringRecipe.OutputEntry entry : recipeOutputs) {
                    if (outputIndex >= 6)
                        break;
                    int x = outputX + (outputIndex % 2) * 18;
                    int y = outputY + (outputIndex / 2) * 18;
                    if (outputIndex < outputs.size()) {
                        EmiStack stack = EmiStack.of(entry.stack());
                        widgets.addSlot(stack, x, y).drawBack(false)
                                .recipeContext(this)
                                .appendTooltip(Component.literal(String.format("%.0f%% Chance", entry.chance() * 100))
                                        .withStyle(ChatFormatting.YELLOW));
                    }
                    outputIndex++;
                }
            }
            float bpChance = recipe.getBlueprintChance();
            if (bpChance > 0) {
                if (!outputs.isEmpty()) {
                    EmiStack bpStack = outputs.get(outputs.size() - 1);
                    if (bpStack.getItemStack().getItem() instanceof BlueprintItem) {
                        widgets.addSlot(bpStack, 115, 53).drawBack(false)
                                .recipeContext(this)
                                .appendTooltip(Component
                                        .translatable("gui.cyber_ware_port.blueprint_chance",
                                                String.format("%.0f", bpChance * 100))
                                        .withStyle(ChatFormatting.BLUE));
                    }
                }
            }
        }
    }
}
