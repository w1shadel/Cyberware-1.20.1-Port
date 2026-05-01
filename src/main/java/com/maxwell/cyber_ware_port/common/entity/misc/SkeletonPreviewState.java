package com.maxwell.cyber_ware_port.common.entity.misc;

import net.minecraft.client.renderer.entity.state.SkeletonRenderState;

public class SkeletonPreviewState extends SkeletonRenderState {
    public float yRot;
    public float xRot;
    public float scale;

    public SkeletonPreviewState() {
        this.isBaby = false;
    }
}