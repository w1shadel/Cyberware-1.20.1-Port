package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberSkeleton;

import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
@SuppressWarnings("removal")
public class CyberSkeletonRenderer extends MobRenderer<CyberSkeletonEntity, CyberSkeletonModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/entity/cyber_skeleton.png");

    public CyberSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberSkeletonModel(context.bakeLayer(CyberSkeletonModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(CyberSkeletonEntity entity) {
        return TEXTURE;
    }
}