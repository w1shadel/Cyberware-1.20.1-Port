package com.Maxwell.cyber_ware_port.Common.Entity;


import net.minecraft.world.item.Item;


import java.util.Collections;

import java.util.List;


public interface ICyberwareMob {

    /**
     * @return このモブがドロップする可能性のある「追加アイテム」のリスト。
     *         （基本プールに含まれていないレアアイテムや、特定の部位などをここで指定します）
     */
    default List<Item> getSpecialDrops() {
        return Collections.emptyList();

    }

    /**
     * @return このモブが「絶対にドロップしない」アイテムのリスト。
     *         （基本プールに含まれていても、ここにあるアイテムは除外されます）
     */
    default List<Item> getForbiddenDrops() {
        return Collections.emptyList();

    }

    /**
     * @return trueの場合、汎用的なレアアイテムプール（High Tier）全体をドロップ候補に含めます。
     *         個別に指定したい場合はfalseにして getSpecialDrops で指定してください。
     */
    default boolean isHighTierMob() {
        return false;

    }

}