package com.Maxwell.cyber_ware_port.Common.Block.CyberSkull;import com.Maxwell.cyber_ware_port.Client.ModClientEvents;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("removal")
public class CyberSkullRenderer implements BlockEntityRenderer<SkullBlockEntity> {

    private final SkullModel model;private static final ResourceLocation TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/entity/cyber_wither_skeleton.png");public CyberSkullRenderer(BlockEntityRendererProvider.Context context) {

        this.model = new SkullModel(context.bakeLayer(ModClientEvents.CYBER_SKULL_LAYER));

    }

    @Override
    public void render(SkullBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        float animationProgress = pBlockEntity.getAnimation(pPartialTick);

        BlockState blockstate = pBlockEntity.getBlockState();boolean isWall = blockstate.getBlock() instanceof WallSkullBlock;

        Direction direction = isWall ? blockstate.getValue(WallSkullBlock.FACING) : null;float rotation = 22.5F * (float)(isWall ? (2 + direction.get2DDataValue()) * 4 : blockstate.getValue(SkullBlock.ROTATION));
SkullBlockRenderer.renderSkull(
                direction,
                rotation,
                animationProgress,
                pPoseStack,
                pBufferSource,
                pPackedLight,
                this.model,
                RenderType.entityTranslucent(TEXTURE) 
        );

    }
}