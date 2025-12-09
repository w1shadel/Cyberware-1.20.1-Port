package com.Maxwell.cyber_ware_port.Common.Block.CWB;
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
public class CyberWareWorkBenchModel extends Model {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "cyberwareworkbenchmodel"), "main");

	private final ModelPart root;

	private final ModelPart hammer;


	public CyberWareWorkBenchModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);

        this.root = root.getChild("root");

		this.hammer = this.root.getChild("hammer");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();

		PartDefinition partdefinition = meshdefinition.getRoot();


		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(34, 32).addBox(-4.0F, -32.0F, 4.0F, 8.0F, 16.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-4.0F, -32.0F, -5.0F, 8.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));


		PartDefinition hammer = root.addOrReplaceChild("hammer", CubeListBuilder.create().texOffs(0, 49).addBox(-3.0F, -0.5F, -3.5F, 6.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(26, 52).addBox(-1.0F, -7.5F, -1.5F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -23.5F, -0.5F));


		return LayerDefinition.create(meshdefinition, 64, 64);

	}
    public void setupAnim(CyberwareWorkbenchBlockEntity pBlockEntity, float pPartialTick) {
        if (pBlockEntity == null) return;


        float progress = pBlockEntity.getRenderProgress(pPartialTick);


        float initialY = -23.5F;
float t1 = 0.1668F;

        float t2 = 0.6668F;
float targetOffset = 6.0F;


        float currentOffset;


        if (progress < t1) {float t = progress / t1;


            currentOffset = 0.0F + (targetOffset - 0.0F) * t;


        } else if (progress < t2) {

            currentOffset = targetOffset;


        } else {float t = (progress - t2) / (1.0F - t2);


            currentOffset = targetOffset + (0.0F - targetOffset) * t;

        }

        this.hammer.y = initialY + currentOffset;

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

    }
}