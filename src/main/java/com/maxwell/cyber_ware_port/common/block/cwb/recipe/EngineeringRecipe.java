package com.maxwell.cyber_ware_port.common.block.cwb.recipe;

import com.maxwell.cyber_ware_port.init.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
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

public class EngineeringRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;

    private final Ingredient input;

    private final List<OutputEntry> outputs;
    private final float blueprintChance;

    public EngineeringRecipe(ResourceLocation id, Ingredient input, List<OutputEntry> outputs, float blueprintChance) {
        this.id = id;
        this.input = input;
        this.outputs = outputs;
        this.blueprintChance = blueprintChance;

    }
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(this.input);
        return nonNullList;
    }
    public float getBlueprintChance() {
        return blueprintChance;

    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        return input.test(pContainer.getItem(0));

    }

    public List<ItemStack> rollOutputs(RandomSource random) {
        List<ItemStack> results = new ArrayList<>();
        for (OutputEntry entry : outputs) {
            if (random.nextFloat() < entry.chance) {
                results.add(entry.stack.copy());

            }
        }
        return results;

    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;

    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;

    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;

    }

    @Override
    public ResourceLocation getId() {
        return id;

    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ENGINEERING_SERIALIZER.get();

    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ENGINEERING_TYPE.get();

    }

    public List<OutputEntry> getOutputs() {
        return this.outputs;

    }

    public record OutputEntry(ItemStack stack, float chance) {
    }

    public static class Serializer implements RecipeSerializer<EngineeringRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public EngineeringRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            JsonArray outputArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "outputs");
            List<OutputEntry> outputList = new ArrayList<>();
            for (int i = 0;
                 i < outputArray.size();
                 i++) {
                JsonObject entry = outputArray.get(i).getAsJsonObject();
                ResourceLocation itemLoc = new ResourceLocation(GsonHelper.getAsString(entry, "item"));
                int count = GsonHelper.getAsInt(entry, "count", 1);
                float chance = GsonHelper.getAsFloat(entry, "chance", 1.0f);
                ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(itemLoc), count);
                outputList.add(new OutputEntry(stack, chance));

            }
            float blueprintChance = GsonHelper.getAsFloat(pSerializedRecipe, "blueprint_chance", 0.5f);
            return new EngineeringRecipe(pRecipeId, input, outputList, blueprintChance);

        }

        @Override
        public @Nullable EngineeringRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            float blueprintChance = pBuffer.readFloat();
            int size = pBuffer.readInt();
            List<OutputEntry> outputs = new ArrayList<>();
            for (int i = 0;
                 i < size;
                 i++) {
                ItemStack stack = pBuffer.readItem();
                float chance = pBuffer.readFloat();
                outputs.add(new OutputEntry(stack, chance));

            }
            return new EngineeringRecipe(pRecipeId, input, outputs, blueprintChance);

        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, EngineeringRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeFloat(pRecipe.blueprintChance);
            pBuffer.writeInt(pRecipe.outputs.size());
            for (OutputEntry entry : pRecipe.outputs) {
                pBuffer.writeItem(entry.stack);
                pBuffer.writeFloat(entry.chance);

            }
        }
    }
}