package com.maxwell.cyber_ware_port.client.upgrades.cybereye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class EyeWareEvents {

    // 電力を消費しないパッシブ機能が正しく動作するよう、電力判定を適正化
    private static boolean isFeatureActive(Player player, Item item) {
        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        if (data == null) return false;
        ItemStacksResourceHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.size(); i++) {
            ItemStack stack = handler.getResource(i).toStack(handler.getAmountAsInt(i));
            if (stack.is(item)) {
                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (cw != null) {
                    if (!cw.isActive(stack)) return false;

                    // 電力を消費するアイテムの場合のみ、電力の有無をチェック
                    if (cw.hasEnergyProperties(stack) && cw.getEnergyConsumption(stack) > 0) {
                        return data.isPowered();
                    }
                    return true; // 電力不要なアップグレードは常に動作可能
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getCamera().getFluidInCamera() == FogType.WATER) {
            Player player = Minecraft.getInstance().player;
            if (player != null && isFeatureActive(player, ModItems.LIQUID_REFRACTION.get())) {
                event.setNearPlaneDistance(-8.0F);
                event.setFarPlaneDistance(200.0F);
            }
        }
    }

    // イベントを ViewportEvent.ComputeFov に変更し、カメラの視野角を強制上書き
    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && isFeatureActive(player, ModItems.DISTANCE_ENHANCER.get())) {
            // スニーク（しゃがみ：Shiftキー）している間だけ、視野角を狭めてズーム
            if (player.isCrouching()) {
                // デフォルトの視野角（FOV）を 0.3f 倍（約3.3倍望遠）に設定します
                float originalFov = event.getFOV(); // float型 且つ 大文字の getFOV()
                event.setFOV(originalFov * 0.3f);   // float型 且つ 大文字の setFOV()
            }
        }
    }
}