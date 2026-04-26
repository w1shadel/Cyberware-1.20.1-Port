package com.maxwell.cyber_ware_port.common.entity.monster.cyberwither;

import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CyberWitherBossRenderer extends MobRenderer<CyberWitherBoss, CyberWitherRenderState, CyberWitherModel> {
    private static final Identifier BEAM_LOCATION = Identifier.withDefaultNamespace("textures/entity/guardian_beam.png");
    private static final Identifier TEXTURE_A = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/wither/cyber_wither.png");
    private static final Identifier TEXTURE_B = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/wither/cyber_wither_invulnerable.png");

    public CyberWitherBossRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberWitherModel(context.bakeLayer(CyberWitherModel.LAYER_LOCATION)), 1.0F);
        this.addLayer(new CyberWitherArmorLayer(this, context.getModelSet()));
    }

    @Override
    public CyberWitherRenderState createRenderState() {
        return new CyberWitherRenderState();
    }

    @Override
    public void extractRenderState(CyberWitherBoss entity, CyberWitherRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.invulnerableTicks = entity.getInvulnerableTicks();
        state.isPowered = entity.isPowered();
        state.eyeHeight = entity.getEyeHeight();
        state.minionOffsets.clear();
        Vec3 bossPos = entity.getPosition(partialTick);
        for (int i = 1; i <= 3; i++) {
            int minionId = entity.getMinionId(i);
            if (minionId != -1) {
                net.minecraft.world.entity.Entity minion = entity.level().getEntity(minionId);
                if (minion != null && minion.isAlive()) {
                    Vec3 minionPos = minion.getPosition(partialTick);
                    double dy = (minionPos.y + minion.getEyeHeight() * 0.5) - (bossPos.y + entity.getEyeHeight() * 0.5);
                    state.minionOffsets.add(new Vec3(minionPos.x - bossPos.x, dy, minionPos.z - bossPos.z));
                }
            }
        }
    }

    @Override
    public void submit(CyberWitherRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        for (Vec3 offset : state.minionOffsets) {
            float dx = (float) offset.x;
            float dy = (float) offset.y;
            float dz = (float) offset.z;
            float distH = Mth.sqrt(dx * dx + dz * dz);
            float distTotal = Mth.sqrt(dx * dx + dy * dy + dz * dz);
            poseStack.pushPose();
            poseStack.translate(0.0D, state.eyeHeight * 0.5D, 0.0D);
            poseStack.mulPose(Axis.YP.rotation((float) (Math.PI / 2.0D - Math.atan2(dz, dx))));
            poseStack.mulPose(Axis.XP.rotation((float) -Math.atan2(dy, distH)));
            SubmitNodeCollector.CustomGeometryRenderer beamRenderer = (pose, consumer) -> {
                float beamWidth = 0.15F;
                float vScale = distTotal * 0.5F;
                float vOffset = state.ageInTicks * 0.05F * -1.0F;
                this.drawBeamQuad(consumer, pose, distTotal, beamWidth, vOffset, vScale);
            };
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(BEAM_LOCATION), beamRenderer);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(BEAM_LOCATION), beamRenderer);
            poseStack.popPose();
        }
    }

    @Override
    public Identifier getTextureLocation(CyberWitherRenderState state) {
        if (state.isPowered) {
            return TEXTURE_B;
        }
        return TEXTURE_A;
    }

    private void drawBeamQuad(VertexConsumer consumer, PoseStack.Pose pose, float length, float width, float vOffset, float vScale) {
        this.vertex(consumer, pose, -width, 0, 0, 0, vOffset);
        this.vertex(consumer, pose, width, 0, 0, 1, vOffset);
        this.vertex(consumer, pose, width, 0, length, 1, vOffset + vScale);
        this.vertex(consumer, pose, -width, 0, length, 0, vOffset + vScale);
    }

    private void vertex(VertexConsumer c, PoseStack.Pose p, float x, float y, float z, float u, float v) {
        c.addVertex(p.pose(), x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setUv2(15728880, 15728880)
                .setNormal(p, 0, 1, 0);
    }
}