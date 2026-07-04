package com.maxwell.cyber_ware_port.api.json;

import com.google.gson.*;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.BodyRegionEnum;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CyberwareDataManager extends SimpleJsonResourceReloadListener {
    public static final Map<Item, ICyberware> DYNAMIC_CYBERWARE = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public String abilityId = "";

    public CyberwareDataManager() {
        super(GSON, "cyberware");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        DYNAMIC_CYBERWARE.clear();
        pObject.forEach((location, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();
                ResourceLocation itemId = new ResourceLocation(json.get("item").getAsString());
                Item item = ForgeRegistries.ITEMS.getValue(itemId);
                if (item != null) {
                    CyberwareData data = new CyberwareData();
                    String slotStr = json.get("slot").getAsString().toUpperCase();
                    data.slotId = BodyRegionEnum.valueOf(slotStr).getStartSlot();
                    data.essence = json.has("essence") ? json.get("essence").getAsInt() : 20;
                    data.maxInstall = json.has("max_install") ? json.get("max_install").getAsInt() : 1;
                    if (json.has("attributes")) {
                        JsonArray attrs = json.getAsJsonArray("attributes");
                        for (JsonElement attrElement : attrs) {
                            JsonObject attrObj = attrElement.getAsJsonObject();
                            Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attrObj.get("attribute").getAsString()));
                            if (attr != null) {
                                double amount = attrObj.get("amount").getAsDouble();
                                AttributeModifier.Operation op = AttributeModifier.Operation.valueOf(attrObj.get("operation").getAsString().toUpperCase());
                                data.attributeModifiers.put(attr, new AttributeModifier(UUID.randomUUID(), "Cyberware Modifier", amount, op));
                            }
                        }
                    }
                    if (json.has("incompatible")) {
                        for (JsonElement e : json.getAsJsonArray("incompatible")) {
                            data.incompatibleItems.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(e.getAsString())));
                        }
                    }
                    if (json.has("stacking")) {
                        String ruleStr = json.get("stacking").getAsString().toUpperCase();
                        data.stackingRule = ICyberware.StackingRule.valueOf(ruleStr);
                    }
                    if (json.has("has_energy")) {
                        data.hasEnergyProperties = json.get("has_energy").getAsBoolean();
                    } else if (json.has("energy_consumption") || json.has("energy_generation") || json.has("energy_storage")) {
                        data.hasEnergyProperties = true;
                    }
                    if (json.has("energy_consumption")) {
                        data.energyConsumption = json.get("energy_consumption").getAsInt();
                    }
                    if (json.has("energy_generation")) {
                        data.energyGeneration = json.get("energy_generation").getAsInt();
                    }
                    if (json.has("energy_storage")) {
                        data.energyStorage = json.get("energy_storage").getAsInt();
                    }
                    DYNAMIC_CYBERWARE.put(item, new DynamicCyberwareWrapper(data));
                }
            } catch (Exception e) {
                System.err.println("Failed to load cyberware data for: " + location);
                e.printStackTrace();
            }
        });
    }
}