package com.maxwell.cyber_ware_port.common.block.cwb.recipe;

import com.maxwell.cyber_ware_port.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    public static final MapCodec<EngineeringRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("input").forGetter(EngineeringRecipe::input),
            OutputEntry.CODEC.codec().listOf().fieldOf("outputs").forGetter(EngineeringRecipe::outputs),
            Codec.FLOAT.optionalFieldOf("blueprint_chance", 0.5f).forGetter(EngineeringRecipe::blueprintChance)
    ).apply(inst, EngineeringRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EngineeringRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, EngineeringRecipe::input,
            OutputEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), EngineeringRecipe::outputs,
            ByteBufCodecs.FLOAT, EngineeringRecipe::blueprintChance,
            EngineeringRecipe::new
    );
    private final Ingredient input;
    private final List<OutputEntry> outputs;
    private final float blueprintChance;

    public EngineeringRecipe(Ingredient input, List<OutputEntry> outputs, float blueprintChance) {
        this.input = input;
        this.outputs = outputs;
        this.blueprintChance = blueprintChance;
    }

    @Override
    public boolean matches(SingleRecipeInput pInput, Level pLevel) {
        return input.test(pInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput singleRecipeInput) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends EngineeringRecipe> getSerializer() {
        return ModRecipes.ENGINEERING_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipes.ENGINEERING_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.input);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
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

    public List<ItemStack> rollOutputs(RandomSource random) {
        List<ItemStack> results = new ArrayList<>();
        for (OutputEntry entry : outputs) {
            if (random.nextFloat() < entry.chance()) {
                results.add(entry.stack().copy());
            }
        }
        return results;
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
}