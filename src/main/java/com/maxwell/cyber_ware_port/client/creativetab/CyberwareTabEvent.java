package com.maxwell.cyber_ware_port.client.creativetab;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.CyberwareTabState;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CyberwareTabEvent {
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/gui/extended_tabs.png");
    private static final List<CyberwareSideTabButton> customTabs = new ArrayList<>();
    private static boolean isReloading = false;

    // キャッシュ用フィールド
    private static Field selectedTabField = null;
    private static Field globalParamsField = null;
    private static Field instanceParamsField = null;

    @SubscribeEvent
    public static void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen screen) {
            customTabs.clear();
            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();
            int buttonX = guiLeft - 21;

            CyberwareSideTabButton btn1 = new CyberwareSideTabButton(buttonX, guiTop + 8, 17, 17, (btn) -> {
                if (CyberwareTabState.currentPage != 1) {
                    CyberwareTabState.currentPage = 1;
                    reloadScreen();
                }
            });

            CyberwareSideTabButton btn2 = new CyberwareSideTabButton(buttonX, guiTop + 31, 17, 17, (btn) -> {
                if (CyberwareTabState.currentPage != 0) {
                    CyberwareTabState.currentPage = 0;
                    reloadScreen();
                }
            });

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
                int guiLeft = screen.getGuiLeft();
                int guiTop = screen.getGuiTop();

                RenderSystem.setShaderTexture(0, TAB_TEXTURE);
                RenderSystem.enableBlend();
                event.getGuiGraphics().pose().pushPose();
                event.getGuiGraphics().pose().translate(0, 0, 100);
                event.getGuiGraphics().blit(TAB_TEXTURE, guiLeft - 28, guiTop, 0, 0, 28, 128, 256, 256);
                event.getGuiGraphics().pose().popPose();
            }
        }
    }

    private static void reloadScreen() {
        isReloading = true;

        // 1. まずパラメータをリフレクションで破壊
        refreshTabContents();

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // 2. 画面を再構築して開く
            mc.setScreen(new CreativeModeInventoryScreen(
                    mc.player,
                    mc.player.connection.enabledFeatures(),
                    mc.options.operatorItemsTab().get()
            ));
        }
        isReloading = false;
    }

    private static void refreshTabContents() {
        try {
            // 【1】グローバルキャッシュ (CreativeModeTabs.CACHED_PARAMETERS) をクリア
            if (globalParamsField == null) {
                for (Field f : CreativeModeTabs.class.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers()) && f.getType() == CreativeModeTab.ItemDisplayParameters.class) {
                        f.setAccessible(true);
                        globalParamsField = f;
                        break;
                    }
                }
            }
            if (globalParamsField != null) globalParamsField.set(null, null);

            // 【2】インスタンスキャッシュ (CreativeModeTab.cachedTabParameters) をクリア
            CreativeModeTab tab = ModItems.CW_TABS.get();
            if (instanceParamsField == null) {
                for (Field f : CreativeModeTab.class.getDeclaredFields()) {
                    // staticではなく、型が ItemDisplayParameters のものを探す
                    if (!Modifier.isStatic(f.getModifiers()) && f.getType() == CreativeModeTab.ItemDisplayParameters.class) {
                        f.setAccessible(true);
                        instanceParamsField = f;
                        break;
                    }
                }
            }
            if (instanceParamsField != null) instanceParamsField.set(tab, null);

            // 【3】強制的に再構築メソッドを呼ぶ
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                tab.buildContents(new CreativeModeTab.ItemDisplayParameters(
                        mc.player.connection.enabledFeatures(),
                        mc.options.operatorItemsTab().get(),
                        mc.player.level().registryAccess()
                ));
            }
        } catch (Exception e) {
            CyberWare.LOGGER.error("致命的エラー: クリエイティブタブのキャッシュ破壊に失敗しました", e);
        }
    }

    private static boolean updateVisibility(CreativeModeInventoryScreen screen) {
        CreativeModeTab selectedTab = null;
        try {
            if (selectedTabField == null) {
                for (Field field : CreativeModeInventoryScreen.class.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) && field.getType() == CreativeModeTab.class) {
                        field.setAccessible(true);
                        selectedTabField = field;
                        break;
                    }
                }
            }
            if (selectedTabField != null) {
                selectedTab = (CreativeModeTab) selectedTabField.get(null);
            }
        } catch (Exception e) {
            return false;
        }

        boolean isMyTab = (selectedTab == ModItems.CW_TABS.get());
        for (CyberwareSideTabButton btn : customTabs) {
            btn.visible = isMyTab;
            btn.active = isMyTab;
        }
        return isMyTab;
    }
}