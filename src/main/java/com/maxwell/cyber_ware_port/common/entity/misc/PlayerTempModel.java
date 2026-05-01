package com.maxwell.cyber_ware_port.common.entity.misc;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class PlayerTempModel extends EntityModel<PlayerTempModelState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(CyberWare.MODID, "playerinternalpartsmodel"), "main");
    private final ModelPart root;
    private final ModelPart bone;
    private final ModelPart muscal;
    private final ModelPart skin;

    public PlayerTempModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.root = root;
        this.bone = root.getChild("root").getChild("bone");
        this.muscal = root.getChild("root").getChild("muscal");
        this.skin = root.getChild("root").getChild("skin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        root.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5.0F, 0.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        root.addOrReplaceChild("muscal", CubeListBuilder.create().texOffs(0, 12)
                .addBox(-5.0F, 0.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        root.addOrReplaceChild("skin", CubeListBuilder.create().texOffs(0, 24)
                .addBox(-5.0F, 0.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(PlayerTempModelState state) {
        super.setupAnim(state);
        this.skin.visible = true;
        this.muscal.visible = true;
        this.bone.visible = true;
        this.root.yRot = (float) Math.toRadians(state.yRot);
        this.root.xRot = (float) Math.toRadians(-state.xRot);
    }
}