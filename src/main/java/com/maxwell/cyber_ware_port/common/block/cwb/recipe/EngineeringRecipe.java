package com.maxwell.cyber_ware_port.common.block.cwb.recipe;

import com.maxwell.cyber_ware_port.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class EngineeringRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient input;
    private final List<OutputEntry> outputs;
    private final float blueprintChance;

    public EngineeringRecipe(Ingredient input, List<OutputEntry> outputs, float blueprintChance) {
        this.input = input;
        this.outputs = outputs;
        this.blueprintChance = blueprintChance;
    }

    public Ingredient input() {
        return input;
    }

    public List<OutputEntry> outputs() {
        return outputs;
    }

    public float blueprintChance() {
        return blueprintChance;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.input);
        return list;
    }

    public float getBlueprintChance() {
        return blueprintChance;
    }

    @Override
    public boolean matches(SingleRecipeInput pInput, Level pLevel) {
        return input.test(pInput.item());
    }

    public List<ItemStack> rollOutputs(RandomSource random) {
        List<ItemStack> results = new ArrayList<>();
        for (OutputEntry entry : outputs) {
            if (random.nextFloat() < entry.chance()) {
                results.add(entry.stack().copy());
            }
        }
        return results;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        if (!outputs.isEmpty()) {
            return outputs.get(0).stack();
        }
        return ItemStack.EMPTY;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ENGINEERING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ENGINEERING_TYPE.get();
    }

    public record OutputEntry(ItemStack stack, float chance) {
        public static final MapCodec<OutputEntry> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(OutputEntry::stack),
                Codec.FLOAT.fieldOf("chance").forGetter(OutputEntry::chance)
        ).apply(inst, OutputEntry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, OutputEntry> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, OutputEntry::stack,
                ByteBufCodecs.FLOAT, OutputEntry::chance,
                OutputEntry::new
        );
    }

    public static class Serializer implements RecipeSerializer<EngineeringRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<EngineeringRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("input").forGetter(EngineeringRecipe::input),
                OutputEntry.CODEC.codec().listOf().fieldOf("outputs").forGetter(EngineeringRecipe::outputs),
                Codec.FLOAT.optionalFieldOf("blueprint_chance", 0.5f).forGetter(EngineeringRecipe::blueprintChance)
        ).apply(inst, EngineeringRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, EngineeringRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, EngineeringRecipe::input,
                OutputEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), EngineeringRecipe::outputs,
                ByteBufCodecs.FLOAT, EngineeringRecipe::blueprintChance,
                EngineeringRecipe::new
        );

        @Override
        public MapCodec<EngineeringRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EngineeringRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }
}