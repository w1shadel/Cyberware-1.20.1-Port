package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWither;import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@SuppressWarnings("removal")
public class CyberWitherArmorLayer extends EnergySwirlLayer<CyberWitherBoss, CyberWitherModel> {

    private static final ResourceLocation WITHER_ARMOR_LOCATION =
            new ResourceLocation("textures/entity/wither/wither_armor.png");private final CyberWitherModel model;public CyberWitherArmorLayer(RenderLayerParent<CyberWitherBoss, CyberWitherModel> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);this.model = new CyberWitherModel(pModelSet.bakeLayer(ModelLayers.WITHER_ARMOR));

    }

    @Override
    protected float xOffset(float pTickCount) {

        return Mth.cos(pTickCount * 0.02F) * 3.0F;

    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return WITHER_ARMOR_LOCATION;

    }

    @Override
    protected EntityModel<CyberWitherBoss> model() {
        return this.model;

    }
}