package com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CyberCreeperRenderState extends LivingEntityRenderState {
    public float swelling;
    public boolean isPowered;
}
