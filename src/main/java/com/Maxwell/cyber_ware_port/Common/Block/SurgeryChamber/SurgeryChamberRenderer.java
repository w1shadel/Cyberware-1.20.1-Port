package com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber;


import com.Maxwell.cyber_ware_port.CyberWare;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;

import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.core.Direction;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

@SuppressWarnings("removal")
public class SurgeryChamberRenderer implements BlockEntityRenderer<SurgeryChamberBlockEntity> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/block/surgery_chamber.png");


    private final SurgeryChamberModel model;


    public SurgeryChamberRenderer(BlockEntityRendererProvider.Context context) {

        this.model = new SurgeryChamberModel(context.bakeLayer(SurgeryChamberModel.LAYER_LOCATION));

    }

    @Override
    public void render(SurgeryChamberBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        if (pBlockEntity.getBlockState().getValue(SurgeryChamberBlock.HALF) == DoubleBlockHalf.UPPER) {
            return;

        }

        pPoseStack.pushPose();

        pPoseStack.translate(0.5D, 1.5D, 0.5D);

        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        Direction facing = pBlockEntity.getBlockState().getValue(SurgeryChamberBlock.FACING);

        float angle = facing.toYRot();

        pPoseStack.mulPose(Axis.YP.rotationDegrees(angle));

        VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        this.model.setupAnim(pBlockEntity, pPartialTick);

        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();

    }
}