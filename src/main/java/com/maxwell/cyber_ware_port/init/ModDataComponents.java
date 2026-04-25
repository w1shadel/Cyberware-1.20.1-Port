package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CyberWare.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GHOST_COMPONENT =
            register("ghost", builder -> builder.networkSynchronized(ByteBufCodecs.BOOL).persistent(com.mojang.serialization.Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PRISTINE =
            register("pristine", builder -> builder.networkSynchronized(ByteBufCodecs.BOOL).persistent(com.mojang.serialization.Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ACTIVE =
            register("active", builder -> builder.networkSynchronized(ByteBufCodecs.BOOL).persistent(com.mojang.serialization.Codec.BOOL));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return COMPONENTS.registerComponentType(name, builder);
    }

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}