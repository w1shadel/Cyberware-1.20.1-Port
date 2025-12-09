package com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower;


import net.minecraft.core.BlockPos;

import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.FenceBlock;

import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.StateDefinition;

import net.minecraft.world.level.block.state.properties.BooleanProperty;


public class RadioTowerFenceBlock extends FenceBlock {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");


    // コアが存在しうる最大の高さ (シャフト6 + 土台4 = 10)
    private static final int MAX_SEARCH_HEIGHT = 10;

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
         return true;

    }
    public RadioTowerFenceBlock(Properties pProperties) {
        super(pProperties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, Boolean.valueOf(false))
                .setValue(EAST, Boolean.valueOf(false))
                .setValue(SOUTH, Boolean.valueOf(false))
                .setValue(WEST, Boolean.valueOf(false))
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(FORMED, Boolean.valueOf(false))
        );

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);

        pBuilder.add(FORMED);

    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        // サーバー側かつ、FORMED状態のフェンスが破壊された(別のブロックになった)場合
        if (!pLevel.isClientSide && pState.getValue(FORMED) && !pState.is(pNewState.getBlock())) {

            BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();


            // 上空 1～10 ブロックの範囲にCoreがないか探す
            // 範囲: X(-1~1), Z(-1~1), Y(1~10)
            // コアが土台の端の上にある場合などを考慮して、X/Zも少し広めに探す
            for (int y = 1;
 y <= MAX_SEARCH_HEIGHT;
 y++) {
                for (int x = -1;
 x <= 1;
 x++) {
                    for (int z = -1;
 z <= 1;
 z++) {
                        searchPos.set(pPos.getX() + x, pPos.getY() + y, pPos.getZ() + z);


                        BlockEntity be = pLevel.getBlockEntity(searchPos);

                        if (be instanceof RadioTowerCoreBlockEntity core) {
                            // Coreが見つかったら構造解除処理を呼び出す
                            core.deformStructure();

                            // 1つ見つかれば十分なのでループを抜ける（必要なら）
                            // return;

                        }
                    }
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);

    }
}