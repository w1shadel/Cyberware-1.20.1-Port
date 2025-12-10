package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberCreeper;import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@SuppressWarnings("removal")
public class CyberCreeperModel extends HierarchicalModel<CyberCreeperEntity> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(CyberWare.MODID, "cyber_creeper"), "main");private final ModelPart body;

    private final ModelPart head;

    private final ModelPart leg0;

    private final ModelPart leg1;

    private final ModelPart leg2;

    private final ModelPart leg3;public CyberCreeperModel(ModelPart root) {
        this.body = root.getChild("body");

        this.head = this.body.getChild("head");

        this.leg0 = this.body.getChild("leg0");

        this.leg1 = this.body.getChild("leg1");

        this.leg2 = this.body.getChild("leg2");

        this.leg3 = this.body.getChild("leg3");

    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();

        PartDefinition partdefinition = meshdefinition.getRoot();PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -18.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 0.0F));PartDefinition leg0 = body.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -6.0F, 4.0F));PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -6.0F, 4.0F));PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -6.0F, -4.0F));PartDefinition leg3 = body.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -6.0F, -4.0F));return LayerDefinition.create(meshdefinition, 64, 32);

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

    }

    @Override
    public ModelPart root() {
        return body;

    }

    @Override
    public void setupAnim(CyberCreeperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);

        this.head.xRot = headPitch * ((float)Math.PI / 180F);
this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;this.leg2.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        this.leg3.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

    }
}