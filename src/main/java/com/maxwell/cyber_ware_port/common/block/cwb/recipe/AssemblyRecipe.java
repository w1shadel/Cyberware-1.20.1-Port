package com.maxwell.cyber_ware_port.common.block.cwb.recipe;

import com.maxwell.cyber_ware_port.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class AssemblyRecipe implements Recipe<RecipeInput> {
    private final List<SizedIngredient> inputs;
    private final ItemStack output;

    public AssemblyRecipe(List<SizedIngredient> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public List<SizedIngredient> getInputs() {
        return inputs;
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
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
        public static final MapCodec<SizedIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
                Codec.INT.optionalFieldOf("count", 1).forGetter(SizedIngredient::count)
        ).apply(inst, SizedIngredient::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SizedIngredient::ingredient,
                ByteBufCodecs.VAR_INT, SizedIngredient::count,
                SizedIngredient::new
        );
    }

    public static class Serializer implements RecipeSerializer<AssemblyRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<AssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.CODEC.codec().listOf().fieldOf("inputs").forGetter(r -> r.inputs),
                ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(inst, AssemblyRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, AssemblyRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.inputs,
                ItemStack.STREAM_CODEC, r -> r.output,
                AssemblyRecipe::new
        );

        @Override
        public MapCodec<AssemblyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AssemblyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}