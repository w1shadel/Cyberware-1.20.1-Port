package com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower;

import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
// ★あなたのICyberwareMobの場所に合わせてimportしてください
// import com.Maxwell.cyber_ware_port.Common.Entity.ICyberwareMob;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class RadioTowerCoreBlockEntity extends BlockEntity {

    private int tickCounter = 0;
    private static final int SEARCH_COOLDOWN = 100;
    private static final int ATTRACTION_RANGE = 200;

    // 構造の高さ定義
    private static final int SHAFT_HEIGHT = 6; // 細い部分の高さ
    private static final int BASE_HEIGHT = 4;  // 太い土台の高さ
    private static final int TOTAL_HEIGHT = SHAFT_HEIGHT + BASE_HEIGHT;

    public RadioTowerCoreBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RADIO_TOWER_CORE.get(), pPos, pBlockState);
    }

    // --- NBT保存 (再起動対策) ---
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("TickCounter", this.tickCounter);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.tickCounter = pTag.getInt("TickCounter");
    }

    // --- 構造制御メソッド ---

    public void deformFencesOnly() {
        if (this.getLevel() == null || this.getLevel().isClientSide()) return;
        setStructureState(false, false);
    }

    public void tryToFormStructure() {
        if (this.getLevel() == null || this.getLevel().isClientSide()) return;
        if (checkStructure()) {
            setStructureState(true);
        }
    }

    public void deformStructure() {
        if (this.getLevel() == null || this.getLevel().isClientSide()) return;
        setStructureState(false);
    }

    private void setStructureState(boolean formed) {
        setStructureState(formed, true);
    }

    private void setStructureState(boolean formed, boolean includeCore) {
        Level level = this.getLevel();
        if (level == null) return;

        BlockPos corePos = this.getBlockPos();

        // 1. コアの状態更新
        if (includeCore) {
            BlockState coreState = this.getBlockState();
            if(coreState.hasProperty(RadioTowerCoreBlock.FORMED)) {
                level.setBlock(corePos, coreState.setValue(RadioTowerCoreBlock.FORMED, formed), 3);
            }
        }

        // 2. フェンスの更新処理
        // シャフト部分
        for (int yOffset = 1; yOffset <= SHAFT_HEIGHT; yOffset++) {
            updateFenceState(level, corePos.below(yOffset), formed);
        }
        // 土台部分
        for (int yOffset = SHAFT_HEIGHT + 1; yOffset <= TOTAL_HEIGHT; yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    updateFenceState(level, layerCenter.offset(x, 0, z), formed);
                }
            }
        }
    }

    private void updateFenceState(Level level, BlockPos pos, boolean formed) {
        BlockState state = level.getBlockState(pos);
        if (state.is(ModBlocks.RADIO_TOWER_COMPONENT.get()) && state.hasProperty(RadioTowerFenceBlock.FORMED)) {
            if (state.getValue(RadioTowerFenceBlock.FORMED) != formed) {
                level.setBlock(pos, state.setValue(RadioTowerFenceBlock.FORMED, formed), 3);
            }
        }
    }

    // --- Tick処理とMob誘引 ---

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, RadioTowerCoreBlockEntity pBlockEntity) {
        if (!pState.getValue(RadioTowerCoreBlock.FORMED)) {
            return;
        }

        pBlockEntity.tickCounter++;
        if (pBlockEntity.tickCounter >= SEARCH_COOLDOWN) {
            pBlockEntity.tickCounter = 0;
            pBlockEntity.attractMobs();
        }
    }

    private void attractMobs() {
        Level level = this.getLevel();
        if (level == null) return;

        BlockPos basePos = this.getBlockPos().below(TOTAL_HEIGHT);
        AABB searchBox = new AABB(basePos).inflate(ATTRACTION_RANGE);
        List<PathfinderMob> mobsInRange = level.getEntitiesOfClass(PathfinderMob.class, searchBox);

        for (PathfinderMob mob : mobsInRange) {
            // ここで判別を行う
            if (isCyberMob(mob)) {
                if (mob.distanceToSqr(basePos.getX() + 0.5, basePos.getY(), basePos.getZ() + 0.5) > 4.0) {
                    mob.getNavigation().moveTo(basePos.getX() + 0.5, basePos.getY(), basePos.getZ() + 0.5, 1.2D);
                }
            }
        }
    }

    // --- その他判定 ---

    @Override
    public AABB getRenderBoundingBox() {
        // 描画範囲の拡張（タワーが消えないように大きく確保）
        BlockPos pos = this.getBlockPos();
        return new AABB(
                pos.getX() - 5.0, pos.getY() - 15.0, pos.getZ() - 5.0,
                pos.getX() + 6.0, pos.getY() + 2.0,  pos.getZ() + 6.0
        );
    }

    private boolean checkStructure() {
        Level level = this.getLevel();
        if (level == null) return false;

        BlockPos corePos = this.getBlockPos();

        // 1. シャフトチェック
        for (int yOffset = 1; yOffset <= SHAFT_HEIGHT; yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos targetPos = layerCenter.offset(x, 0, z);
                    BlockState state = level.getBlockState(targetPos);
                    if (x == 0 && z == 0) {
                        if (!state.is(ModBlocks.RADIO_TOWER_COMPONENT.get())) return false;
                    } else {
                        if (!state.isAir()) return false;
                    }
                }
            }
        }

        // 2. 土台チェック
        for (int yOffset = SHAFT_HEIGHT + 1; yOffset <= TOTAL_HEIGHT; yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockState state = level.getBlockState(layerCenter.offset(x, 0, z));
                    if (!state.is(ModBlocks.RADIO_TOWER_COMPONENT.get())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * ICyberwareMob インターフェースを持つモブだけを対象にする
     */
    private static boolean isCyberMob(PathfinderMob mob) {
        // ★修正ポイント: instanceof を使用
        // ここでエラーが出る場合は、ICyberwareMob を正しく import しているか確認してください
        return mob instanceof com.Maxwell.cyber_ware_port.Common.Entity.ICyberwareMob;
    }
}