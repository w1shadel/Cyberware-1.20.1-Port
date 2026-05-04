package com.maxwell.cyber_ware_port.client.upgrades;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;

public class CyberwarePlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation CYBER_SKIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_limbs.png");
    private final CyberLimbModel<AbstractClientPlayer> cyberLimbModel;

    public CyberwarePlayerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
        this.cyberLimbModel = new CyberLimbModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CyberLimbModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        if (data.isCyberwareInstalled(ModItems.SYNTHETIC_SKIN.get())) {
            return;
        }
        PlayerModel<AbstractClientPlayer> parentModel = this.getParentModel();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(CYBER_SKIN_TEXTURE));
        if (data.hasCyberRightArm() && player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE)) {
            this.cyberLimbModel.rightArm.copyFrom(parentModel.rightArm);
            this.cyberLimbModel.rightArm.visible = true;
            this.cyberLimbModel.rightArm.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (data.hasCyberLeftArm() && player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE)) {
            this.cyberLimbModel.leftArm.copyFrom(parentModel.leftArm);
            this.cyberLimbModel.leftArm.visible = true;
            this.cyberLimbModel.leftArm.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (data.hasCyberRightLeg() && player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG)) {
            this.cyberLimbModel.rightLeg.copyFrom(parentModel.rightLeg);
            this.cyberLimbModel.rightLeg.visible = true;
            this.cyberLimbModel.rightLeg.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (data.hasCyberLeftLeg() && player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG)) {
            this.cyberLimbModel.leftLeg.copyFrom(parentModel.leftLeg);
            this.cyberLimbModel.leftLeg.visible = true;
            this.cyberLimbModel.leftLeg.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}