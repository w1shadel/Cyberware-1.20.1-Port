package com.Maxwell.cyber_ware_port.Datagen;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipie.AssemblyRecipe;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import com.Maxwell.cyber_ware_port.Init.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
@SuppressWarnings("removal")
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        new AssemblyRecipeBuilder(ModItems.CYBER_EYE.get())
                .requires(Items.IRON_INGOT, 2)
                .requires(Items.REDSTONE, 1)
                .requires(Items.GLASS_PANE, 1)
                .save(pWriter);

    }

    public static class AssemblyRecipeBuilder {
        private final Item result;
        private final List<AssemblyRecipe.SizedIngredient> ingredients = new ArrayList<>();

        public AssemblyRecipeBuilder(Item result) {
            this.result = result;
        }

        public AssemblyRecipeBuilder requires(Item item, int count) {
            ingredients.add(new AssemblyRecipe.SizedIngredient(Ingredient.of(item), count));
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Result(
                    new ResourceLocation(getItemName(result) + "_assembly"),
                    result,
                    ingredients
            ));
        }

        public static class Result implements FinishedRecipe {
            private final ResourceLocation id;
            private final Item result;
            private final List<AssemblyRecipe.SizedIngredient> ingredients;

            public Result(ResourceLocation id, Item result, List<AssemblyRecipe.SizedIngredient> ingredients) {
                this.id = id;
                this.result = result;
                this.ingredients = ingredients;
            }

            @Override
            public void serializeRecipeData(JsonObject json) {
                JsonArray inputs = new JsonArray();
                for (AssemblyRecipe.SizedIngredient ing : ingredients) {
                    JsonObject entry = new JsonObject();
                    entry.add("ingredient", ing.ingredient.toJson());
                    entry.addProperty("count", ing.count);
                    inputs.add(entry);
                }
                json.add("inputs", inputs);
                json.addProperty("output", getItemName(result));
            }

            @Override
            public ResourceLocation getId() { return id; }
            @Override
            public RecipeSerializer<?> getType() { return ModRecipes.ASSEMBLY_SERIALIZER.get(); }
            @Nullable
            @Override
            public JsonObject serializeAdvancement() { return null; }
            @Nullable
            @Override
            public ResourceLocation getAdvancementId() { return null; }
        }
    }
}