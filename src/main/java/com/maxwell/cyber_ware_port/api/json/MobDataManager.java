package com.maxwell.cyber_ware_port.api.json;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobDataManager extends SimpleJsonResourceReloadListener {
    public static final Map<EntityType<?>, MobData> MOB_DATA = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public MobDataManager() {
        super(GSON, "cyberware/mobs");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        MOB_DATA.clear();
        pObject.forEach((location, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();
                if (!json.has("mob")) return;
                ResourceLocation mobId = ResourceLocation.parse(json.get("mob").getAsString());
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(mobId);
                if (entityType != BuiltInRegistries.ENTITY_TYPE.get(BuiltInRegistries.ENTITY_TYPE.getDefaultKey())) {
                    MobData data = new MobData();
                    if (json.has("replace_with")) {
                        ResourceLocation replaceId = ResourceLocation.parse(json.get("replace_with").getAsString());
                        data.replaceWith = BuiltInRegistries.ENTITY_TYPE.get(replaceId);
                    }
                    data.chance = json.has("chance") ? json.get("chance").getAsDouble() : 0.0;
                    data.isHighTier = json.has("is_high_tier") && json.get("is_high_tier").getAsBoolean();
                    if (json.has("special_drops")) {
                        JsonArray drops = json.getAsJsonArray("special_drops");
                        for (JsonElement e : drops) {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(e.getAsString()));
                            if (item != BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey()))
                                data.specialDrops.add(item);
                        }
                    }
                    if (json.has("forbidden_drops")) {
                        JsonArray drops = json.getAsJsonArray("forbidden_drops");
                        for (JsonElement e : drops) {
                            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(e.getAsString()));
                            if (item != BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey()))
                                data.forbiddenDrops.add(item);
                        }
                    }
                    MOB_DATA.put(entityType, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static class MobData {
        public EntityType<?> replaceWith;
        public double chance;
        public List<Item> specialDrops = new ArrayList<>();
        public List<Item> forbiddenDrops = new ArrayList<>();
        public boolean isHighTier = false;
    }
}