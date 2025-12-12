package com.Maxwell.cyber_ware_port.Datagen;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Datagen.WorldGen.ModWorldGenProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        boolean includeServer = event.includeServer();
        generator.addProvider(includeServer, new ModLanguageProvider(packOutput, "ja_jp"));
        generator.addProvider(includeServer, new ModLanguageProvider(packOutput, "en_us"));
        generator.addProvider(includeServer, new ModLanguageProvider(packOutput, "fr_fr"));
        generator.addProvider(includeServer, new ModLanguageProvider(packOutput, "ru_ru"));
        generator.addProvider(includeServer, new ModLanguageProvider(packOutput, "zh_cn"));
        generator.addProvider(includeServer, new ModWorldGenProvider(packOutput, lookupProvider));
    }
}