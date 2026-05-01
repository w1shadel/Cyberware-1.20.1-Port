package com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class CyberCreeperModel extends EntityModel<CyberCreeperRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CyberWare.MODID, "cyber_creeper"), "main");
    public static final ModelLayerLocation ARMOR_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CyberWare.MODID, "cyber_creeper"), "armor");
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;

    public CyberCreeperModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leg0 = root.getChild("leg0");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
    }

    public static LayerDefinition createBodyLayer() {
        return createBodyLayer(CubeDeformation.NONE);
    }

    public static LayerDefinition createArmorLayer() {
        return createBodyLayer(new CubeDeformation(2.0F));
    }

    private static LayerDefinition createBodyLayer(CubeDeformation deformation) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation),
                PartPose.offset(0.0F, 6.0F, 0.0F));
        partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformation),
                PartPose.offset(0.0F, 6.0F, 0.0F));
        partdefinition.addOrReplaceChild("leg0",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, deformation),
                PartPose.offset(-2.0F, 18.0F, 4.0F));
        partdefinition.addOrReplaceChild("leg1",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, deformation),
                PartPose.offset(2.0F, 18.0F, 4.0F));
        partdefinition.addOrReplaceChild("leg2",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, deformation),
                PartPose.offset(-2.0F, 18.0F, -4.0F));
        partdefinition.addOrReplaceChild("leg3",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, deformation),
                PartPose.offset(2.0F, 18.0F, -4.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(CyberCreeperRenderState state) {
        super.setupAnim(state);
        this.head.yRot = state.yRot * ((float) Math.PI / 180F);
        this.head.xRot = state.xRot * ((float) Math.PI / 180F);
        float animationSpeed = state.walkAnimationSpeed;
        float animationPos = state.walkAnimationPos;
        this.leg0.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
        this.leg1.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
        this.leg2.xRot = Mth.cos(animationPos * 0.6662F + (float) Math.PI) * 1.4F * animationSpeed;
        this.leg3.xRot = Mth.cos(animationPos * 0.6662F) * 1.4F * animationSpeed;
    }
}