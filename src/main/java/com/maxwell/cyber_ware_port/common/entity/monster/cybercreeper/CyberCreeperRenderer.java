package com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

@SuppressWarnings("removal")
public class CyberCreeperRenderer extends MobRenderer<CyberCreeperEntity, CyberCreeperRenderState, CyberCreeperModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_creeper.png");

    public CyberCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberCreeperModel(context.bakeLayer(CyberCreeperModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new CyberCreeperPowerLayer(this, context.getModelSet()));
    }

    @Override
    public Identifier getTextureLocation(CyberCreeperRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(CyberCreeperRenderState state, PoseStack poseStack) {
        float swell = state.swelling;
        float f1 = 1.0F + Mth.sin(swell * 100.0F) * swell * 0.01F;
        swell = Mth.clamp(swell, 0.0F, 1.0F);
        swell *= swell;
        swell *= swell;
        float f2 = (1.0F + swell * 0.4F) * f1;
        float f3 = (1.0F + swell * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
        if (state.isBaby) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }
    }

    @Override
    protected float getWhiteOverlayProgress(CyberCreeperRenderState state) {
        float swell = state.swelling;
        return (int) (swell * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swell, 0.5F, 1.0F);
    }

    @Override
    public CyberCreeperRenderState createRenderState() {
        return new CyberCreeperRenderState();
    }

    @Override
    public void extractRenderState(CyberCreeperEntity entity, CyberCreeperRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.swelling = entity.getSwelling(partialTicks);
        state.isPowered = entity.isPowered();
    }
}