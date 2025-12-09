package com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower;


import com.Maxwell.cyber_ware_port.CyberWare;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;

import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("remeoval")
public class RadioTowerRenderer implements BlockEntityRenderer<RadioTowerCoreBlockEntity> {

    private final RadioTowerModel model;

    // テクスチャのパス
    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/block/radio_tower_complete.png");


    public RadioTowerRenderer(BlockEntityRendererProvider.Context context) {
        // レイヤー定義からモデルを取得 (登録方法は後述)
        this.model = new RadioTowerModel(context.bakeLayer(RadioTowerModel.LAYER_LOCATION));

    }
    @Override
    public void render(RadioTowerCoreBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        // 完成していないなら描画しない
        if (!pBlockEntity.getBlockState().getValue(RadioTowerCoreBlock.FORMED)) {
            return;

        }

        pPoseStack.pushPose();


        // ■ 座標調整 ■
        // Core（ブロック）は中心(0.5, 0.5, 0.5)に描画されています。
        // タワーモデル（柱部分）は、その「下」に描画したいので位置をずらします。

        pPoseStack.translate(0.5D, -7.5D, 0.5D);

        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));


        // もしBlockBenchのモデルに「コア部分」が含まれていない（柱だけ）なら、
        // Y座標を調整して、コアの底面と柱の天面がくっつくように調整してください。
        // 例: Coreの下から描画開始する場合
         pPoseStack.translate(0.0D, 1.0D, 0.0D);


        VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);


        pPoseStack.popPose();

    }
}