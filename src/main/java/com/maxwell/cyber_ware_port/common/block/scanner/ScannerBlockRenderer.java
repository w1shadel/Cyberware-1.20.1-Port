package com.maxwell.cyber_ware_port.common.block.scanner;

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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ScannerBlockRenderer implements BlockEntityRenderer<ScannerBlockEntity, ScannerBlockRenderer.ScannerRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/block/scanner.png");
    private final ScannerBlockModel model;

    public ScannerBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ScannerBlockModel(context.bakeLayer(ScannerBlockModel.LAYER_LOCATION));
    }

    @Override
    public ScannerRenderState createRenderState() {
        return new ScannerRenderState();
    }

    @Override
    public void extractRenderState(ScannerBlockEntity blockEntity, ScannerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        state.isWorking = blockEntity.isWorking();
        state.animTime = state.isWorking ? (blockEntity.getLevel().getGameTime() + partialTicks) : 0;
    }

    @Override
    public void submit(ScannerRenderState state, PoseStack pPoseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 1.5D, 0.5D);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(state.facing.getOpposite().toYRot()));
        this.model.setupMovingParts(state.isWorking, state.animTime);
        collector.submitModelPart(
                this.model.root(),
                pPoseStack,
                RenderTypes.entityCutout(TEXTURE),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                null
        );
        pPoseStack.popPose();
    }

    public static class ScannerRenderState extends BlockEntityRenderState {
        public Direction facing;
        public boolean isWorking;
        public float animTime;
    }
}