package com.Maxwell.cyber_ware_port.Common.Block.Scanner;import com.Maxwell.cyber_ware_port.CyberWare;
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
public class ScannerBlockModel extends Model {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(CyberWare.MODID, "scanner_block"), "main");
	private final ModelPart root;
	private final ModelPart scanner;
	private final ModelPart scanner_part;

	public ScannerBlockModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
		this.root = root.getChild("root");
		this.scanner = root.getChild("scanner");
		this.scanner_part = this.scanner.getChild("scanner_part");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(-21.0F, -5.0F, -8.0F, 16.0F, 6.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(64, 11).addBox(-21.0F, -14.0F, -8.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 22).addBox(-21.0F, -15.0F, -8.0F, 1.0F, 1.0F, 15.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-21.0F, -15.0F, 7.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(60, 62).addBox(-6.0F, -15.0F, -8.0F, 1.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 21).addBox(-6.0F, -14.0F, 7.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 22).addBox(-6.0F, -15.0F, -7.0F, 1.0F, 1.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(13.0F, 23.0F, 0.0F));

		PartDefinition cube_r1 = root.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(30, 54).addBox(-0.5F, -0.5F, -7.5F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.5F, -14.5F, 7.5F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r2 = root.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 54).addBox(-0.5F, -0.5F, -7.5F, 1.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-12.5F, -14.5F, -7.5F, 0.0F, 1.5708F, 0.0F));

		PartDefinition scanner = partdefinition.addOrReplaceChild("scanner", CubeListBuilder.create().texOffs(32, 38).addBox(-0.5F, -1.5F, -7.5F, 1.0F, 1.0F, 15.0F, new CubeDeformation(0.0F))
		.texOffs(0, 38).addBox(-0.5F, 0.5F, -7.5F, 1.0F, 1.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(5.5F, 8.5F, 0.5F));

		PartDefinition scanner_part = scanner.addOrReplaceChild("scanner_part", CubeListBuilder.create().texOffs(60, 54).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 2.5F, -5.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		scanner.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
    public void setupMovingParts(boolean isWorking, float time) {
        float scannerAmp = 5.5F;
        float scannerOff = 0.0F;

        float partAmp = 3.0F;
        float partOff = 0.0F;

        float homeScannerX = 5.5F; 
        float homePartZ = -5.0F;   

        if (isWorking) {

            this.scanner.x = scannerOff + (float)Math.sin(time * 0.15F) * scannerAmp;
            this.scanner_part.z = partOff + (float)Math.cos(time * 0.3F) * partAmp;
        } else {this.scanner.x = homeScannerX;
            this.scanner_part.z = homePartZ;
        }
    }
}