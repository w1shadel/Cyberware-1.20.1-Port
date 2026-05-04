package com.maxwell.cyber_ware_port.mixin;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class MixinPlayerModel extends HumanoidModel<AvatarRenderState> {

    @Shadow @Final public ModelPart leftSleeve;
    @Shadow @Final public ModelPart rightSleeve;
    @Shadow @Final public ModelPart leftPants;
    @Shadow @Final public ModelPart rightPants;

    public MixinPlayerModel(ModelPart root) { super(root); }

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void cyberware$hideSleeves(AvatarRenderState state, CallbackInfo ci) {
        Player player = (Player) Minecraft.getInstance().level.getEntity(state.id);
        if (player == null) return;

        CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());

        if (data.hasCyberRightArm()) this.rightSleeve.visible = false;
        if (data.hasCyberLeftArm()) this.leftSleeve.visible = false;
        if (data.hasCyberRightLeg()) this.rightPants.visible = false;
        if (data.hasCyberLeftLeg()) this.leftPants.visible = false;
    }
}