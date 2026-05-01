package com.maxwell.cyber_ware_port.common.entity.misc;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class PlayerTempRenderer extends MobRenderer<PlayerTempModelEntity, PlayerTempModelState, PlayerTempModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/player_internal_part.png");

    public PlayerTempRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerTempModel(context.bakeLayer(PlayerTempModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(PlayerTempModelState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(PlayerTempModelState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }
    }

    @Override
    public PlayerTempModelState createRenderState() {
        return new PlayerTempModelState();
    }

    @Override
    public void extractRenderState(PlayerTempModelEntity entity, PlayerTempModelState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yRot = entity.getYRot();
        state.xRot = entity.getXRot();
    }
}