package com.Maxwell.cyber_ware_port.Common.Block.Radio;


import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;

import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.sounds.SoundEvents;

import net.minecraft.sounds.SoundSource;

import net.minecraft.util.RandomSource;

import net.minecraft.world.item.context.BlockPlaceContext;

import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import net.minecraft.world.level.block.Mirror;

import net.minecraft.world.level.block.Rotation;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.StateDefinition;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.minecraft.world.phys.shapes.CollisionContext;

import net.minecraft.world.phys.shapes.Shapes;
 // ★追加
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;


public class RadioKitBlock extends HorizontalDirectionalBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;


    // --- VoxelShape の定義 ---
    // モデルの座標: 本体の箱 [3, 0, 6]~[15, 4, 14] + アンテナ [2, 0, 1]~[4, 15, 3]
    // これらを結合(Shapes.or)し、各方角用に座標計算したものです。

    // 北向き (Blockbenchのデフォルト)
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(3, 0, 6, 15, 4, 14), // 本体
            Block.box(2, 0, 1, 4, 15, 3)   // アンテナ
    );


    // 南向き (180度回転)
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(1, 0, 2, 13, 4, 10),
            Block.box(12, 0, 13, 14, 15, 15)
    );


    // 西向き (90度回転)
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(6, 0, 1, 14, 4, 13),
            Block.box(1, 0, 12, 3, 15, 14)
    );


    // 東向き (270度回転)
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(2, 0, 3, 10, 4, 15),
            Block.box(13, 0, 2, 15, 15, 4)
    );


    public RadioKitBlock(Properties pProperties) {
        super(pProperties);

        this.registerDefaultState(this.stateDefinition.any().
                setValue(POWERED, Boolean.valueOf(false))
                .setValue(FACING, Direction.NORTH));

    }

    // ... (getStateForPlacement, rotate, mirror はそのまま) ...
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());

    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));

    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));

    }

    // ★ここを書き換え
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);

        switch (direction) {
            case SOUTH:
                return SHAPE_SOUTH;

            case EAST:
                return SHAPE_EAST;

            case WEST:
                return SHAPE_WEST;

            case NORTH:
            default:
                return SHAPE_NORTH;

        }
    }

    // ... (createBlockStateDefinition 以降はそのまま) ...
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);

        pBuilder.add(FACING);

    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            boolean isPowered = pLevel.hasNeighborSignal(pPos);

            if (isPowered != pState.getValue(POWERED)) {
                pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.valueOf(isPowered)), 3);

            }
        }
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pOldState.is(pState.getBlock()) && !pLevel.isClientSide) {
            boolean isPowered = pLevel.hasNeighborSignal(pPos);

            if (isPowered != pState.getValue(POWERED)) {
                pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.valueOf(isPowered)), 2);

            }
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pState.getValue(POWERED)) {
            if (pRandom.nextInt(5) == 0) {
                double centerX = pPos.getX() + 0.5;

                double centerY = pPos.getY() + 0.7;

                double centerZ = pPos.getZ() + 0.5;

                pLevel.addParticle(ParticleTypes.SMOKE, centerX, centerY, centerZ, 0.0D, 0.05D, 0.0D);

            }
            if (pRandom.nextInt(100) == 0) {
                pLevel.playSound(null, pPos,
                        SoundEvents.BEACON_AMBIENT,
                        SoundSource.BLOCKS,
                        0.2F,
                        1.5F);

            }
        }
    }
}