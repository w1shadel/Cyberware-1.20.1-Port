package com.maxwell.cyber_ware_port.common.block.radio.tower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class RadioTowerFenceBlock extends FenceBlock {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    private static final int MAX_SEARCH_HEIGHT = 10;

    public RadioTowerFenceBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(WATERLOGGED, false)
                .setValue(FORMED, false));

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
    public VoxelShape getCollisionShape(BlockState pState,
                                        BlockGetter pLevel, BlockPos pPos,
                                        CollisionContext pContext) {
        return this.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FORMED);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            var be = findTower(level, pos);
            if (be != null) {
                be.tryToFormStructure();
            }
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pState.getValue(FORMED) && !pState.is(pNewState.getBlock())) {
            var be = findTower(pLevel, pPos);
            if (be != null) {
                be.deformStructure();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    private @Nullable RadioTowerCoreBlockEntity findTower(Level level, BlockPos origin) {
        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
        for (int y = 1; y <= MAX_SEARCH_HEIGHT; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    searchPos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    BlockEntity be = level.getBlockEntity(searchPos);
                    if (be instanceof RadioTowerCoreBlockEntity core) {
                        return core;
                    }
                }
            }
        }
        return null;
    }
}