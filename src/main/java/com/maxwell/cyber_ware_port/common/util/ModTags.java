package com.maxwell.cyber_ware_port.common.util;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> CYBERWARE = create("cyberware");

        public static final TagKey<Item> CYBERWARE_EYES = create("cyberware/eyes");
        public static final TagKey<Item> CYBERWARE_BRAIN = create("cyberware/brain");
        public static final TagKey<Item> CYBERWARE_HEART = create("cyberware/heart");
        public static final TagKey<Item> CYBERWARE_LUNGS = create("cyberware/lungs");
        public static final TagKey<Item> CYBERWARE_STOMACH = create("cyberware/stomach");
        public static final TagKey<Item> CYBERWARE_SKIN = create("cyberware/skin");
        public static final TagKey<Item> CYBERWARE_MUSCLE = create("cyberware/muscle");
        public static final TagKey<Item> CYBERWARE_BONES = create("cyberware/bones");
        public static final TagKey<Item> CYBERWARE_ARMS = create("cyberware/arms");
        public static final TagKey<Item> CYBERWARE_HANDS = create("cyberware/hands");
        public static final TagKey<Item> CYBERWARE_LEGS = create("cyberware/legs");
        public static final TagKey<Item> CYBERWARE_BOOTS = create("cyberware/boots");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, name));
        }
    }
}
