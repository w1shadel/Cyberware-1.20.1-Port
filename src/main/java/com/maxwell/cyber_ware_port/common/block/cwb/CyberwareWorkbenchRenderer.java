package com.maxwell.cyber_ware_port.common.block.cwb;

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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CyberwareWorkbenchRenderer implements BlockEntityRenderer<CyberwareWorkbenchBlockEntity, CyberwareWorkbenchRenderer.WorkbenchRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/block/cyberware_workbench_model.png");
    private final CyberWareWorkBenchModel model;

    public CyberwareWorkbenchRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new CyberWareWorkBenchModel(context.bakeLayer(CyberWareWorkBenchModel.LAYER_LOCATION));
    }

    @Override
    public WorkbenchRenderState createRenderState() {
        return new WorkbenchRenderState();
    }

    @Override
    public void extractRenderState(CyberwareWorkbenchBlockEntity blockEntity, WorkbenchRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(WorkbenchRenderState state, PoseStack pPoseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 1.5, 0.5);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(state.facing.getOpposite().toYRot() + 180.0f));
        collector.submitCustomGeometry(pPoseStack, RenderTypes.entityCutout(TEXTURE), (pose, vertexConsumer) -> {
            this.model.setupAnim(null, state.partialTicks);
            this.model.renderToBuffer(pPoseStack, vertexConsumer, state.lightCoords, 0, -1);
        });
        pPoseStack.popPose();
    }

    public static class WorkbenchRenderState extends BlockEntityRenderState {
        public Direction facing;
        public float partialTicks;
    }
}