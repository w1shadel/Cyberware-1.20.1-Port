package com.Maxwell.cyber_ware_port;

import com.Maxwell.cyber_ware_port.Common.Network.PacketHandler;
import com.Maxwell.cyber_ware_port.Config.CyberwareConfig;
import com.Maxwell.cyber_ware_port.Init.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CyberWare.MODID)
public class CyberWare
{

    public static final String MODID = "cyber_ware_port";
    public CyberWare(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.register(modEventBus);
        PacketHandler.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CyberwareConfig.COMMON_CONFIG, "cyberware-common.toml");
    }
}
