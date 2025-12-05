package com.Maxwell.cyber_ware_port.Client.CreativeTab;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    private static final ResourceLocation TAB_TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/extended_tabs.png");

    private static final List<CyberwareSideTabButton> customTabs = new ArrayList<>();

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen screen) {
            customTabs.clear();

            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();
            int guiWidth = 195;

            int panelX = guiLeft + guiWidth;
            int panelY = guiTop;

            CyberwareSideTabButton btn1 = new CyberwareSideTabButton(
                    panelX + 4, panelY + 8, 17, 17,
                    new ItemStack(ModItems.CYBER_EYE.get()),
                    (btn) -> {
                        System.out.println("Button 1 Clicked");
                    }
            );
            event.addListener(btn1);
            customTabs.add(btn1);

            CyberwareSideTabButton btn2 = new CyberwareSideTabButton(
                    panelX + 4, panelY + 31, 17, 17,
                    new ItemStack(ModItems.CYBER_LEG_LEFT.get()),
                    (btn) -> {
                        System.out.println("Button 2 Clicked");
                    }
            );
            event.addListener(btn2);
            customTabs.add(btn2);

            updateVisibility(screen);
        }
    }

    @SubscribeEvent
    public static void onScreenRenderPre(ScreenEvent.Render.Pre event) {
        if (event.getScreen() instanceof CreativeModeInventoryScreen screen) {
            if (updateVisibility(screen)) {
                int guiLeft = screen.getGuiLeft();
                int guiTop = screen.getGuiTop();
                int guiWidth = 195;

                int panelX = guiLeft + guiWidth;
                int panelY = guiTop;

                RenderSystem.setShaderTexture(0, TAB_TEXTURE);
                RenderSystem.enableBlend();

                event.getGuiGraphics().blit(TAB_TEXTURE,
                        panelX, panelY,
                        0, 0,
                        28, 128,
                        256, 256
                );

                RenderSystem.disableBlend();
            }
        }
    }
    private static boolean updateVisibility(CreativeModeInventoryScreen screen) {
//        CreativeModeTab selectedTab = null;
//
//        try {
//            // 1. SRG名 (f_98505_) でフィールドを取得しようとする
//            selectedTab = ObfuscationReflectionHelper.getPrivateValue(
//                    CreativeModeInventoryScreen.class,
//                    null, // staticフィールドなのでインスタンスはnull
//                    "f_98505_" // selectedTabのSRG名
//            );
//        } catch (Exception e) {
//            // 2. 開発環境などでSRG名が解決できない場合、直の名前 ("selectedTab") でトライする
//            try {
//                Field f = CreativeModeInventoryScreen.class.getDeclaredField("selectedTab");
//                f.setAccessible(true);
//                selectedTab = (CreativeModeTab) f.get(null);
//            } catch (Exception ex) {
//                // それでもダメなら諦める（ログを出してエラー回避）
//                ex.printStackTrace();
//                return false;
//            }
//        }
        CreativeModeTab selectedTab = CreativeModeInventoryScreen.selectedTab;
        // ここまで来れば selectedTab が取れています
        boolean isMyTab = (selectedTab == ModItems.CW_TABS.get());

        for (CyberwareSideTabButton btn : customTabs) {
            btn.visible = isMyTab;
            btn.active = isMyTab;
        }

        return isMyTab;
    }
}