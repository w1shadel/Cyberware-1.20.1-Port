package com.maxwell.cyber_ware_port.client.upgrades;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
public class CyberwareRenderEventHandler {
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return;
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (data.isCyberwareInstalled(ModItems.SYNTHETIC_SKIN.get())) {
                return;
            }
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
        });
    }
}