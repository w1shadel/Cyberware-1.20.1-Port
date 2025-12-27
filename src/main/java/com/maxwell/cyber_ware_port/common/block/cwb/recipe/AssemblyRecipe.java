package com.maxwell.cyber_ware_port.common.block.cwb.recipe;

import com.maxwell.cyber_ware_port.init.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("removal")
public class AssemblyRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final List<SizedIngredient> inputs;

    private final ItemStack output;

    public AssemblyRecipe(ResourceLocation id, List<SizedIngredient> inputs, ItemStack output) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;

    }

    public List<SizedIngredient> getInputs() {
        return inputs;

    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        return true;

    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();

    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ASSEMBLY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ASSEMBLY_TYPE.get();
    }

    public record SizedIngredient(Ingredient ingredient, int count) {
    }

    @SuppressWarnings("removal")
    public static class Serializer implements RecipeSerializer<AssemblyRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public AssemblyRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            List<SizedIngredient> inputs = new ArrayList<>();
            JsonArray inputArray = GsonHelper.getAsJsonArray(pJson, "inputs");
            for (int i = 0;
                 i < inputArray.size();
                 i++) {
                JsonObject entry = inputArray.get(i).getAsJsonObject();
                Ingredient ing;
                if (entry.has("ingredient")) {
                    ing = Ingredient.fromJson(entry.get("ingredient"));

                } else {
                    ing = Ingredient.fromJson(entry);

                }
                int count = GsonHelper.getAsInt(entry, "count", 1);
                inputs.add(new SizedIngredient(ing, count));

            }
            ResourceLocation outputId = new ResourceLocation(GsonHelper.getAsString(pJson, "output"));
            ItemStack outputStack = new ItemStack(ForgeRegistries.ITEMS.getValue(outputId));
            return new AssemblyRecipe(pRecipeId, inputs, outputStack);

        }

        @Override
        public @Nullable AssemblyRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int size = pBuffer.readInt();
            List<SizedIngredient> inputs = new ArrayList<>();
            for (int i = 0;
                 i < size;
                 i++) {
                Ingredient ing = Ingredient.fromNetwork(pBuffer);
                int count = pBuffer.readInt();
                inputs.add(new SizedIngredient(ing, count));

            }
            ItemStack output = pBuffer.readItem();
            return new AssemblyRecipe(pRecipeId, inputs, output);

        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, AssemblyRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputs.size());
            for (SizedIngredient entry : pRecipe.inputs) {
                entry.ingredient.toNetwork(pBuffer);
                pBuffer.writeInt(entry.count);

            }
            pBuffer.writeItem(pRecipe.output);

        }
    }
}