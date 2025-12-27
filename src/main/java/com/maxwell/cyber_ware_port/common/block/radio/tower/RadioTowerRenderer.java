package com.maxwell.cyber_ware_port.common.block.radio.tower;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RadioTowerRenderer implements BlockEntityRenderer<RadioTowerCoreBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/block/radio_tower_complete.png");
    private final RadioTowerModel model;

    public RadioTowerRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new RadioTowerModel(context.bakeLayer(RadioTowerModel.LAYER_LOCATION));

    }

    @Override
    public void render(RadioTowerCoreBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (!pBlockEntity.getBlockState().getValue(RadioTowerCoreBlock.FORMED)) {
            return;

        }
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, -7.5D, 0.5D);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        pPoseStack.translate(0.0D, 1.0D, 0.0D);
        VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();

    }
}