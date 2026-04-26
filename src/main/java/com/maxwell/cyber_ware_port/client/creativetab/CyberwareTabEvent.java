package com.maxwell.cyber_ware_port.client.creativetab;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.CyberwareTabState;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class CyberwareTabEvent {
    private static final Identifier TAB_TEXTURE =
            Identifier.fromNamespaceAndPath(CyberWare.MODID.toLowerCase(), "textures/gui/extended_tabs.png");
    private static final List<CyberwareSideTabButton> customTabs = new ArrayList<>();
    private static boolean isReloading = false;
    private static Field cachedSelectedTabField = null;

    @SubscribeEvent
    public static void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen screen) {
            customTabs.clear();
            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();
            int buttonX = guiLeft - 21;
            CyberwareSideTabButton btn1 = new CyberwareSideTabButton(
                    buttonX, guiTop + 8, 17, 17,
                    (btn) -> {
                        if (CyberwareTabState.currentPage != 1) {
                            CyberwareTabState.currentPage = 1;
                            reloadScreen();
                        }
                    }
            );
            CyberwareSideTabButton btn2 = new CyberwareSideTabButton(
                    buttonX, guiTop + 31, 17, 17,
                    (btn) -> {
                        if (CyberwareTabState.currentPage != 0) {
                            CyberwareTabState.currentPage = 0;
                            reloadScreen();
                        }
                    }
            );
            event.addListener(btn1);
            event.addListener(btn2);
            customTabs.add(btn1);
            customTabs.add(btn2);
            updateVisibility(screen);
        }
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen screen) {
            if (updateVisibility(screen)) {
                var g = event.getGuiGraphics();
                int guiLeft = screen.getGuiLeft();
                int guiTop = screen.getGuiTop();
                int panelX = guiLeft - 28;
                g.pose().pushMatrix();
                g.nextStratum();
                g.blit(
                        RenderPipelines.GUI_TEXTURED,
                        TAB_TEXTURE,
                        panelX,
                        guiTop,
                        0.0F,
                        0.0F,
                        28,
                        128,
                        256,
                        256
                );
                g.pose().popMatrix();
            }
        }
    }

    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen && !isReloading) {
            CyberwareTabState.currentPage = 0;
        }
    }

    private static void reloadScreen() {
        isReloading = true;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            CreativeModeTab tab = ModItems.CW_TABS.get();
            tab.buildContents(new CreativeModeTab.ItemDisplayParameters(
                    mc.player.connection.enabledFeatures(),
                    mc.options.operatorItemsTab().get(),
                    mc.player.level().registryAccess()
            ));
            mc.setScreen(new CreativeModeInventoryScreen(
                    mc.player,
                    mc.player.connection.enabledFeatures(),
                    mc.options.operatorItemsTab().get()
            ));
        }
        isReloading = false;
    }

    private static CreativeModeTab getSelectedTab(CreativeModeInventoryScreen screen) {
        try {
            if (cachedSelectedTabField == null) {
                for (Field field : CreativeModeInventoryScreen.class.getDeclaredFields()) {
                    if (field.getType() == CreativeModeTab.class && Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        cachedSelectedTabField = field;
                        break;
                    }
                }
            }
            if (cachedSelectedTabField != null) {
                return (CreativeModeTab) cachedSelectedTabField.get(null);
            }
        } catch (Exception e) {
            CyberWare.LOGGER.error("Failed to get selected tab via reflection", e);
        }
        return null;
    }

    private static boolean updateVisibility(CreativeModeInventoryScreen screen) {
        CreativeModeTab selectedTab = getSelectedTab(screen);
        if (selectedTab == null) return false;
        boolean isMyTab = (selectedTab == ModItems.CW_TABS.get());
        for (CyberwareSideTabButton btn : customTabs) {
            btn.visible = isMyTab;
            btn.active = isMyTab;
        }
        return isMyTab;
    }
}