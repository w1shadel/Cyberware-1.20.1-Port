package com.maxwell.cyber_ware_port.common.block.cyberskull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CyberWallSkullBlock extends WallSkullBlock {
    public CyberWallSkullBlock(SkullBlock.Type type, Properties properties) {
        super(type, properties);

    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CyberSkullBlockEntity(pos, state);

    }
}