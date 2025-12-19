package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberCreeper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;

public class CyberCreeperPowerLayer extends EnergySwirlLayer<CyberCreeperEntity, CyberCreeperModel> {

    private static final ResourceLocation POWER_LOCATION =
            new ResourceLocation("textures/entity/creeper/creeper_armor.png");

    private final CyberCreeperModel model;

    public CyberCreeperPowerLayer(RenderLayerParent<CyberCreeperEntity, CyberCreeperModel> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        this.model = new CyberCreeperModel(pModelSet.bakeLayer(CyberCreeperModel.ARMOR_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight, CyberCreeperEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isPowered()) {
            super.render(poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Override
    protected float xOffset(float pTickCount) {
        return pTickCount * 0.01F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return POWER_LOCATION;
    }

    @Override
    protected EntityModel<CyberCreeperEntity> model() {
        return this.model;
    }
}