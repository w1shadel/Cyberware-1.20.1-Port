package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWitherSkeleton;

import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
@SuppressWarnings("removal")
public class CyberWitherSkeletonRenderer extends MobRenderer<CyberWitherSkeletonEntity,CyberWitherSkeletonModel> {
    private static final ResourceLocation NORMAL_TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/entity/cyber_wither_skeleton.png");
    public CyberWitherSkeletonRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new CyberWitherSkeletonModel(pContext.bakeLayer(CyberWitherSkeletonModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(CyberWitherSkeletonEntity cyberWitherSkeletonEntity) {
        return NORMAL_TEXTURE;
    }
}
