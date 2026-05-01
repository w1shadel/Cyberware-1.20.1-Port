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
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class AssemblyRecipe implements Recipe<RecipeInput> {
    public static final MapCodec<AssemblyRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SizedIngredient.CODEC.codec().listOf().fieldOf("inputs").forGetter(r -> r.inputs),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(r -> r.outputTemplate)
    ).apply(inst, AssemblyRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AssemblyRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(java.util.ArrayList::new, SizedIngredient.STREAM_CODEC), r -> r.inputs,
            ItemStackTemplate.STREAM_CODEC, r -> r.outputTemplate,
            AssemblyRecipe::new
    );
    private final List<SizedIngredient> inputs;
    private final ItemStackTemplate outputTemplate;

    public AssemblyRecipe(List<SizedIngredient> inputs, ItemStackTemplate outputTemplate) {
        this.inputs = inputs;
        this.outputTemplate = outputTemplate;
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        if (pInput.size() < inputs.size()) return false;
        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).ingredient().test(pInput.getItem(i))) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput pInput) {
        return outputTemplate.create();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "cyberware";
    }

    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return outputTemplate.create();
    }

    @Override
    public RecipeSerializer<? extends AssemblyRecipe> getSerializer() {
        return ModRecipes.ASSEMBLY_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return ModRecipes.ASSEMBLY_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(inputs.stream().map(SizedIngredient::ingredient).toList());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public List<SizedIngredient> getInputs() {
        return inputs;
    }

    public ItemStackTemplate getOutputTemplate() {
        return outputTemplate;
    }

    public record SizedIngredient(Ingredient ingredient, int count) {
        public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredient> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, SizedIngredient::ingredient,
                ByteBufCodecs.VAR_INT, SizedIngredient::count,
                SizedIngredient::new
        );
        private static final Codec<Ingredient> FLEXIBLE_INGREDIENT_CODEC = Codec.either(
                Ingredient.CODEC,
                RecordCodecBuilder.<Ingredient>create(i -> i.group(
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ing -> null)
                ).apply(i, Ingredient::of))
        ).xmap(
                either -> either.map(java.util.function.Function.identity(), java.util.function.Function.identity()),
                com.mojang.datafixers.util.Either::left
        );
        public static final MapCodec<SizedIngredient> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                FLEXIBLE_INGREDIENT_CODEC.fieldOf("ingredient").forGetter(SizedIngredient::ingredient),
                Codec.INT.optionalFieldOf("count", 1).forGetter(SizedIngredient::count)
        ).apply(inst, SizedIngredient::new));
    }

}