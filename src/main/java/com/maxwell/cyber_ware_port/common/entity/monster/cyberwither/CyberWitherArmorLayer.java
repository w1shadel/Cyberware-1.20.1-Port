package com.maxwell.cyber_ware_port.common.entity.monster.cyberwither;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.Identifier;

@SuppressWarnings("removal")
public class CyberWitherArmorLayer extends EnergySwirlLayer<CyberWitherRenderState, CyberWitherModel> {
    private static final Identifier POWER_LOCATION = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/wither/wither_armor.png");
    private final CyberWitherModel model;

    public CyberWitherArmorLayer(RenderLayerParent<CyberWitherRenderState, CyberWitherModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new CyberWitherModel(modelSet.bakeLayer(ModelLayers.CREEPER_ARMOR));
    }

    protected boolean isPowered(CyberWitherRenderState state) {
        return state.isPowered;
    }

    protected float xOffset(float t) {
        return t * 0.01F;
    }

    protected Identifier getTextureLocation() {
        return POWER_LOCATION;
    }

    protected CyberWitherModel model() {
        return this.model;
    }
}
