package com.maxwell.cyber_ware_port.common.entity;

import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.List;

public interface ICyberwareMob {
    default List<Item> getSpecialDrops() {
        return Collections.emptyList();

    }

    default List<Item> getForbiddenDrops() {
        return Collections.emptyList();

    }

    default boolean isHighTierMob() {
        return false;

    }

}