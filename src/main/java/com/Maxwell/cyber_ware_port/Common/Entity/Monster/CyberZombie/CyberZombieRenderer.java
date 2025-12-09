package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberZombie;


import com.Maxwell.cyber_ware_port.CyberWare;

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
}
