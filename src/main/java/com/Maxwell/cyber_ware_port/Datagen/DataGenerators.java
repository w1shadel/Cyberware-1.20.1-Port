package com.Maxwell.cyber_ware_port.Datagen;


import com.Maxwell.cyber_ware_port.CyberWare;

import com.Maxwell.cyber_ware_port.Datagen.Loot.ModGlobalLootModifiersProvider;

import net.minecraft.core.HolderLookup;

import net.minecraft.core.RegistrySetBuilder;

import net.minecraft.data.DataGenerator;

import net.minecraft.data.PackOutput;

import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import net.minecraftforge.common.data.ExistingFileHelper;

import net.minecraftforge.data.event.GatherDataEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.registries.ForgeRegistries;


import java.util.Collections;

import java.util.concurrent.CompletableFuture;


@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        PackOutput packOutput = generator.getPackOutput();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

//        generator.addProvider(event.includeServer(), new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper));

//        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));

//      generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));

    //   generator.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, "en_us"));

        generator.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, "ja_jp"));

//        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));

//        generator.addProvider(event.includeServer(), new ModGlobalLootModifiersProvider(packOutput));

//        RegistrySetBuilder builder = new RegistrySetBuilder()
//                .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

//
//        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
//                packOutput,
//                lookupProvider,
//                builder,
//                Collections.singleton(CyberWare.MODID)
//        ));

    }
}