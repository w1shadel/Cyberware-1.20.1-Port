package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberCreeper;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@SuppressWarnings("removal")
public class CyberCreeperRenderer extends MobRenderer<CyberCreeperEntity, CyberCreeperModel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/entity/cyber_creeper.png");

    public CyberCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberCreeperModel(context.bakeLayer(CyberCreeperModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(CyberCreeperEntity entity) {
        return TEXTURE;

    }

    @Override
    protected void scale(CyberCreeperEntity entity, PoseStack poseStack, float partialTick) {
        float swell = entity.getSwelling(partialTick);
        float f1 = 1.0F + Mth.sin(swell * 100.0F) * swell * 0.01F;
        swell = Mth.clamp(swell, 0.0F, 1.0F);
        swell *= swell;
        swell *= swell;
        float f2 = (1.0F + swell * 0.4F) * f1;
        float f3 = (1.0F + swell * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);

    }

    @Override
    protected float getWhiteOverlayProgress(CyberCreeperEntity entity, float partialTick) {
        float swell = entity.getSwelling(partialTick);
        return (int) (swell * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swell, 0.5F, 1.0F);

    }
}