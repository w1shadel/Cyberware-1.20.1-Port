package com.maxwell.cyber_ware_port;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.network.A_PacketHandler;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.*;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(CyberWare.MODID)
public class CyberWare {
    public static final String MODID = "cyber_ware_port";

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GHOST_COMPONENT = ModDataComponents.GHOST_COMPONENT;

    public CyberWare(IEventBus modEventBus, ModContainer modContainer) {
        ModDataComponents.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntities.register(modEventBus);
        CyberwareCapabilityProvider.register(modEventBus);

        A_PacketHandler.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CyberwareConfig.COMMON_CONFIG);
    }
}