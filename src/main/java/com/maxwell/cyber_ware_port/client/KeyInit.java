package com.maxwell.cyber_ware_port.client;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class KeyInit {
    public static final KeyMapping.Category CYBERWARE_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "main")
    );
    public static final KeyMapping MENU_KEY = new KeyMapping(
            "key.cyber_ware_port.menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CYBERWARE_CATEGORY
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(MENU_KEY);
    }
}