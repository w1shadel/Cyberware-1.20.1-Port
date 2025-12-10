package com.Maxwell.cyber_ware_port.Common.Item.Base;import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity.*;public enum CyberwareSlotType {

    EYES("cyberware_slot.cyber_ware_port.eyes"),
    BRAIN("cyberware_slot.cyber_ware_port.brain"),
    HEART("cyberware_slot.cyber_ware_port.heart"),
    LUNGS("cyberware_slot.cyber_ware_port.lungs"),
    STOMACH("cyberware_slot.cyber_ware_port.stomach"),
    SKIN("cyberware_slot.cyber_ware_port.skin"),
    MUSCLE("cyberware_slot.cyber_ware_port.muscle"),
    BONES("cyberware_slot.cyber_ware_port.bones"),

    ARMS("cyberware_slot.cyber_ware_port.arms"),
    HANDS("cyberware_slot.cyber_ware_port.hands"),

    LEGS("cyberware_slot.cyber_ware_port.legs"),
    BOOTS("cyberware_slot.cyber_ware_port.boots"),

    UNKNOWN("cyberware_slot.cyber_ware_port.unknown");private final String translationKey;CyberwareSlotType(String translationKey) {
        this.translationKey = translationKey;}

    public MutableComponent getDisplayName() {
        return Component.translatable(this.translationKey);}
    public static CyberwareSlotType fromId(int id) {
        if (id < 0) return UNKNOWN;if (isInRange(id, SLOT_EYES)) return EYES;if (isInRange(id, SLOT_BRAIN)) return BRAIN;if (isInRange(id, SLOT_HEART)) return HEART;

        if (isInRange(id, SLOT_LUNGS)) return LUNGS;

        if (isInRange(id, SLOT_STOMACH)) return STOMACH;if (isInRange(id, SLOT_SKIN)) return SKIN;

        if (isInRange(id, SLOT_MUSCLE)) return MUSCLE;

        if (isInRange(id, SLOT_BONES)) return BONES;if (isInRange(id, SLOT_ARMS)) return ARMS;

        if (isInRange(id, SLOT_HANDS)) return HANDS;if (isInRange(id, SLOT_LEGS)) return LEGS;

        if (isInRange(id, SLOT_BOOTS)) return BOOTS;return UNKNOWN;

    }

    private static boolean isInRange(int id, int startId) {
        return id >= startId && id < startId + RobosurgeonBlockEntity.SLOTS_PER_PART;

    }
}