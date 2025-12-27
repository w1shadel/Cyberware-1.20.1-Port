package com.maxwell.cyber_ware_port;

import com.maxwell.cyber_ware_port.common.network.A_PacketHandler;
import com.maxwell.cyber_ware_port.config.CyberwareConfig;
import com.maxwell.cyber_ware_port.init.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("removal")
@Mod(CyberWare.MODID)
public class CyberWare {

    public static final String MODID = "cyber_ware_port";

    public CyberWare(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntities.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        A_PacketHandler.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CyberwareConfig.COMMON_CONFIG, "cyberware-common.toml");

    }
}
