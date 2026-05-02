package com.maxwell.cyber_ware_port.common.block.radio.tower;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RadioTowerCoreBlockEntity extends BlockEntity {
    private static final int BASE_HEIGHT = 4;
    private static final int SHAFT_HEIGHT = 6;
    private static final int TOTAL_HEIGHT = SHAFT_HEIGHT + BASE_HEIGHT;

    public RadioTowerCoreBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RADIO_TOWER_CORE.get(), pPos, pBlockState);
    }

    public void deformFencesOnly() {
        if (this.level == null || this.level.isClientSide()) return;
        setStructureState(false, false);
    }

    public void tryToFormStructure() {
        if (this.level == null || this.level.isClientSide()) return;
        if (checkStructure()) {
            setStructureState(true);
        }
    }

    public void deformStructure() {
        if (this.level == null || this.level.isClientSide()) return;
        setStructureState(false);
    }

    private void setStructureState(boolean formed) {
        setStructureState(formed, true);
    }

    private void setStructureState(boolean formed, boolean includeCore) {
        if (this.level == null) return;
        BlockPos corePos = this.worldPosition;
        if (includeCore) {
            BlockState coreState = this.getBlockState();
            if (coreState.hasProperty(RadioTowerCoreBlock.FORMED)) {
                this.level.setBlock(corePos, coreState.setValue(RadioTowerCoreBlock.FORMED, formed), 3);
            }
        }
        for (int yOffset = 1; yOffset <= SHAFT_HEIGHT; yOffset++) {
            updateFenceState(this.level, corePos.below(yOffset), formed);
        }
        for (int yOffset = SHAFT_HEIGHT + 1; yOffset <= TOTAL_HEIGHT; yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    updateFenceState(this.level, layerCenter.offset(x, 0, z), formed);
                }
            }
        }
    }
    private int checkDelay = 0;
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        checkDelay++;
        if (checkDelay >= 20) { // 1秒ごとにチェック
            checkDelay = 0;
            boolean currentlyValid = checkStructure();
            boolean isFormed = state.getValue(RadioTowerCoreBlock.FORMED);

            if (isFormed && !currentlyValid) {
                deformStructure(); // 壊れたら解除
            } else if (!isFormed && currentlyValid) {
                tryToFormStructure(); // 揃ったら形成
            }
        }

        // アクティブ時の時間更新
        if (state.getValue(RadioTowerCoreBlock.FORMED)) {
            RadioTowerCoreBlock.LAST_TOWER_ACTIVE_TIME.put(level.dimension(), level.getGameTime());
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

    private boolean checkStructure() {
        if (this.level == null) return false;
        BlockPos corePos = this.worldPosition;
        for (int yOffset = 1; yOffset <= SHAFT_HEIGHT; yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos targetPos = layerCenter.offset(x, 0, z);
                    BlockState state = this.level.getBlockState(targetPos);
                    if (x == 0 && z == 0) {
                        if (!state.is(ModBlocks.RADIO_TOWER_COMPONENT.get())) return false;
                    } else {
                        if (!state.isAir()) return false;
                    }
                }
            }
        }
        for (int yOffset = SHAFT_HEIGHT + 1; yOffset <= TOTAL_HEIGHT; yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockState state = this.level.getBlockState(layerCenter.offset(x, 0, z));
                    if (!state.is(ModBlocks.RADIO_TOWER_COMPONENT.get())) return false;
                }
            }
        }
        return true;
    }
}