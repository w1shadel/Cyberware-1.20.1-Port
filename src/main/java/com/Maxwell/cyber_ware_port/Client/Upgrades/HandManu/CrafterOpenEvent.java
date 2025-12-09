package com.Maxwell.cyber_ware_port.Client.Upgrades.HandManu;



import com.Maxwell.cyber_ware_port.Common.Network.A_PacketHandler;


import com.Maxwell.cyber_ware_port.Common.Network.OpenPortableCraftingPacket;


import com.Maxwell.cyber_ware_port.CyberWare;


import com.Maxwell.cyber_ware_port.Init.ModItems;


import net.minecraft.client.Minecraft;


import net.minecraftforge.api.distmarker.Dist;


import net.minecraftforge.client.event.ScreenEvent;


import net.minecraftforge.eventbus.api.SubscribeEvent;


import net.minecraftforge.fml.common.Mod;



@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CrafterOpenEvent {
    private static PortableCraftingButton craftingBtn = null;



    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {if (event.getScreen() instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen screen) {
            Minecraft mc = Minecraft.getInstance();


            if (mc.player == null) return;



            craftingBtn = null;



            mc.player.getCapability(com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                if (data.isCyberwareInstalled(ModItems.FINE_MANIPULATORS.get())) {

                    int guiLeft = screen.getGuiLeft();


                    int guiTop = screen.getGuiTop();

int btnX = guiLeft + 130;


                    int btnY = guiTop + 60;



                    craftingBtn = new PortableCraftingButton(btnX, btnY, (btn) -> {
                        A_PacketHandler.INSTANCE.sendToServer(
                                new OpenPortableCraftingPacket());


                    });



                    event.addListener(craftingBtn);


                }
            });


        }
    }

    @SubscribeEvent
    public static void onScreenRenderPre(ScreenEvent.Render.Pre event) {if (event.getScreen() instanceof net.minecraft.client.gui.screens.inventory.InventoryScreen screen) {
            if (craftingBtn != null) {
                int guiLeft = screen.getGuiLeft();


                int guiTop = screen.getGuiTop();



                craftingBtn.setX(guiLeft + 130);


                craftingBtn.setY(guiTop + 60);


            }
        }
    }
}
