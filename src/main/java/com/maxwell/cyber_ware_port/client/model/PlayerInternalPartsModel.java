package com.maxwell.cyber_ware_port.client.model;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

@SuppressWarnings("removal")
public class PlayerInternalPartsModel extends Model.Simple {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(CyberWare.MODID, "playerinternalpartsmodel"), "main");
    private final ModelPart root;
    private final ModelPart bone;
    private final ModelPart muscal;
    private final ModelPart skin;

    public PlayerInternalPartsModel(ModelPart root) {
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
        root.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        root.addOrReplaceChild("muscal", CubeListBuilder.create().texOffs(0, 12).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("skin", CubeListBuilder.create().texOffs(0, 24).addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void setVisibleLayer(int type) {
        this.skin.visible = false;
        this.muscal.visible = false;
        this.bone.visible = false;
        switch (type) {
            case 0 -> this.skin.visible = true;
            case 1 -> this.muscal.visible = true;
            case 2 -> this.bone.visible = true;
        }
    }
}