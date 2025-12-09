package com.Maxwell.cyber_ware_port.Client.CreativeTab;



import com.Maxwell.cyber_ware_port.Common.CyberwareTabState;


import com.Maxwell.cyber_ware_port.CyberWare;


import com.Maxwell.cyber_ware_port.Init.ModItems;


import com.mojang.blaze3d.systems.RenderSystem;


import net.minecraft.client.Minecraft;


import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;


import net.minecraft.resources.ResourceLocation;


import net.minecraft.world.item.CreativeModeTab;


import net.minecraftforge.api.distmarker.Dist;


import net.minecraftforge.client.event.ScreenEvent;


import net.minecraftforge.eventbus.api.SubscribeEvent;


import net.minecraftforge.fml.common.Mod;


import net.minecraftforge.fml.util.ObfuscationReflectionHelper;



import java.lang.reflect.Field;


import java.util.ArrayList;


import java.util.List;


@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CyberwareTabEvent {

    private static final ResourceLocation TAB_TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/extended_tabs.png");



    private static final List<CyberwareSideTabButton> customTabs = new ArrayList<>();



    private static boolean isReloading = false;

@SubscribeEvent
    public static void onScreenInitPre(ScreenEvent.Init.Pre event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen) {
            refreshTabContents();


        }
    }@SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen) {

            if (isReloading) {
                return;


            }
            CyberwareTabState.currentPage = 0;


        }
    }@SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen screen) {
            customTabs.clear();



            int guiLeft = screen.getGuiLeft();


            int guiTop = screen.getGuiTop();


            int imageWidth = 28;


            int panelX = guiLeft - imageWidth;


            int panelY = guiTop;


            int buttonX = panelX + 7;



            CyberwareSideTabButton btn1 = new CyberwareSideTabButton(
                    buttonX, panelY + 8, 17, 17,
                    (btn) -> {
                        if (CyberwareTabState.currentPage != 1) {
                            CyberwareTabState.currentPage = 1;


                            reloadScreen();


                        }
                    }
            );


            event.addListener(btn1);


            customTabs.add(btn1);



            CyberwareSideTabButton btn2 = new CyberwareSideTabButton(
                    buttonX, panelY + 31, 17, 17,
                    (btn) -> {
                        if (CyberwareTabState.currentPage != 0) {
                            CyberwareTabState.currentPage = 0;


                            reloadScreen();


                        }
                    }
            );


            event.addListener(btn2);


            customTabs.add(btn2);



            updateVisibility(screen);


        }
    }@SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen screen) {
            if (updateVisibility(screen)) {
                int guiLeft = screen.getGuiLeft();


                int guiTop = screen.getGuiTop();


                int panelX = guiLeft - 28;


                int panelY = guiTop;



                RenderSystem.setShaderTexture(0, TAB_TEXTURE);

                RenderSystem.enableBlend();

                RenderSystem.defaultBlendFunc();


                event.getGuiGraphics().pose().pushPose();

                event.getGuiGraphics().pose().translate(0, 0, 100);


                event.getGuiGraphics().blit(TAB_TEXTURE, panelX, panelY, 0, 0, 28, 128, 256, 256);


                event.getGuiGraphics().pose().popPose();

                RenderSystem.disableBlend();

            }
        }
    }private static void reloadScreen() {

        isReloading = true;


        refreshTabContents();


        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            mc.setScreen(new CreativeModeInventoryScreen(
                    mc.player,
                    mc.player.connection.enabledFeatures(),
                    mc.options.operatorItemsTab().get()
            ));

        }

        isReloading = false;

    }

    private static void refreshTabContents() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null) {
            CreativeModeTab.ItemDisplayParameters params = new CreativeModeTab.ItemDisplayParameters(
                    mc.player.connection.enabledFeatures(),
                    mc.options.operatorItemsTab().get(),
                    mc.player.level().registryAccess()
            );

            ModItems.CW_TABS.get().buildContents(params);

        }
    }

    private static boolean updateVisibility(CreativeModeInventoryScreen screen) {
        CreativeModeTab selectedTab = null;

        try {
            selectedTab = ObfuscationReflectionHelper.getPrivateValue(
                    CreativeModeInventoryScreen.class,
                    null,
                    "f_98505_"
            );

        } catch (Exception e) {
            try {
                Field f = CreativeModeInventoryScreen.class.getDeclaredField("selectedTab");

                f.setAccessible(true);

                selectedTab = (CreativeModeTab) f.get(null);

            } catch (Exception ex) {
                return false;

            }
        }

        boolean isMyTab = (selectedTab == ModItems.CW_TABS.get());


        for (CyberwareSideTabButton btn : customTabs) {
            btn.visible = isMyTab;

            btn.active = isMyTab;

        }

        return isMyTab;

    }
}