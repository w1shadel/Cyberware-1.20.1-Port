package com.Maxwell.cyber_ware_port.Datagen;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.Recipe.AssemblyRecipe;

import com.Maxwell.cyber_ware_port.CyberWare;

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

import net.minecraftforge.registries.ForgeRegistries;


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
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {new AssemblyRecipeBuilder(ModItems.CYBER_EYE.get())
                .requires(ModItems.COMPONENT_PLATING.get(), 2)
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 2)
                .requires(ModItems.COMPONENT_SSC.get(), 1)
                .requires(Items.GLASS_PANE, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.LOW_LIGHT_VISION.get())
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 2)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .requires(Items.GLOWSTONE_DUST, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.LIQUID_REFRACTION.get())
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 1)
                .requires(Items.PRISMARINE_SHARD, 1)
                .requires(Items.GLASS_PANE, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.HUDJACK.get())
                .requires(ModItems.COMPONENT_SSC.get(), 2)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.TARGETING_OVERLAY.get())
                .requires(ModItems.COMPONENT_SSC.get(), 1)
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 1)
                .requires(Items.REDSTONE, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.DISTANCE_ENHANCER.get())
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 1)
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 1)
                .requires(Items.GLASS_PANE, 2)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.CORTICAL_STACK.get())
                .requires(ModItems.COMPONENT_STORAGE.get(), 2)
                .requires(ModItems.COMPONENT_SSC.get(), 1)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.ENDER_JAMMER.get())
                .requires(ModItems.COMPONENT_SSC.get(), 1)
                .requires(Items.ENDER_PEARL, 1)
                .requires(Items.OBSIDIAN, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.CONSCIOUSNESS_TRANSMITTER.get())
                .requires(ModItems.COMPONENT_SSC.get(), 2)
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 1)
                .requires(Items.ENDER_EYE, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.NEURAL_CONTEXTUALIZER.get())
                .requires(ModItems.COMPONENT_SSC.get(), 2)
                .requires(ModItems.COMPONENT_SYNTHNERVES.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.THREAT_MATRIX.get())
                .requires(ModItems.COMPONENT_SSC.get(), 2)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .requires(Items.SPIDER_EYE, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.CRANIAL_BROADCASTER.get())
                .requires(ModItems.COMPONENT_SSC.get(), 1)
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 1)
                .requires(Items.NOTE_BLOCK, 1)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.CARDIOMECHANIC_PUMP.get())
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.INTERNAL_DEFIBRILLATOR.get())
                .requires(ModItems.COMPONENT_STORAGE.get(), 1)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 2)
                .requires(Items.REDSTONE_BLOCK, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.PLATELET_DISPATCHER.get())
                .requires(ModItems.COMPONENT_REACTOR.get(), 1)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .requires(Items.SLIME_BALL, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.STEM_CELL_SYNTHESIZER.get())
                .requires(ModItems.COMPONENT_REACTOR.get(), 2)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .requires(Items.GHAST_TEAR, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.CARDIOVASCULAR_COUPLER.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 1)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .requires(ModItems.COMPONENT_SYNTHNERVES.get(), 1)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.COMPRESSED_OXYGEN.get())
                .requires(ModItems.COMPONENT_PLATING.get(), 2)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.HYPER_OXYGENATION.get())
                .requires(ModItems.COMPONENT_REACTOR.get(), 1)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 1)
                .requires(ModItems.COMPONENT_FULLERENE.get(), 1)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.LIVER_FILTER.get())
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .requires(ModItems.COMPONENT_FULLERENE.get(), 1)
                .requires(Items.CHARCOAL, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.METABOLIC_GENERATOR.get())
                .requires(ModItems.COMPONENT_REACTOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .requires(Items.PISTON, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.INTERNAL_BATTERY.get())
                .requires(ModItems.COMPONENT_STORAGE.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.ADRENALINE_PUMP.get())
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 1)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .requires(Items.SUGAR, 1)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.SOLARSKIN.get())
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 2)
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 1)
                .requires(Items.QUARTZ, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.SUBDERMAL_SPIKES.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 2)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.SYNTHETIC_SKIN.get())
                .requires(ModItems.COMPONENT_FULLERENE.get(), 2)
                .requires(ModItems.COMPONENT_SYNTHNERVES.get(), 1)
                .requires(Items.LEATHER, 2)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.TARGETED_IMMUNOSUPPRESSANT.get())
                .requires(ModItems.COMPONENT_REACTOR.get(), 1)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.WIRED_REFLEXES.get())
                .requires(ModItems.COMPONENT_SYNTHNERVES.get(), 2)
                .requires(ModItems.COMPONENT_MICROELECTRIC.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.MYOMER_MUSCLE.get())
                .requires(ModItems.COMPONENT_FULLERENE.get(), 2)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.BONELACING.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 2)
                .requires(ModItems.COMPONENT_FULLERENE.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.CITRATE_ENHANCEMENT.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 1)
                .requires(ModItems.COMPONENT_REACTOR.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.MARROW_BATTERY.get())
                .requires(ModItems.COMPONENT_STORAGE.get(), 2)
                .requires(ModItems.COMPONENT_TITANIUM.get(), 1)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.CYBER_ARM_LEFT.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 3)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.CYBER_ARM_RIGHT.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 3)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.CYBER_LEG_LEFT.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 3)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.CYBER_LEG_RIGHT.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 3)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.RAPID_FIRE_FLYWHEEL.get())
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .requires(Items.BOW, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.IMPLANTED_SPURS.get())
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .requires(Items.IRON_NUGGET, 4)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.FINE_MANIPULATORS.get())
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 4)
                .requires(ModItems.COMPONENT_FIBEROPTICS.get(), 2)
                .requires(Items.CRAFTING_TABLE, 1)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.RAPID_FIRE_FLYWHEEL.get())
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .addOutput(Items.BOW, 1, 0.5f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.IMPLANTED_SPURS.get())
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .addOutput(Items.IRON_NUGGET, 4, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.FINE_MANIPULATORS.get())
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 4, 1.0f)
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 2, 1.0f)
                .addOutput(Items.CRAFTING_TABLE, 1, 0.8f)
                .setBlueprintChance(0.15f)
                .save(pWriter);

        new AssemblyRecipeBuilder(ModItems.RETRACTABLE_CLAWS.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 2)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.REINFORCED_FIST.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 1)
                .requires(ModItems.COMPONENT_PLATING.get(), 2)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 1)
                .save(pWriter);
new AssemblyRecipeBuilder(ModItems.LINEAR_ACTUATORS.get())
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(Items.PISTON, 1)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.FALL_BRACERS.get())
                .requires(ModItems.COMPONENT_TITANIUM.get(), 2)
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 1)
                .requires(Items.WHITE_WOOL, 2)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.AQUATIC_PROPULSION.get())
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .requires(Items.IRON_INGOT, 2)
                .save(pWriter);


        new AssemblyRecipeBuilder(ModItems.DEPLOYABLE_WHEELS.get())
                .requires(ModItems.COMPONENT_ACTUATOR.get(), 2)
                .requires(ModItems.COMPONENT_PLATING.get(), 1)
                .save(pWriter);

        new EngineeringRecipeBuilder(ModItems.CYBER_EYE.get())
                .addOutput(ModItems.COMPONENT_PLATING.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_SSC.get(), 1, 1.0f)
                .addOutput(Items.GLASS_PANE, 1, 0.5f) 
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.LOW_LIGHT_VISION.get())
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .addOutput(Items.GLOWSTONE_DUST, 1, 0.5f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.LIQUID_REFRACTION.get())
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 1, 1.0f)
                .addOutput(Items.PRISMARINE_SHARD, 1, 1.0f)
                .addOutput(Items.GLASS_PANE, 1, 0.5f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.HUDJACK.get())
                .addOutput(ModItems.COMPONENT_SSC.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.TARGETING_OVERLAY.get())
                .addOutput(ModItems.COMPONENT_SSC.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 1, 1.0f)
                .addOutput(Items.REDSTONE, 1, 0.8f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.DISTANCE_ENHANCER.get())
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 1, 1.0f)
                .addOutput(Items.GLASS_PANE, 2, 0.5f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CORTICAL_STACK.get())
                .addOutput(ModItems.COMPONENT_STORAGE.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_SSC.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .setBlueprintChance(0.10f) 
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.ENDER_JAMMER.get())
                .addOutput(ModItems.COMPONENT_SSC.get(), 1, 1.0f)
                .addOutput(Items.ENDER_PEARL, 1, 0.0f) 
                .addOutput(Items.OBSIDIAN, 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CONSCIOUSNESS_TRANSMITTER.get())
                .addOutput(ModItems.COMPONENT_SSC.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 1, 1.0f)
                .addOutput(Items.ENDER_EYE, 1, 0.5f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.NEURAL_CONTEXTUALIZER.get())
                .addOutput(ModItems.COMPONENT_SSC.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_SYNTHNERVES.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.THREAT_MATRIX.get())
                .addOutput(ModItems.COMPONENT_SSC.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .addOutput(Items.SPIDER_EYE, 1, 0.5f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CRANIAL_BROADCASTER.get())
                .addOutput(ModItems.COMPONENT_SSC.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 1, 1.0f)
                .addOutput(Items.NOTE_BLOCK, 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CARDIOMECHANIC_PUMP.get())
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.INTERNAL_DEFIBRILLATOR.get())
                .addOutput(ModItems.COMPONENT_STORAGE.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 2, 1.0f)
                .addOutput(Items.REDSTONE_BLOCK, 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.PLATELET_DISPATCHER.get())
                .addOutput(ModItems.COMPONENT_REACTOR.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .addOutput(Items.SLIME_BALL, 1, 0.5f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.STEM_CELL_SYNTHESIZER.get())
                .addOutput(ModItems.COMPONENT_REACTOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .addOutput(Items.GHAST_TEAR, 1, 0.2f)
                .setBlueprintChance(0.10f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CARDIOVASCULAR_COUPLER.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_SYNTHNERVES.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.COMPRESSED_OXYGEN.get())
                .addOutput(ModItems.COMPONENT_PLATING.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.HYPER_OXYGENATION.get())
                .addOutput(ModItems.COMPONENT_REACTOR.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_FULLERENE.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.LIVER_FILTER.get())
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_FULLERENE.get(), 1, 1.0f)
                .addOutput(Items.CHARCOAL, 1, 0.5f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.METABOLIC_GENERATOR.get())
                .addOutput(ModItems.COMPONENT_REACTOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .addOutput(Items.PISTON, 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.INTERNAL_BATTERY.get())
                .addOutput(ModItems.COMPONENT_STORAGE.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.ADRENALINE_PUMP.get())
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .addOutput(Items.SUGAR, 1, 0.8f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.SOLARSKIN.get())
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_FIBEROPTICS.get(), 1, 1.0f)
                .addOutput(Items.QUARTZ, 1, 0.8f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.SUBDERMAL_SPIKES.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.SYNTHETIC_SKIN.get())
                .addOutput(ModItems.COMPONENT_FULLERENE.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_SYNTHNERVES.get(), 1, 1.0f)
                .addOutput(Items.LEATHER, 2, 0.8f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.TARGETED_IMMUNOSUPPRESSANT.get())
                .addOutput(ModItems.COMPONENT_REACTOR.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.WIRED_REFLEXES.get())
                .addOutput(ModItems.COMPONENT_SYNTHNERVES.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_MICROELECTRIC.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.MYOMER_MUSCLE.get())
                .addOutput(ModItems.COMPONENT_FULLERENE.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.BONELACING.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_FULLERENE.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CITRATE_ENHANCEMENT.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_REACTOR.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.MARROW_BATTERY.get())
                .addOutput(ModItems.COMPONENT_STORAGE.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CYBER_ARM_LEFT.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 3, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .setBlueprintChance(0.20f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CYBER_ARM_RIGHT.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 3, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .setBlueprintChance(0.20f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CYBER_LEG_LEFT.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 3, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .setBlueprintChance(0.20f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.CYBER_LEG_RIGHT.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 3, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .setBlueprintChance(0.20f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.RETRACTABLE_CLAWS.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.REINFORCED_FIST.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 1, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.LINEAR_ACTUATORS.get())
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(Items.PISTON, 1, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.FALL_BRACERS.get())
                .addOutput(ModItems.COMPONENT_TITANIUM.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 1, 1.0f)
                .addOutput(Items.WHITE_WOOL, 2, 0.8f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.AQUATIC_PROPULSION.get())
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .addOutput(Items.IRON_INGOT, 2, 1.0f)
                .setBlueprintChance(0.15f)
                .save(pWriter);


        new EngineeringRecipeBuilder(ModItems.DEPLOYABLE_WHEELS.get())
                .addOutput(ModItems.COMPONENT_ACTUATOR.get(), 2, 1.0f)
                .addOutput(ModItems.COMPONENT_PLATING.get(), 1, 1.0f)
                .setBlueprintChance(0.15f)
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
                    new ResourceLocation(CyberWare.MODID, getItemName(result) + "_assembly"),
                    result,
                    ingredients
            ));

        }

        private String getItemName(Item item) {
            return ForgeRegistries.ITEMS.getKey(item).getPath();

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

                json.addProperty("output", ForgeRegistries.ITEMS.getKey(result).toString());

            }

            @Override
            public ResourceLocation getId() { return id;
 }
            @Override
            public RecipeSerializer<?> getType() { return ModRecipes.ASSEMBLY_SERIALIZER.get();
 }
            @Nullable
            @Override
            public JsonObject serializeAdvancement() { return null;
 }
            @Nullable
            @Override
            public ResourceLocation getAdvancementId() { return null;
 }
        }
    }
    public static class EngineeringRecipeBuilder {
        private final Item input;

        private final List<OutputEntry> outputs = new ArrayList<>();

        private float blueprintChance = 0.0f;


        public EngineeringRecipeBuilder(Item input) {
            this.input = input;

        }

        public EngineeringRecipeBuilder addOutput(Item item, int count, float chance) {
            outputs.add(new OutputEntry(item, count, chance));

            return this;

        }

        public EngineeringRecipeBuilder setBlueprintChance(float chance) {
            this.blueprintChance = chance;

            return this;

        }

        public void save(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Result(
                    new ResourceLocation(CyberWare.MODID, getItemName(input) + "_engineering"),
                    input,
                    outputs,
                    blueprintChance
            ));

        }

        private String getItemName(Item item) {
            return ForgeRegistries.ITEMS.getKey(item).getPath();

        }

        private record OutputEntry(Item item, int count, float chance) {}

        public static class Result implements FinishedRecipe {
            private final ResourceLocation id;

            private final Item input;

            private final List<OutputEntry> outputs;

            private final float blueprintChance;


            public Result(ResourceLocation id, Item input, List<OutputEntry> outputs, float blueprintChance) {
                this.id = id;

                this.input = input;

                this.outputs = outputs;

                this.blueprintChance = blueprintChance;

            }

            @Override
            public void serializeRecipeData(JsonObject json) {

                JsonObject inputObj = new JsonObject();

                inputObj.addProperty("item", ForgeRegistries.ITEMS.getKey(input).toString());

                json.add("input", inputObj);


                JsonArray outputArr = new JsonArray();

                for (OutputEntry entry : outputs) {
                    JsonObject outObj = new JsonObject();

                    outObj.addProperty("item", ForgeRegistries.ITEMS.getKey(entry.item).toString());

                    outObj.addProperty("count", entry.count);

                    outObj.addProperty("chance", entry.chance);

                    outputArr.add(outObj);

                }
                json.add("outputs", outputArr);


                json.addProperty("blueprint_chance", blueprintChance);

            }

            @Override
            public ResourceLocation getId() { return id;
 }

            @Override
            public RecipeSerializer<?> getType() {

                return ModRecipes.ENGINEERING_SERIALIZER.get();

            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() { return null;
 }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() { return null;
 }
        }
    }

}