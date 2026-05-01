package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.AssemblyRecipe;
import com.maxwell.cyber_ware_port.common.block.cwb.recipe.EngineeringRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CyberWare.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CyberWare.MODID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AssemblyRecipe>> ASSEMBLY_SERIALIZER =
            SERIALIZERS.register("assembly", () -> new RecipeSerializer<>(
                    AssemblyRecipe.CODEC,
                    AssemblyRecipe.STREAM_CODEC
            ));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EngineeringRecipe>> ENGINEERING_SERIALIZER =
            SERIALIZERS.register("engineering", () -> new RecipeSerializer<>(
                    EngineeringRecipe.CODEC,
                    EngineeringRecipe.STREAM_CODEC
            ));
    public static final DeferredHolder<RecipeType<?>, RecipeType<AssemblyRecipe>> ASSEMBLY_TYPE =
            TYPES.register("assembly", () -> new RecipeType<AssemblyRecipe>() {
            });
    public static final DeferredHolder<RecipeType<?>, RecipeType<EngineeringRecipe>> ENGINEERING_TYPE =
            TYPES.register("engineering", () -> new RecipeType<EngineeringRecipe>() {
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}