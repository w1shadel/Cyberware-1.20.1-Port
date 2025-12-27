package com.maxwell.cyber_ware_port.common.entity.monster.cyberzombie;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class CyberZombieRenderer extends MobRenderer<CyberZombieEntity, CyberZombieModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/entity/cyber_zombie.png");

    public CyberZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberZombieModel(context.bakeLayer(CyberZombieModel.LAYER_LOCATION)), 0.5F);

    }

    @Override
    public ResourceLocation getTextureLocation(CyberZombieEntity entity) {
        return TEXTURE;

    }

    @Override
    protected void scale(CyberZombieEntity entity, PoseStack poseStack, float partialTickTime) {
        if (entity.isBaby()) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }
        super.scale(entity, poseStack, partialTickTime);
    }
}
