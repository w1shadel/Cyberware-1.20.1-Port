package com.maxwell.cyber_ware_port.common.entity.monster.cyberskeleton;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class CyberSkeletonRenderer extends MobRenderer<CyberSkeletonEntity, CyberSkeletonRenderState, CyberSkeletonModel<CyberSkeletonRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_skeleton.png");

    public CyberSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberSkeletonModel<>(context.bakeLayer(CyberSkeletonModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(CyberSkeletonRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(CyberSkeletonRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }
    }

    @Override
    public CyberSkeletonRenderState createRenderState() {
        return new CyberSkeletonRenderState();
    }

    @Override
    public void extractRenderState(CyberSkeletonEntity entity, CyberSkeletonRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isAggressive = entity.isAggressive();
        state.isHoldingBow = entity.getMainHandItem().is(Items.BOW);
    }
}