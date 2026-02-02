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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Map<Item, ICyberware> DYNAMIC_CYBERWARE = new HashMap<>();
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
                    // スロットの読み込み (EYES, HEART などの文字列をIDに変換)
                    String slotStr = json.get("slot").getAsString().toUpperCase();
                    data.slotId = BodyRegionEnum.valueOf(slotStr).getStartSlot();
                    data.essence = json.has("essence") ? json.get("essence").getAsInt() : 20;
                    data.maxInstall = json.has("max_install") ? json.get("max_install").getAsInt() : 1;
                    // 属性(Attributes)の解析
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
                    // 競合アイテム
                    if (json.has("incompatible")) {
                        for (JsonElement e : json.getAsJsonArray("incompatible")) {
                            data.incompatibleItems.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(e.getAsString())));
                        }
                    }
                    if (json.has("stacking")) {
                        String ruleStr = json.get("stacking").getAsString().toUpperCase();
                        data.stackingRule = ICyberware.StackingRule.valueOf(ruleStr);
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