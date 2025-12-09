package com.Maxwell.cyber_ware_port.Client;



import com.Maxwell.cyber_ware_port.CyberWare;


import com.mojang.blaze3d.platform.InputConstants;


import net.minecraft.client.KeyMapping;


import net.minecraftforge.api.distmarker.Dist;


import net.minecraftforge.client.event.RegisterKeyMappingsEvent;


import net.minecraftforge.eventbus.api.SubscribeEvent;


import net.minecraftforge.fml.common.Mod;


import org.lwjgl.glfw.GLFW;



@Mod.EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyInit {
    public static final KeyMapping MENU_KEY = new KeyMapping(
            "key.cyber_ware_port.menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.cyber_ware_port"
    );



    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(MENU_KEY);


    }
}