package com.maxwell.cyber_ware_port.datagen;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new ModLanguageProvider(packOutput, "zh_cn"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(packOutput, "ru_ru"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(packOutput, "fr_fr"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(packOutput, "en_us"));
        generator.addProvider(event.includeServer(), new ModLanguageProvider(packOutput, "ja_jp"));
        generator.addProvider(event.includeServer(),
                new ModBiomeTagProvider(packOutput, lookupProvider, existingFileHelper));

    }
}