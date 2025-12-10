package com.Maxwell.cyber_ware_port.Common.Block.CWB;import com.Maxwell.cyber_ware_port.CyberWare;
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
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("removal")
public class CyberwareWorkbenchRenderer implements BlockEntityRenderer<CyberwareWorkbenchBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/block/cyberware_workbench_model.png");

    private final CyberWareWorkBenchModel model;public CyberwareWorkbenchRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new CyberWareWorkBenchModel(context.bakeLayer(CyberWareWorkBenchModel.LAYER_LOCATION));

    }
    @Override
    public void render(CyberwareWorkbenchBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();

        pPoseStack.translate(0.5, 1.5, 0.5);

        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));

        BlockState blockState = pBlockEntity.getBlockState();

        Direction facing = blockState.getValue(HorizontalDirectionalBlock.FACING);

        float rotationDegrees = facing.getOpposite().toYRot();

         pPoseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees + 180.0f));

        this.model.setupAnim(pBlockEntity, pPartialTick);

        VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        this.model.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();

    }
}