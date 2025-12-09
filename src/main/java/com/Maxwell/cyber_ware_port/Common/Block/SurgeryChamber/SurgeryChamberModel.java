package com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber;


import com.Maxwell.cyber_ware_port.CyberWare;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.Model;

import net.minecraft.client.model.geom.ModelLayerLocation;

import net.minecraft.client.model.geom.ModelPart;

import net.minecraft.client.model.geom.PartPose;

import net.minecraft.client.model.geom.builders.*;

import net.minecraft.client.renderer.RenderType;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class SurgeryChamberModel extends Model {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CyberWare.MODID, "surgery_chamber"), "main");

	private final ModelPart root;

	private final ModelPart door_left;

	private final ModelPart door_right;


	public SurgeryChamberModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);

		this.root = root.getChild("root");

		this.door_left = this.root.getChild("door_left");

		this.door_right = this.root.getChild("door_right");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();

		PartDefinition partdefinition = meshdefinition.getRoot();


		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(6.0F, -30.0F, -8.0F, 2.0F, 30.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(52, 64).addBox(-6.0F, -30.0F, 6.0F, 12.0F, 30.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(-6.0F, -1.0F, -8.0F, 12.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(36, 18).addBox(-8.0F, -30.0F, -8.0F, 2.0F, 30.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(36, 0).addBox(-8.0F, -32.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));


		PartDefinition door_left = root.addOrReplaceChild("door_left", CubeListBuilder.create().texOffs(0, 79).addBox(-6.0F, -13.0F, 0.5F, 6.0F, 29.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -17.0F, -7.5F));


		PartDefinition door_right = root.addOrReplaceChild("door_right", CubeListBuilder.create().texOffs(0, 79).addBox(0.0F, -13.0F, 0.5F, 6.0F, 29.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -17.0F, -7.5F));


		return LayerDefinition.create(meshdefinition, 128, 128);

	}
    public void setupAnim(SurgeryChamberBlockEntity entity, float partialTick) {
        if (entity == null) return;

        float progress = entity.prevAnimationProgress + (entity.animationProgress - entity.prevAnimationProgress) * partialTick;

        float scale = 1.0F - progress;

        this.door_left.xScale = scale;

        this.door_right.xScale = scale;

    }

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

	}
}