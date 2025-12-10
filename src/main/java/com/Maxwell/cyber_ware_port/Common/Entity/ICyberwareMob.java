package com.Maxwell.cyber_ware_port.Common.Entity;import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.List;public interface ICyberwareMob {default List<Item> getSpecialDrops() {
        return Collections.emptyList();

    }default List<Item> getForbiddenDrops() {
        return Collections.emptyList();

    }default boolean isHighTierMob() {
        return false;

    }

}