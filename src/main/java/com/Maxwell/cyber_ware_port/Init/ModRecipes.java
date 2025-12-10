package com.Maxwell.cyber_ware_port.Init;

import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.AssemblyRecipe;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.EngineeringRecipe;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CyberWare.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CyberWare.MODID);
    public static final RegistryObject<RecipeSerializer<EngineeringRecipe>> ENGINEERING_SERIALIZER =
            SERIALIZERS.register("engineering", () -> EngineeringRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<EngineeringRecipe>> ENGINEERING_TYPE =
            TYPES.register("engineering", () -> new RecipeType<EngineeringRecipe>() {
                @Override
                public String toString() {
                    return "engineering";
                }
            });
    public static final RegistryObject<RecipeSerializer<AssemblyRecipe>> ASSEMBLY_SERIALIZER =
            SERIALIZERS.register("assembly", () -> AssemblyRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<AssemblyRecipe>> ASSEMBLY_TYPE =
            TYPES.register("assembly", () -> new RecipeType<AssemblyRecipe>() {
                @Override
                public String toString() {
                    return "assembly";

                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}