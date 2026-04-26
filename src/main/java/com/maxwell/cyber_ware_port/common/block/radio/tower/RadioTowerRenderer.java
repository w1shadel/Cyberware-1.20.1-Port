package com.maxwell.cyber_ware_port.common.block.radio.tower;

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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class RadioTowerRenderer implements BlockEntityRenderer<RadioTowerCoreBlockEntity, RadioTowerRenderer.RadioTowerRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/block/radio_tower_complete.png");
    private final RadioTowerModel model;

    public RadioTowerRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new RadioTowerModel(context.bakeLayer(RadioTowerModel.LAYER_LOCATION));
    }

    @Override
    public RadioTowerRenderState createRenderState() {
        return new RadioTowerRenderState();
    }

    @Override
    public void extractRenderState(RadioTowerCoreBlockEntity blockEntity, RadioTowerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.isFormed = blockEntity.getBlockState().getValue(RadioTowerCoreBlock.FORMED);
    }

    @Override
    public void submit(RadioTowerRenderState state, PoseStack pPoseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!state.isFormed) {
            return;
        }
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, -7.5D, 0.5D);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        pPoseStack.translate(0.0D, 1.0D, 0.0D);
        collector.submitCustomGeometry(pPoseStack, RenderTypes.entityCutout(TEXTURE), (pose, vertexConsumer) -> {
            this.model.renderToBuffer(pPoseStack, vertexConsumer, state.lightCoords, 0, -1);
        });
        pPoseStack.popPose();
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(RadioTowerCoreBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(
                pos.getX() - 5.0, pos.getY() - 15.0, pos.getZ() - 5.0,
                pos.getX() + 6.0, pos.getY() + 2.0, pos.getZ() + 6.0
        );
    }

    public static class RadioTowerRenderState extends BlockEntityRenderState {
        public boolean isFormed;
    }
}