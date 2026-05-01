package com.maxwell.cyber_ware_port.common.entity.monster.cyberwitherskeleton;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class CyberWitherSkeletonModel<S extends CyberWitherSkeletonRenderState> extends HumanoidModel<S> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(CyberWare.MODID, "cyber_witherskeleton"), "main");

    public CyberWitherSkeletonModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();
        CubeDeformation deformation = CubeDeformation.NONE;
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deformation), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, deformation), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition left_arm = root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, deformation).mirror(false)
                .texOffs(24, 0).addBox(-5.0F, 11.0F, -1.0F, 10.0F, 2.0F, 2.0F, deformation)
                .texOffs(44, 1).addBox(-3.0F, 5.0F, -2.0F, 6.0F, 9.0F, 4.0F, deformation), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, deformation), PartPose.offset(-2.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, deformation).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(CyberWitherSkeletonRenderState state) {
        this.head.yRot = state.yRot * ((float) Math.PI / 180F);
        this.head.xRot = state.xRot * ((float) Math.PI / 180F);
        this.rightArm.xRot = 0.0F;
        this.rightArm.yRot = 0.0F;
        this.rightArm.zRot = 0.0F;
        this.leftArm.xRot = 0.0F;
        this.leftArm.yRot = 0.0F;
        this.leftArm.zRot = 0.0F;
        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = state.walkAnimationSpeed;
        this.rightArm.xRot += Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.xRot += Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        if (state.isAggressive) {
            if (state.isHoldingBow) {
                this.rightArm.yRot = this.head.yRot - 0.1F;
                this.leftArm.yRot = this.head.yRot + 0.1F;
                this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot -= 0.05F;
                this.leftArm.yRot += 0.3F;
            } else {
                float attackTime = state.attackTime;
                float sin1 = Mth.sin(attackTime * (float) Math.PI);
                float sin2 = Mth.sin((1.0F - (1.0F - attackTime) * (1.0F - attackTime)) * (float) Math.PI);
                this.rightArm.zRot = 0.0F;
                this.leftArm.zRot = 0.0F;
                this.rightArm.yRot = -(0.1F - sin1 * 0.6F);
                this.leftArm.yRot = 0.1F - sin1 * 0.6F;
                this.rightArm.xRot = (-(float) Math.PI / 2F);
                this.leftArm.xRot = (-(float) Math.PI / 2F);
                this.rightArm.xRot -= sin1 * 1.2F - sin2 * 0.4F;
                this.leftArm.xRot -= sin1 * 1.2F - sin2 * 0.4F;
            }
        }
        if (!state.isAggressive) {
            float age = state.ageInTicks;
            this.rightArm.zRot += Mth.cos(age * 0.09F) * 0.05F + 0.05F;
            this.leftArm.zRot -= Mth.cos(age * 0.09F) * 0.05F + 0.05F;
            this.rightArm.xRot += Mth.sin(age * 0.067F) * 0.05F;
            this.leftArm.xRot -= Mth.sin(age * 0.067F) * 0.05F;
        }
    }
}