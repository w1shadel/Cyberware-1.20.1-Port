package com.Maxwell.cyber_ware_port.Common.Block.Scanner;import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;@SuppressWarnings("removal")
public class ScannerBlockRenderer implements BlockEntityRenderer<ScannerBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/block/scanner.png");

    private final ScannerBlockModel model;public ScannerBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ScannerBlockModel(context.bakeLayer(ScannerBlockModel.LAYER_LOCATION));

    }
    @Override
    public void render(ScannerBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();

        pPoseStack.translate(0.5D, 1.5D, 0.5D);

        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        BlockState blockState = pBlockEntity.getBlockState();

        Direction facing = blockState.getValue(HorizontalDirectionalBlock.FACING);

        float rotationDegrees = facing.getOpposite().toYRot();

        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees));float animTime = 0;

        if (pBlockEntity.isWorking()) {

            animTime = pBlockEntity.getProgress() + pPartialTick;

        }

        this.model.setupMovingParts(pBlockEntity.isWorking(), animTime);VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);pPoseStack.popPose();

    }
}