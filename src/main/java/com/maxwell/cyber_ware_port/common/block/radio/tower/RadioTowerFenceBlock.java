package com.maxwell.cyber_ware_port.common.block.radio.tower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RadioTowerFenceBlock extends FenceBlock {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    private static final int MAX_SEARCH_HEIGHT = 10;

    public RadioTowerFenceBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, Boolean.valueOf(false))
                .setValue(EAST, Boolean.valueOf(false))
                .setValue(SOUTH, Boolean.valueOf(false))
                .setValue(WEST, Boolean.valueOf(false))
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(FORMED, Boolean.valueOf(false)));

    }

    @Override
    public boolean connectsTo(BlockState pState, boolean pIsSideSolid, Direction pDirection) {
        return pState.is(this) || pState.getBlock() instanceof RadioTowerCoreBlock || super.connectsTo(pState, pIsSideSolid, pDirection);
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getCollisionShape(BlockState pState,
                                                                        net.minecraft.world.level.BlockGetter pLevel, BlockPos pPos,
                                                                        net.minecraft.world.phys.shapes.CollisionContext pContext) {
        return this.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FORMED);

    }
}