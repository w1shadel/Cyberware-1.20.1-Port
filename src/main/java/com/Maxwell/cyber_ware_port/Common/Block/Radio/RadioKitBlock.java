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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RadioKitBlock extends HorizontalDirectionalBlock {

    // レッドストーンでON/OFFを管理するためのプロパティ
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    // 当たり判定 (アンテナや箱のような見た目を想定)
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 10, 14);

    public RadioKitBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().
                setValue(POWERED, Boolean.valueOf(false))
                .setValue(FACING, Direction.NORTH));
    }
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
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
        pBuilder.add(FACING);
    }

    // 周囲のブロックが変化したとき (レッドストーン信号の変化を検知)
    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            boolean isPowered = pLevel.hasNeighborSignal(pPos);
            // 現在の状態とレッドストーンの状態が異なれば、ブロックの状態を更新
            if (isPowered != pState.getValue(POWERED)) {
                pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.valueOf(isPowered)), 3);
            }
        }
    }

    // ブロックが設置されたときにも一度レッドストーン信号を確認
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
        // POWERED プロパティが true (起動中) の場合のみ実行
        if (pState.getValue(POWERED)) {

            // --- 1. パーティクルの生成 ---

            // 5回に1回くらいの確率でパーティクルを出す (毎回出すとうるさすぎるため)
            if (pRandom.nextInt(5) == 0) {
                // ブロックの中心座標
                double centerX = pPos.getX() + 0.5;
                double centerY = pPos.getY() + 0.7; // 少し上の方から出す
                double centerZ = pPos.getZ() + 0.5;

                // 黒い煙 (SMOKE) パーティクルをスポーンさせる
                // 最後の3つの引数は、パーティクルの初速 (X, Y, Z)。0にするとその場で漂う。
                pLevel.addParticle(ParticleTypes.SMOKE, centerX, centerY, centerZ, 0.0D, 0.05D, 0.0D);
            }

            // --- 2. サウンドの再生 ---

            // 100回に1回くらいの確率で音を鳴らす (かなりレアにする)
            if (pRandom.nextInt(100) == 0) {
                // playSound(Player, BlockPos, SoundEvent, SoundSource, volume, pitch)
                // プレイヤーを指定しないので、その場所にいる全プレイヤーに聞こえる
                pLevel.playSound(null, pPos,
                        SoundEvents.BEACON_AMBIENT, // ビーコンの環境音 (ジジジ...という感じ)
                        SoundSource.BLOCKS, // サウンドカテゴリ: ブロック
                        0.2F,  // 音量 (かなり小さめ)
                        1.5F); // ピッチ (少し高め)
            }
        }
    }
}