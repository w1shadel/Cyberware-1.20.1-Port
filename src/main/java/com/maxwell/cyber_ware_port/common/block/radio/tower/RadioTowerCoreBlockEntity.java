package com.maxwell.cyber_ware_port.common.block.radio.tower;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class RadioTowerCoreBlockEntity extends BlockEntity {
    private static final int SPAWN_COOLDOWN = 300;
    private int tickCounter = 0;
    private static final int BASE_HEIGHT = 4;
    private static final int SHAFT_HEIGHT = 6;
    private static final int TOTAL_HEIGHT = SHAFT_HEIGHT + BASE_HEIGHT;

    public RadioTowerCoreBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RADIO_TOWER_CORE.get(), pPos, pBlockState);
    }

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
        if (includeCore) {
            BlockState coreState = this.getBlockState();
            if (coreState.hasProperty(RadioTowerCoreBlock.FORMED)) {
                level.setBlock(corePos, coreState.setValue(RadioTowerCoreBlock.FORMED, formed), 3);
            }
        }
        for (int yOffset = 1;
             yOffset <= SHAFT_HEIGHT;
             yOffset++) {
            updateFenceState(level, corePos.below(yOffset), formed);
        }
        for (int yOffset = SHAFT_HEIGHT + 1;
             yOffset <= TOTAL_HEIGHT;
             yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1;
                 x <= 1;
                 x++) {
                for (int z = -1;
                     z <= 1;
                     z++) {
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

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, RadioTowerCoreBlockEntity pBlockEntity) {
        if (pLevel.isClientSide()) return;
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.getBlockPos();
        return new AABB(
                pos.getX() - 5.0, pos.getY() - 15.0, pos.getZ() - 5.0,
                pos.getX() + 6.0, pos.getY() + 2.0, pos.getZ() + 6.0
        );
    }

    private boolean checkStructure() {
        Level level = this.getLevel();
        if (level == null) return false;
        BlockPos corePos = this.getBlockPos();
        for (int yOffset = 1;
             yOffset <= SHAFT_HEIGHT;
             yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1;
                 x <= 1;
                 x++) {
                for (int z = -1;
                     z <= 1;
                     z++) {
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
        for (int yOffset = SHAFT_HEIGHT + 1;
             yOffset <= TOTAL_HEIGHT;
             yOffset++) {
            BlockPos layerCenter = corePos.below(yOffset);
            for (int x = -1;
                 x <= 1;
                 x++) {
                for (int z = -1;
                     z <= 1;
                     z++) {
                    BlockState state = level.getBlockState(layerCenter.offset(x, 0, z));
                    if (!state.is(ModBlocks.RADIO_TOWER_COMPONENT.get())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}