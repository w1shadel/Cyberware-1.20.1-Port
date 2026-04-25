package com.maxwell.cyber_ware_port.client.upgrades.handmenu;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.network.OpenPortableCraftingPacket;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class CrafterOpenEvent {
    private static PortableCraftingButton craftingBtn = null;

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            craftingBtn = null;
            CyberwareUserData data = mc.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            if (data.isCyberwareInstalled(ModItems.FINE_MANIPULATORS.get())) {
                int guiLeft = screen.getGuiLeft();
                int guiTop = screen.getGuiTop();
                int btnX = guiLeft + 130;
                int btnY = guiTop + 60;
                craftingBtn = new PortableCraftingButton(btnX, btnY, (btn) -> {
                    ClientPacketDistributor.sendToServer(new OpenPortableCraftingPacket());
                });
                event.addListener(craftingBtn);
            }
        }
    }

    @SubscribeEvent
    public static void onScreenRenderPre(ScreenEvent.Render.Pre event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            if (craftingBtn != null) {
                int guiLeft = screen.getGuiLeft();
                int guiTop = screen.getGuiTop();
                craftingBtn.setX(guiLeft + 130);
                craftingBtn.setY(guiTop + 60);
            }
        }
    }
}