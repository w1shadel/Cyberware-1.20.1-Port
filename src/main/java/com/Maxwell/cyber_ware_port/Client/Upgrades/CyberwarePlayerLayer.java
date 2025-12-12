package com.Maxwell.cyber_ware_port.Client.Upgrades;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModItems;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CyberwarePlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation CYBER_SKIN_TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/entity/cyber_limbs.png");
    private final CyberLimbModel<AbstractClientPlayer> cyberLimbModel;

    public CyberwarePlayerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
        this.cyberLimbModel = new CyberLimbModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CyberLimbModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (data.isCyberwareInstalled(ModItems.SYNTHETIC_SKIN.get())) {
                return;
            }
            PlayerModel<AbstractClientPlayer> parentModel = this.getParentModel();
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(CYBER_SKIN_TEXTURE));
            if (data.hasCyberRightArm()) {
                this.cyberLimbModel.rightArm.copyFrom(parentModel.rightArm);
                this.cyberLimbModel.rightArm.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
            if (data.hasCyberLeftArm()) {
                this.cyberLimbModel.leftArm.copyFrom(parentModel.leftArm);
                this.cyberLimbModel.leftArm.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
            if (data.hasCyberRightLeg()) {
                this.cyberLimbModel.rightLeg.copyFrom(parentModel.rightLeg);
                this.cyberLimbModel.rightLeg.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
            if (data.hasCyberLeftLeg()) {
                this.cyberLimbModel.leftLeg.copyFrom(parentModel.leftLeg);
                this.cyberLimbModel.leftLeg.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        });
    }
}