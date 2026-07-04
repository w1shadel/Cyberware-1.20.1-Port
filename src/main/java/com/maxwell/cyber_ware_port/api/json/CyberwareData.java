package com.maxwell.cyber_ware_port.api.json;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.Set;

public class CyberwareData {
    public int essence = 20;
    public int slotId = 0;
    public int maxInstall = 1;
    public boolean isPristine = true;
    public ICyberware.StackingRule stackingRule = ICyberware.StackingRule.STATIC;
    public Multimap<Attribute, AttributeModifier> attributeModifiers = ArrayListMultimap.create();
    public Set<Item> incompatibleItems = new HashSet<>();
    public Set<Item> prerequisites = new HashSet<>();
    public boolean hasEnergyProperties = false;
    public int energyConsumption = 0;
    public int energyGeneration = 0;
    public int energyStorage = 0;

    public CyberwareData() {
    }
}