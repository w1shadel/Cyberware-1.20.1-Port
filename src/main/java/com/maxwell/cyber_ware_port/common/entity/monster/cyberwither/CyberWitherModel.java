package com.maxwell.cyber_ware_port.common.entity.monster.cyberwither;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class CyberWitherModel extends HierarchicalModel<CyberWitherBoss> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CyberWare.MODID, "cyber_wither"), "main");
    private final ModelPart root;
    private final ModelPart centerHead;
    private final ModelPart rightHead;
    private final ModelPart leftHead;
    private final ModelPart ribcage;
    private final ModelPart tail;

    public CyberWitherModel(ModelPart pRoot) {
        this.root = pRoot;
        this.ribcage = pRoot.getChild("ribcage");
        this.tail = pRoot.getChild("tail");
        this.centerHead = pRoot.getChild("center_head");
        this.rightHead = pRoot.getChild("right_head");
        this.leftHead = pRoot.getChild("left_head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        CubeDeformation deformation = CubeDeformation.NONE;
        partdefinition.addOrReplaceChild("shoulders", CubeListBuilder.create().texOffs(0, 16).addBox(-10.0F, 3.9F, -0.5F, 20.0F, 3.0F, 3.0F, deformation), PartPose.ZERO);
        partdefinition.addOrReplaceChild("ribcage", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 3.0F, 10.0F, 3.0F, deformation).texOffs(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11.0F, 2.0F, 2.0F, deformation).texOffs(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11.0F, 2.0F, 2.0F, deformation).texOffs(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11.0F, 2.0F, 2.0F, deformation), PartPose.offsetAndRotation(-2.0F, 6.9F, -0.5F, 0.20420352F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 22).addBox(0.0F, 0.0F, 0.0F, 3.0F, 6.0F, 3.0F, deformation), PartPose.offsetAndRotation(-2.0F, 6.9F + Mth.cos(0.20420352F) * 10.0F, -0.5F + Mth.sin(0.20420352F) * 10.0F, 0.83252203F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("center_head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation), PartPose.ZERO);
        CubeListBuilder headBuilder = CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 6.0F, 6.0F, 6.0F, deformation);
        partdefinition.addOrReplaceChild("right_head", headBuilder, PartPose.offset(-8.0F, 4.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_head", headBuilder, PartPose.offset(10.0F, 4.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private void setupHeadRotation(CyberWitherBoss pWither, ModelPart pPart, int pHead) {
        pPart.yRot = (pWither.getHeadYRot(pHead) - pWither.yBodyRot) * ((float) Math.PI / 180F);
        pPart.xRot = pWither.getHeadXRot(pHead) * ((float) Math.PI / 180F);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(CyberWitherBoss pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        float f = Mth.cos(pAgeInTicks * 0.1F);
        this.ribcage.xRot = (0.065F + 0.05F * f) * (float) Math.PI;
        this.tail.setPos(-2.0F, 6.9F + Mth.cos(this.ribcage.xRot) * 10.0F, -0.5F + Mth.sin(this.ribcage.xRot) * 10.0F);
        this.tail.xRot = (0.265F + 0.1F * f) * (float) Math.PI;
        this.centerHead.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
        this.centerHead.xRot = pHeadPitch * ((float) Math.PI / 180F);
    }

    @Override
    public void prepareMobModel(CyberWitherBoss pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        this.setupHeadRotation(pEntity, this.rightHead, 1);
        this.setupHeadRotation(pEntity, this.leftHead, 2);
    }
}