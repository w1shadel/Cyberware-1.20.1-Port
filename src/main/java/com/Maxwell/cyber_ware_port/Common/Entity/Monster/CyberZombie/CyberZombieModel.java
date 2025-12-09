package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberZombie;
import com.Maxwell.cyber_ware_port.CyberWare;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.AnimationUtils;

import net.minecraft.client.model.HierarchicalModel;

import net.minecraft.client.model.geom.ModelLayerLocation;

import net.minecraft.client.model.geom.ModelPart;

import net.minecraft.client.model.geom.PartPose;

import net.minecraft.client.model.geom.builders.*;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.util.Mth;

@SuppressWarnings("removal")
public class CyberZombieModel extends HierarchicalModel<CyberZombieEntity> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(CyberWare.MODID, "cyber_zombie"), "main");


    private final ModelPart waist;

    private final ModelPart body;

    private final ModelPart head;

    private final ModelPart rightArm;

    private final ModelPart leftArm;

    private final ModelPart rightLeg;

    private final ModelPart leftLeg;


    public CyberZombieModel(ModelPart root) {
        this.waist = root.getChild("waist");

        this.body = this.waist.getChild("body");

        this.head = this.body.getChild("head");

        this.rightArm = this.body.getChild("rightArm");

        this.leftArm = this.body.getChild("leftArm");

        this.rightLeg = this.body.getChild("rightLeg");

        this.leftLeg = this.body.getChild("leftLeg");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();

        PartDefinition partdefinition = meshdefinition.getRoot();


        PartDefinition waist = partdefinition.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));


        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));


        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        rightArm.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(-1.0F, 7.0F, 1.0F));


        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));

        leftArm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));


        body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.9F, 12.0F, 0.0F));


        return LayerDefinition.create(meshdefinition, 64, 32);

    }

    @Override
    public void setupAnim(CyberZombieEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

        this.head.xRot = headPitch * ((float)Math.PI / 180F);


        this.rightArm.xRot = 0.0F;

        this.rightArm.yRot = 0.0F;

        this.rightArm.zRot = 0.0F;

        this.leftArm.xRot = 0.0F;

        this.leftArm.yRot = 0.0F;

        this.leftArm.zRot = 0.0F;


        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
animateZombieArms(this.leftArm, this.rightArm, entity.isAggressive(), this.attackTime, ageInTicks);

    }public static void animateZombieArms(ModelPart pLeftArm, ModelPart pRightArm, boolean pIsAggressive, float pAttackTime, float pAgeInTicks) {
        float f = Mth.sin(pAttackTime * (float)Math.PI);

        float f1 = Mth.sin((1.0F - (1.0F - pAttackTime) * (1.0F - pAttackTime)) * (float)Math.PI);


        pRightArm.zRot = 0.0F;

        pLeftArm.zRot = 0.0F;

        pRightArm.yRot = -(0.1F - f * 0.6F);

        pLeftArm.yRot = 0.1F - f * 0.6F;


        float f2 = -(float)Math.PI / (pIsAggressive ? 1.5F : 2.25F);

        pRightArm.xRot = f2;

        pLeftArm.xRot = f2;


        pRightArm.xRot += f * 1.2F - f1 * 0.4F;

        pLeftArm.xRot += f * 1.2F - f1 * 0.4F;


        AnimationUtils.bobArms(pRightArm, pLeftArm, pAgeInTicks);

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

    }

    @Override
    public ModelPart root() {
        return waist;

    }
}