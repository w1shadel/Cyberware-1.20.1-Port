package com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower;import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("remeoval")
public class RadioTowerModel extends Model {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CyberWare.MODID, "radio_tower"), "main");

	private final ModelPart root;

	private final ModelPart bone3;

	private final ModelPart bone2;

	private final ModelPart bone;public RadioTowerModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);

		this.root = root.getChild("root");

		this.bone3 = this.root.getChild("bone3");

		this.bone2 = this.root.getChild("bone2");

		this.bone = this.root.getChild("bone");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();

		PartDefinition partdefinition = meshdefinition.getRoot();PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-24.0F, -4.0F, -23.0F, 48.0F, 4.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));PartDefinition cube_r1 = root.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(48, 52).addBox(-2.0F, -80.0F, -2.0F, 4.0F, 160.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -80.0F, 6.0F, 0.0869F, 0.7816F, 0.1231F));PartDefinition cube_r2 = root.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 52).addBox(-2.0F, -80.0F, -2.0F, 4.0F, 160.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -80.0F, -4.0F, -0.0873F, 0.7854F, 0.0F));PartDefinition cube_r3 = root.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(16, 52).addBox(-2.0F, -80.0F, -2.0F, 4.0F, 160.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -80.0F, -4.0F, -0.0869F, 0.7816F, -0.1231F));PartDefinition cube_r4 = root.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, -80.0F, -2.0F, 4.0F, 160.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -80.0F, 6.0F, 0.0873F, 0.7854F, 0.0F));PartDefinition bone3 = root.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(98, 87).addBox(3.0F, -2.0F, -7.5F, 11.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(64, 101).addBox(3.0F, -2.0F, 3.5F, 10.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -68.0F, 1.5F));PartDefinition cube_r5 = bone3.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(100, 80).addBox(-6.0F, -2.0F, -1.5F, 11.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));PartDefinition cube_r6 = bone3.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(90, 101).addBox(-5.0F, -2.0F, -1.5F, 10.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(14.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));PartDefinition bone2 = root.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(64, 87).addBox(1.0F, -2.0F, -9.5F, 14.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(96, 94).addBox(2.0F, -2.0F, 5.5F, 12.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -45.0F, 1.5F));PartDefinition cube_r7 = bone2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(64, 80).addBox(-7.0F, -2.0F, -1.5F, 15.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));PartDefinition cube_r8 = bone2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(64, 94).addBox(-6.0F, -2.0F, -1.5F, 13.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(16.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(64, 52).addBox(-1.0F, -2.0F, -10.5F, 18.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(64, 59).addBox(-1.0F, -2.0F, 6.5F, 18.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -26.0F, 1.5F));PartDefinition cube_r9 = bone.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(64, 73).addBox(-9.0F, -2.0F, -1.5F, 18.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));PartDefinition cube_r10 = bone.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(64, 66).addBox(-9.0F, -2.0F, -1.5F, 18.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(16.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));return LayerDefinition.create(meshdefinition, 256, 256);

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

	}
}