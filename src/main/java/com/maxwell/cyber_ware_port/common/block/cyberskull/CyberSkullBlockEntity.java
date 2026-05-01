package com.maxwell.cyber_ware_port.common.block.cyberskull;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CyberSkullBlockEntity extends BlockEntity {
    private int animationTickCount;

    public CyberSkullBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CYBER_SKULL.get(), pos, state);
    }

    public float getAnimation(float partialTicks) {
        return (float) this.animationTickCount;
    }
}