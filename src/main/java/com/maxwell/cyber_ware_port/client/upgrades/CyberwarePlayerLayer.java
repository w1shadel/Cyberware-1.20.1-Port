package com.maxwell.cyber_ware_port.client.upgrades;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CyberwarePlayerLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier CYBER_SKIN_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_limbs.png");
    private final CyberLimbModel<AvatarRenderState> cyberLimbModel;

    public CyberwarePlayerLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
        this.cyberLimbModel = new CyberLimbModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CyberLimbModel.LAYER_LOCATION));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                       AvatarRenderState state, float yRot, float xRot) {
        Entity entity = Minecraft.getInstance().level.getEntity(state.id);
        if (!(entity instanceof Player player)) return;
        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        if (data.isCyberwareInstalled(ModItems.SYNTHETIC_SKIN.get())) {
            return;
        }
        this.cyberLimbModel.setupAnim(state);
        var renderType = RenderTypes.entityCutout(CYBER_SKIN_TEXTURE);
        if (data.hasCyberRightArm() && state.showRightSleeve) {
            submitNodeCollector.submitModelPart(
                    this.cyberLimbModel.rightArm,
                    poseStack,
                    renderType,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    null
            );
        }
        if (data.hasCyberLeftArm() && state.showLeftSleeve) {
            submitNodeCollector.submitModelPart(
                    this.cyberLimbModel.leftArm,
                    poseStack,
                    renderType,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    null
            );
        }
        if (data.hasCyberRightLeg() && state.showRightPants) {
            submitNodeCollector.submitModelPart(
                    this.cyberLimbModel.rightLeg,
                    poseStack,
                    renderType,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    null
            );
        }
        if (data.hasCyberLeftLeg() && state.showLeftPants) {
            submitNodeCollector.submitModelPart(
                    this.cyberLimbModel.leftLeg,
                    poseStack,
                    renderType,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    null
            );
        }
    }
}