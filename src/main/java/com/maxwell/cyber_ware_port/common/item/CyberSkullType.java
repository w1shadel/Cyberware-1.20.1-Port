package com.maxwell.cyber_ware_port.common.item;

import net.minecraft.world.level.block.SkullBlock;

import java.util.Locale;

public enum CyberSkullType implements SkullBlock.Type {
    CYBER_WITHER_SKELETON;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}