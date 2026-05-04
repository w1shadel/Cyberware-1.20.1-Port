package com.maxwell.cyber_ware_port.client.upgrades;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CyberwarePlayerLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier CYBER_SKIN_TEXTURE = Identifier.fromNamespaceAndPath("cyber_ware_port", "textures/entity/cyber_limbs.png");
    private final CyberLimbModel<AvatarRenderState> cyberLimbModel;

    public CyberwarePlayerLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
        super(renderer);
        this.cyberLimbModel = new CyberLimbModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CyberLimbModel.LAYER_LOCATION));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
                       AvatarRenderState state, float yRot, float xRot) {
        Entity entity = Minecraft.getInstance().level.getEntity(state.id);
        if (!(entity instanceof Player player)) return;

        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        if (data.isCyberwareInstalled(ModItems.SYNTHETIC_SKIN.get())) return;

        this.cyberLimbModel.setupAnim(state);

        this.cyberLimbModel.leftArm.visible = data.hasCyberLeftArm();
        this.cyberLimbModel.rightArm.visible = data.hasCyberRightArm();
        this.cyberLimbModel.leftLeg.visible = data.hasCyberLeftLeg();
        this.cyberLimbModel.rightLeg.visible = data.hasCyberRightLeg();

        var cyberRenderType = RenderTypes.entityCutout(CYBER_SKIN_TEXTURE);

        submitNodeCollector.submitModel(
                this.cyberLimbModel,
                state,
                poseStack,
                cyberRenderType,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                0,
                null
        );
    }
}