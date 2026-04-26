package com.maxwell.cyber_ware_port.common.entity.monster.cyberzombie;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

@SuppressWarnings("removal")
public class CyberZombieRenderer extends MobRenderer<CyberZombieEntity, CyberZombieRenderState, CyberZombieModel<CyberZombieRenderState>> {
    public static final ModelLayerLocation CYBER_ZOMBIE_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CyberWare.MODID, "cyber_zombie"), "main");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_zombie.png");

    public CyberZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberZombieModel<>(context.bakeLayer(CYBER_ZOMBIE_LAYER)), 0.5F);
    }

    @Override
    public Identifier getTextureLocation(CyberZombieRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(CyberZombieRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.7F, 0.7F, 0.7F);
        }
    }

    @Override
    public CyberZombieRenderState createRenderState() {
        return new CyberZombieRenderState();
    }

    @Override
    public void extractRenderState(CyberZombieEntity entity, CyberZombieRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isAggressive = entity.isAggressive();
    }
}
