package com.maxwell.cyber_ware_port.common.block.surgerychamber;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

@SuppressWarnings("removal")
public class SurgeryChamberRenderer implements BlockEntityRenderer<SurgeryChamberBlockEntity, SurgeryChamberRenderer.SurgeryChamberRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/block/surgery_chamber.png");
    private final SurgeryChamberModel model;

    public SurgeryChamberRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SurgeryChamberModel(context.bakeLayer(SurgeryChamberModel.LAYER_LOCATION));
    }

    @Override
    public SurgeryChamberRenderState createRenderState() {
        return new SurgeryChamberRenderState();
    }

    @Override
    public void extractRenderState(SurgeryChamberBlockEntity blockEntity, SurgeryChamberRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.half = blockEntity.getBlockState().getValue(SurgeryChamberBlock.HALF);
        state.facing = blockEntity.getBlockState().getValue(SurgeryChamberBlock.FACING);
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(SurgeryChamberRenderState state, PoseStack pPoseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.half == DoubleBlockHalf.UPPER) {
            return;
        }
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 1.5D, 0.5D);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(state.facing.toYRot()));
        collector.submitCustomGeometry(pPoseStack, RenderTypes.entityCutout(TEXTURE), (pose, vertexConsumer) -> {
            this.model.setupAnim(null, state.partialTicks);
            this.model.renderToBuffer(pPoseStack, vertexConsumer, state.lightCoords, 0, -1);
        });
        pPoseStack.popPose();
    }

    public static class SurgeryChamberRenderState extends BlockEntityRenderState {
        public DoubleBlockHalf half;
        public Direction facing;
        public float partialTicks;
    }
}