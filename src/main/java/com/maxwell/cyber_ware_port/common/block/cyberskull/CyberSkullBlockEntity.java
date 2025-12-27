package com.maxwell.cyber_ware_port.common.block.cyberskull;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CyberSkullBlockEntity extends SkullBlockEntity {
    public CyberSkullBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);

    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.CYBER_SKULL.get();

    }
}