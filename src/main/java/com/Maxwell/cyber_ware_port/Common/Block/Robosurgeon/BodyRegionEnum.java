package com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon;
public enum BodyRegionEnum {EYES,       
    BRAIN,      
    HEART,      
    LUNGS,      
    STOMACH,    

    SKIN,       
    MUSCLE,     
    BONES,      

    ARMS,       
    HANDS,      

    LEGS,       
    BOOTS;
      

    public static final int SLOTS_PER_PART = 9;


    public int getStartSlot() {
        return this.ordinal() * SLOTS_PER_PART;

    }

    public static int getTotalSlots() {
        return values().length * SLOTS_PER_PART;
 
    }
}