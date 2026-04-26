package com.maxwell.cyber_ware_port.api.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobDataManager extends SimpleJsonResourceReloadListener {
    public static final Map<EntityType<?>, MobData> MOB_DATA = new HashMap<>();

    public MobDataManager() {
        super(ExtraCodecs.JSON, FileToIdConverter.json("cyberware/mobs"));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void apply(Object o, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        MOB_DATA.clear();
        Map<Identifier, JsonElement> map = (Map<Identifier, JsonElement>) o;
        map.forEach((location, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();
                if (!json.has("mob")) return;
                Identifier mobId = Identifier.parse(json.get("mob").getAsString());
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(mobId)
                        .map(Holder.Reference::value)
                        .orElse(null);
                if (entityType != null) {
                    MobData data = new MobData();
                    if (json.has("replace_with")) {
                        Identifier replaceId = Identifier.parse(json.get("replace_with").getAsString());
                        data.replaceWith = BuiltInRegistries.ENTITY_TYPE.get(replaceId)
                                .map(Holder.Reference::value)
                                .orElse(null);
                    }
                    data.chance = json.has("chance") ? json.get("chance").getAsDouble() : 0.0;
                    data.isHighTier = json.has("is_high_tier") && json.get("is_high_tier").getAsBoolean();
                    if (json.has("special_drops")) {
                        JsonArray drops = json.getAsJsonArray("special_drops");
                        for (JsonElement e : drops) {
                            BuiltInRegistries.ITEM.get(Identifier.parse(e.getAsString()))
                                    .map(Holder.Reference::value)
                                    .ifPresent(data.specialDrops::add);
                        }
                    }
                    if (json.has("forbidden_drops")) {
                        JsonArray drops = json.getAsJsonArray("forbidden_drops");
                        for (JsonElement e : drops) {
                            BuiltInRegistries.ITEM.get(Identifier.parse(e.getAsString()))
                                    .map(Holder.Reference::value)
                                    .ifPresent(data.forbiddenDrops::add);
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