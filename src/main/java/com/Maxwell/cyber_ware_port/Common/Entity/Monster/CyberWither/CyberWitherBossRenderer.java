package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWither;import com.Maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@SuppressWarnings("removal")
public class CyberWitherBossRenderer extends MobRenderer<CyberWitherBoss, CyberWitherModel> {

    private static final ResourceLocation WITHER_LOCATION =
            new ResourceLocation(CyberWare.MODID, "textures/entity/wither/cyber_wither.png");private static final ResourceLocation WITHER_INVULNERABLE_LOCATION =
            new ResourceLocation(CyberWare.MODID, "textures/entity/wither/cyber_wither_invulnerable.png");

    private static final ResourceLocation BEAM_LOCATION =
            new ResourceLocation("textures/entity/guardian_beam.png");
 
    public CyberWitherBossRenderer(EntityRendererProvider.Context context) {
        super(context, new CyberWitherModel(context.bakeLayer(CyberWitherModel.LAYER_LOCATION)), 1.0F);

        this.addLayer(new CyberWitherArmorLayer(this, context.getModelSet()));

    }
    @Override
    public void render(CyberWitherBoss pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
renderChains(pEntity, pPartialTicks, pPoseStack, pBuffer);

    }

    private void renderChains(CyberWitherBoss wither, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {

        for (int i = 1;
 i <= 3;
 i++) {
            int entityId = wither.getMinionId(i);

            if (entityId != -1) {
                Entity minion = wither.level().getEntity(entityId);

                if (minion != null) {
                    renderSingleChain(wither, minion, partialTick, poseStack, buffer);

                }
            }
        }
    }

    private void renderSingleChain(CyberWitherBoss wither, Entity minion, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {

        double witherX = Mth.lerp(partialTick, wither.xo, wither.getX());

        double witherY = Mth.lerp(partialTick, wither.yo, wither.getY());

        double witherZ = Mth.lerp(partialTick, wither.zo, wither.getZ());double minionX = Mth.lerp(partialTick, minion.xo, minion.getX());

        double minionY = Mth.lerp(partialTick, minion.yo, minion.getY());

        double minionZ = Mth.lerp(partialTick, minion.zo, minion.getZ());float dx = (float) (minionX - witherX);

        float dy = (float) ((minionY + minion.getEyeHeight() * 0.5) - (witherY + wither.getEyeHeight() * 0.5));

        float dz = (float) (minionZ - witherZ);float distH = Mth.sqrt(dx * dx + dz * dz);

        float distTotal = Mth.sqrt(dx * dx + dy * dy + dz * dz);poseStack.pushPose();
poseStack.translate(0.0D, wither.getEyeHeight() * 0.5D, 0.0D);
poseStack.mulPose(Axis.YP.rotation((float) (Math.PI / 2.0D - Math.atan2(dz, dx))));

        poseStack.mulPose(Axis.XP.rotation((float) -Math.atan2(dy, distH)));VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(BEAM_LOCATION));float beamWidth = 0.15F;

        float vScale = distTotal * 0.5F;float age = wither.tickCount + partialTick;

        float vOffset = age * 0.05F * -1.0F;PoseStack.Pose pose = poseStack.last();

        Matrix4f matrix4f = pose.pose();

        Matrix3f matrix3f = pose.normal();drawVertex(vertexconsumer, matrix4f, matrix3f, distTotal, beamWidth, 0.0F, 1.0F, 0.5F, 1.0F, vOffset, vScale);poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));

        pose = poseStack.last();

        matrix4f = pose.pose();

        matrix3f = pose.normal();

        drawVertex(vertexconsumer, matrix4f, matrix3f, distTotal, beamWidth, 0.0F, 1.0F, 0.5F, 1.0F, vOffset, vScale);poseStack.popPose();

    }

    private void drawVertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal, float length, float width, float r, float g, float b, float alpha, float vOffset, float vScale) {
        consumer.vertex(pose, -width, 0, 0).color(r, g, b, alpha).uv(0, vOffset).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();

        consumer.vertex(pose, width, 0, 0).color(r, g, b, alpha).uv(1, vOffset).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();

        consumer.vertex(pose, width, 0, length).color(r, g, b, alpha).uv(1, vOffset + vScale).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();

        consumer.vertex(pose, -width, 0, length).color(r, g, b, alpha).uv(0, vOffset + vScale).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();

    }
    @Override
    protected int getBlockLightLevel(CyberWitherBoss pEntity, BlockPos pPos) {
        return 15;

    }
    @Override
    public boolean shouldRender(CyberWitherBoss pEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {

        if (super.shouldRender(pEntity, pCamera, pCamX, pCamY, pCamZ)) {
            return true;

        }for (int i = 1;
 i <= 3;
 i++) {
            int entityId = pEntity.getMinionId(i);

            if (entityId != -1) {
                Entity minion = pEntity.level().getEntity(entityId);

                if (minion != null) {
                    Vec3 start = pEntity.position();

                    Vec3 end = minion.position();
if (pCamera.isVisible(new AABB(start.x, start.y, start.z, end.x, end.y, end.z))) {
                        return true;

                    }
                }
            }
        }

        return false;

    }
    @Override
    public ResourceLocation getTextureLocation(CyberWitherBoss pEntity) {
        int invulTicks = pEntity.getInvulnerableTicks();
if (invulTicks > 0) {
            return WITHER_LOCATION;

        }

        return pEntity.isPowered() ? WITHER_INVULNERABLE_LOCATION : WITHER_LOCATION;

    }

    @Override
    protected float getWhiteOverlayProgress(CyberWitherBoss pEntity, float pPartialTicks) {
        int invulTicks = pEntity.getInvulnerableTicks();if (invulTicks > 0) {

            float progress = 1.0F - ((float)invulTicks - pPartialTicks) / 220.0F;if (invulTicks < 10) {
                return 1.0F;

            }float flash = Mth.sin(progress * progress * 20.0F * (float)Math.PI);
return Mth.clamp(flash, 0.0F, 0.6F);
 
        }

        return super.getWhiteOverlayProgress(pEntity, pPartialTicks);

    }

    @Override
    protected void scale(CyberWitherBoss pLivingEntity, PoseStack pPoseStack, float pPartialTickTime) {
        float f = 2.0F;int i = pLivingEntity.getInvulnerableTicks();

        if (i > 0) {
            f -= ((float)i - pPartialTickTime) / 220.0F * 0.5F;

        }

        pPoseStack.scale(f, f, f);

    }
}