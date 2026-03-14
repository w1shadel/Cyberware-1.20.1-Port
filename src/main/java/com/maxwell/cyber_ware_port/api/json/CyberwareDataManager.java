package com.maxwell.cyber_ware_port.api.json;

import com.google.gson.*;
import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.BodyRegionEnum;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class CyberwareDataManager extends SimpleJsonResourceReloadListener {
    public static final Map<Item, ICyberware> DYNAMIC_CYBERWARE = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public CyberwareDataManager() {
        super(GSON, "cyberware");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        DYNAMIC_CYBERWARE.clear();
        pObject.forEach((location, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();
                if (!json.has("item")) return;
                ResourceLocation itemId = ResourceLocation.parse(json.get("item").getAsString());
                Item item = BuiltInRegistries.ITEM.get(itemId);
                if (item != null && item != BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey())) {
                    if (!json.has("slot")) return;
                    CyberwareData data = new CyberwareData();
                    String slotStr = json.get("slot").getAsString().toUpperCase();
                    data.slotId = BodyRegionEnum.valueOf(slotStr).getStartSlot();
                    data.essence = json.has("essence") ? json.get("essence").getAsInt() : 20;
                    data.maxInstall = json.has("max_install") ? json.get("max_install").getAsInt() : 1;
                    if (json.has("attributes")) {
                        JsonArray attrs = json.getAsJsonArray("attributes");
                        for (JsonElement attrElement : attrs) {
                            JsonObject attrObj = attrElement.getAsJsonObject();
                            ResourceLocation attrId = ResourceLocation.parse(attrObj.get("attribute").getAsString());
                            Attribute attr = BuiltInRegistries.ATTRIBUTE.get(attrId);
                            if (attr != null) {
                                double amount = attrObj.get("amount").getAsDouble();
                                AttributeModifier.Operation op = AttributeModifier.Operation.valueOf(attrObj.get("operation").getAsString().toUpperCase());
                                ResourceLocation modId = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "dynamic_" + location.getPath().replace("/", "_"));
                                data.attributeModifiers.put(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attr), new AttributeModifier(modId, amount, op));
                            }
                        }
                    }
                    if (json.has("incompatible")) {
                        for (JsonElement e : json.getAsJsonArray("incompatible")) {
                            Item incomp = BuiltInRegistries.ITEM.get(ResourceLocation.parse(e.getAsString()));
                            if (incomp != BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey())) {
                                data.incompatibleItems.add(incomp);
                            }
                        }
                    }
                    if (json.has("stacking")) {
                        String ruleStr = json.get("stacking").getAsString().toUpperCase();
                        data.stackingRule = ICyberware.StackingRule.valueOf(ruleStr);
                    }
                    DYNAMIC_CYBERWARE.put(item, new DynamicCyberwareWrapper(data));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}