package com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.Identifier;

public class CyberCreeperPowerLayer extends EnergySwirlLayer<CyberCreeperRenderState, CyberCreeperModel> {
    private static final Identifier POWER_LOCATION = Identifier.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    private final CyberCreeperModel model;

    public CyberCreeperPowerLayer(RenderLayerParent<CyberCreeperRenderState, CyberCreeperModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new CyberCreeperModel(modelSet.bakeLayer(CyberCreeperModel.ARMOR_LOCATION));
    }

    protected boolean isPowered(CyberCreeperRenderState state) {
        return state.isPowered;
    }

    protected float xOffset(float t) {
        return t * 0.01F;
    }

    protected Identifier getTextureLocation() {
        return POWER_LOCATION;
    }

    protected CyberCreeperModel model() {
        return this.model;
    }
}
