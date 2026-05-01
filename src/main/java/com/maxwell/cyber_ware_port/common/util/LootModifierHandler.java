package com.maxwell.cyber_ware_port.common.util;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber(modid = CyberWare.MODID)
public class LootModifierHandler {
    private static final Identifier MINESHAFT_CHEST =
            Identifier.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft");

    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        if (MINESHAFT_CHEST.equals(event.getName())) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(ModItems.ROBO_SURGEON.get()))
                    .setRolls(ConstantValue.exactly(1))
                    .build());
        }
    }
}