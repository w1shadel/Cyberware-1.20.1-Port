package com.maxwell.cyber_ware_port.api.json;

import com.google.gson.*;
import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.BodyRegionEnum;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
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
        super(ExtraCodecs.JSON, FileToIdConverter.json("cyberware"));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void apply(Object o, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DYNAMIC_CYBERWARE.clear();
        Map<Identifier, JsonElement> prepared = (Map<Identifier, JsonElement>) o;
        prepared.forEach((location, element) -> {
            try {
                JsonObject json = element.getAsJsonObject();
                if (!json.has("item")) {
                    return;
                }
                Identifier itemId = Identifier.parse(json.get("item").getAsString());
                Item item = BuiltInRegistries.ITEM.get(itemId)
                        .map(Holder.Reference::value)
                        .orElse(null);
                if (item != null) {
                    if (!json.has("slot")) {
                        return;
                    }
                    CyberwareData data = new CyberwareData();
                    String slotStr = json.get("slot").getAsString().toUpperCase();
                    data.slotId = BodyRegionEnum.valueOf(slotStr).getStartSlot();
                    data.essence = json.has("essence") ? json.get("essence").getAsInt() : 20;
                    data.maxInstall = json.has("max_install") ? json.get("max_install").getAsInt() : 1;
                    if (json.has("attributes")) {
                        JsonArray attrs = json.getAsJsonArray("attributes");
                        for (JsonElement attrElement : attrs) {
                            JsonObject attrObj = attrElement.getAsJsonObject();
                            Identifier attrId = Identifier.parse(attrObj.get("attribute").getAsString());
                            Attribute attr = BuiltInRegistries.ATTRIBUTE.get(attrId)
                                    .map(Holder.Reference::value)
                                    .orElse(null);
                            if (attr != null) {
                                double amount = attrObj.get("amount").getAsDouble();
                                AttributeModifier.Operation op = AttributeModifier.Operation.valueOf(attrObj.get("operation").getAsString().toUpperCase());
                                Identifier modId = Identifier.fromNamespaceAndPath(CyberWare.MODID, "dynamic_" + location.getPath().replace("/", "_"));
                                data.attributeModifiers.put(
                                        BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attr),
                                        new AttributeModifier(modId, amount, op)
                                );
                            }
                        }
                    }
                    if (json.has("incompatible")) {
                        for (JsonElement e : json.getAsJsonArray("incompatible")) {
                            BuiltInRegistries.ITEM.get(Identifier.parse(e.getAsString()))
                                    .map(Holder.Reference::value)
                                    .ifPresent(data.incompatibleItems::add);
                        }
                    }
                    if (json.has("stacking")) {
                        String ruleStr = json.get("stacking").getAsString().toUpperCase();
                        data.stackingRule = ICyberware.StackingRule.valueOf(ruleStr);
                    }

                    // 追加：エネルギーシステム（電力）関連パラメーターのパース
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
                e.printStackTrace();
            }
        });
    }
}