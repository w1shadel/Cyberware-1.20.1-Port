package com.maxwell.cyber_ware_port.common.block.cyberskull;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.client.ModClientEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CyberSkullRenderer implements BlockEntityRenderer<SkullBlockEntity, SkullBlockRenderState> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_wither_skeleton.png");
    private final SkullModel model;

    public CyberSkullRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new SkullModel(context.bakeLayer(ModClientEvents.CYBER_SKULL_LAYER));
    }

    @Override
    public SkullBlockRenderState createRenderState() {
        return new SkullBlockRenderState();
    }

    @Override
    public void extractRenderState(SkullBlockEntity blockEntity, SkullBlockRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        state.animationProgress = blockEntity.getAnimation(partialTicks);
        BlockState blockState = blockEntity.getBlockState();
        if (blockState.getBlock() instanceof WallSkullBlock) {
            Direction facing = blockState.getValue(WallSkullBlock.FACING);
            state.transformation = SkullBlockRenderer.TRANSFORMATIONS.wallTransformation(facing);
        } else {
            state.transformation = SkullBlockRenderer.TRANSFORMATIONS.freeTransformations(blockState.getValue(SkullBlock.ROTATION));
        }
        state.renderType = RenderTypes.entityCutout(TEXTURE);
    }

    @Override
    public void submit(SkullBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(state.transformation);
        SkullBlockRenderer.submitSkull(
                state.animationProgress,
                poseStack,
                submitNodeCollector,
                state.lightCoords,
                this.model,
                state.renderType,
                0,
                state.breakProgress
        );
        poseStack.popPose();
    }
}