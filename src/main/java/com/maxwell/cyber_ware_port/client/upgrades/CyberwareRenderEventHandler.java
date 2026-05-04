package com.maxwell.cyber_ware_port.client.upgrades;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import com.maxwell.cyber_ware_port.CyberWare;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class CyberwareRenderEventHandler {
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

        if (data.hasCyberRightArm()) {
            model.rightArm.visible = false;
            model.rightSleeve.visible = false;
        }
        if (data.hasCyberLeftArm()) {
            model.leftArm.visible = false;
            model.leftSleeve.visible = false;
        }
        if (data.hasCyberRightLeg()) {
            model.rightLeg.visible = false;
            model.rightPants.visible = false;
        }
        if (data.hasCyberLeftLeg()) {
            model.leftLeg.visible = false;
            model.leftPants.visible = false;
        }
    }
}