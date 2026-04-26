package com.maxwell.cyber_ware_port.common.entity.monster.cyberwither;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CyberWitherRenderState extends LivingEntityRenderState {
    public final float[] xHeadRots = new float[2];
    public final float[] yHeadRots = new float[2];
    public final List<Vec3> minionOffsets = new ArrayList<>();
    public int invulnerableTicks;
    public boolean isPowered;
    public float eyeHeight;
}