package com.maxwell.cyber_ware_port.common.entity.monster.cyberwitherskeleton;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

@SuppressWarnings("removal")
public class CyberWitherSkeletonRenderer extends MobRenderer<CyberWitherSkeletonEntity, CyberWitherSkeletonRenderState, CyberWitherSkeletonModel> {
    private static final Identifier NORMAL_TEXTURE =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_wither_skeleton.png");

    public CyberWitherSkeletonRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new CyberWitherSkeletonModel(pContext.bakeLayer(CyberWitherSkeletonModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(CyberWitherSkeletonRenderState state) {
        return NORMAL_TEXTURE;
    }

    @Override
    protected void scale(CyberWitherSkeletonRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }
    }

    @Override
    public CyberWitherSkeletonRenderState createRenderState() {
        return new CyberWitherSkeletonRenderState();
    }

    @Override
    public void extractRenderState(CyberWitherSkeletonEntity entity, CyberWitherSkeletonRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isAggressive = entity.isAggressive();
        state.isHoldingBow = entity.getMainHandItem().is(Items.BOW);
    }
}