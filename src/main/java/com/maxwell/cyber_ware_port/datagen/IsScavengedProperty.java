package com.maxwell.cyber_ware_port.datagen;

import com.maxwell.cyber_ware_port.init.ModDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public record IsScavengedProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<IsScavengedProperty> CODEC = MapCodec.unit(IsScavengedProperty::new);

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return CODEC;
    }

    @Override
    public boolean get(ItemStack itemStack, @org.jspecify.annotations.Nullable ClientLevel clientLevel, @org.jspecify.annotations.Nullable LivingEntity livingEntity, int i, ItemDisplayContext itemDisplayContext) {
        return !itemStack.getOrDefault(ModDataComponents.PRISTINE.get(), true);
    }
}