package com.maxwell.cyber_ware_port.api.json;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class MobDataManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static class MobData {
        public EntityType<?> replaceWith;
        public double chance;
        public List<Item> specialDrops = new ArrayList<>();
        public List<Item> forbiddenDrops = new ArrayList<>();
        public boolean isHighTier = false;
    }

    public static final Map<EntityType<?>, MobData> MOB_DATA = new HashMap<>();

    public MobDataManager() {
        super(GSON, "cyberware/mobs");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager,
            ProfilerFiller pProfiler) {
        MOB_DATA.clear();
        System.out.println("[Cyberware] Loading mob data from JSON...");
        pObject.forEach((location, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();
                if (!json.has("mob")) {
                    System.err.println("[Cyberware] Missing 'mob' field in: " + location);
                    return;
                }

                ResourceLocation mobId = new ResourceLocation(json.get("mob").getAsString());
                EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(mobId);

                if (entityType != null) {
                    MobData data = new MobData();

                    if (json.has("replace_with")) {
                        ResourceLocation replaceId = new ResourceLocation(json.get("replace_with").getAsString());
                        data.replaceWith = ForgeRegistries.ENTITY_TYPES.getValue(replaceId);
                    }

                    data.chance = json.has("chance") ? json.get("chance").getAsDouble() : 0.0;
                    data.isHighTier = json.has("is_high_tier") && json.get("is_high_tier").getAsBoolean();

                    if (json.has("special_drops")) {
                        JsonArray drops = json.getAsJsonArray("special_drops");
                        for (JsonElement e : drops) {
                            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(e.getAsString()));
                            if (item != null)
                                data.specialDrops.add(item);
                        }
                    }

                    if (json.has("forbidden_drops")) {
                        JsonArray drops = json.getAsJsonArray("forbidden_drops");
                        for (JsonElement e : drops) {
                            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(e.getAsString()));
                            if (item != null)
                                data.forbiddenDrops.add(item);
                        }
                    }

                    MOB_DATA.put(entityType, data);
                    ResourceLocation replaceName = data.replaceWith != null
                            ? ForgeRegistries.ENTITY_TYPES.getKey(data.replaceWith)
                            : null;
                    System.out.println("[Cyberware] Loaded mob data for: " + mobId + " (Replace with: "
                            + (replaceName != null ? replaceName : "None") + ")");
                } else {
                    System.err.println("[Cyberware] Could not find EntityType: " + mobId + " in " + location);
                }
            } catch (Exception e) {
                System.err.println("[Cyberware] Failed to load mob data for: " + location);
                e.printStackTrace();
            }
        });
        System.out.println("[Cyberware] Finished loading " + MOB_DATA.size() + " mob data entries.");
    }
}
