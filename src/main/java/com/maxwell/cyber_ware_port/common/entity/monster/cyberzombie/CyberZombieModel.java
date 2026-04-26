package com.maxwell.cyber_ware_port.common.entity.monster.cyberzombie;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.monster.zombie.AbstractZombieModel;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;

@SuppressWarnings("removal")
public class CyberZombieModel<S extends ZombieRenderState> extends AbstractZombieModel<S> {
    public CyberZombieModel(ModelPart root) {
        super(root);
    }
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = AbstractZombieModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
