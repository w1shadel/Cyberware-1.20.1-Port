package com.maxwell.cyber_ware_port.mixin;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class MixinHumanoidModel<S extends HumanoidRenderState> {

    @Shadow
    @Final
    public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightLeg;
    @Shadow @Final public ModelPart leftLeg;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void cyberware$hideCyberLimbs(S state, CallbackInfo ci) {
        if (this.getClass().getSimpleName().contains("CyberLimbModel")) {
            return;
        }

        if (state instanceof AvatarRenderState avatarState) {
            Player player = (Player) Minecraft.getInstance().level.getEntity(avatarState.id);
            if (player == null) return;

            CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            if (data.isCyberwareInstalled(ModItems.SYNTHETIC_SKIN.get())) return;

            // プレイヤー本体や防具モデルの場合のみ、ここが実行される
            if (data.hasCyberRightArm()) this.rightArm.visible = false;
            if (data.hasCyberLeftArm()) this.leftArm.visible = false;
            if (data.hasCyberRightLeg()) this.rightLeg.visible = false;
            if (data.hasCyberLeftLeg()) this.leftLeg.visible = false;
        }
    }
}