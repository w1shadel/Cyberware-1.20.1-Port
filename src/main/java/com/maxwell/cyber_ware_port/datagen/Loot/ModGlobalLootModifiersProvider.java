package com.maxwell.cyber_ware_port.datagen.Loot;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, CyberWare.MODID);

    }

    @Override
    protected void start() {
        add("robosurgeon_in_nether_bridge", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/nether_bridge")).build(),
                        LootItemRandomChanceCondition.randomChance(0.3f).build()
                },
                ModBlocks.ROBO_SURGEON.get().asItem()
        ));

    }
}