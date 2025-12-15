package com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower;

import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
        if (pBlockEntity.tickCounter == 0) {
        }
        if (!pState.getValue(RadioTowerCoreBlock.FORMED)) {
            return;
        }
        pBlockEntity.tickCounter++;
        if (pBlockEntity.tickCounter >= SPAWN_COOLDOWN) {
            pBlockEntity.tickCounter = 0;
            pBlockEntity.spawnMobsAroundTower((ServerLevel) pLevel, pPos, pLevel.getRandom());
        }
    }

    private void spawnMobsAroundTower(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        Player player = pLevel.getNearestPlayer(pPos.getX(), pPos.getY(), pPos.getZ(), 80, true);
        if (player == null) {
            return;
        }
        int spawnCount = 2 + pRandom.nextInt(3);
        for (int i = 0; i < spawnCount; i++) {
            spawnOneCyberMob(pLevel, pPos, pRandom);
        }
    }

    private void spawnOneCyberMob(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        List<EntityType<? extends Monster>> spawnList = new ArrayList<>();
        if (pLevel.dimension() == Level.NETHER) {
            spawnList.add(ModEntities.CYBER_WITHER_SKELETON.get());
        } else {
            spawnList.add(ModEntities.CYBER_ZOMBIE.get());
            spawnList.add(ModEntities.CYBER_SKELETON.get());
            spawnList.add(ModEntities.CYBER_CREEPER.get());
        }
        EntityType<? extends Monster> mobToSpawn = spawnList.get(pRandom.nextInt(spawnList.size()));
        BlockPos groundLevelPos = pPos.below(TOTAL_HEIGHT);
        for (int i = 0; i < 15; i++) {
            int minDistance = 16;
            int maxDistance = 32;
            float angle = pRandom.nextFloat() * 2.0F * (float) Math.PI;
            int distance = minDistance + pRandom.nextInt(maxDistance - minDistance);
            int x = groundLevelPos.getX() + (int) (Math.cos(angle) * distance);
            int z = groundLevelPos.getZ() + (int) (Math.sin(angle) * distance);
            BlockPos potentialPos = new BlockPos(x, groundLevelPos.getY(), z);
            BlockPos spawnPos = findValidSpawnPos(pLevel, potentialPos);
            if (spawnPos != null) {
                Monster monster = mobToSpawn.create(pLevel);
                if (monster != null) {
                    monster.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, pRandom.nextFloat() * 360.0F, 0.0F);
                    monster.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(spawnPos), MobSpawnType.EVENT, null, null);
                    pLevel.addFreshEntity(monster);
                    pLevel.playSound(null, pPos, SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 1.0F, 1.5F);
                    return;
                }
            }
        }

    }

    @Nullable
    private BlockPos findValidSpawnPos(Level pLevel, BlockPos pPos) {
        for (int yOffset = -10; yOffset <= 5; yOffset++) {
            BlockPos currentPos = pPos.above(yOffset);
            if (isValidSpawnLocation(pLevel, currentPos)) {
                return currentPos;
            }
        }
        return null;
    }

    private boolean isValidSpawnLocation(Level level, BlockPos pos) {
        BlockState groundState = level.getBlockState(pos.below());
        BlockState mainState = level.getBlockState(pos);
        BlockState headState = level.getBlockState(pos.above());
        return groundState.isSolidRender(level, pos.below()) && !mainState.isCollisionShapeFullBlock(level, pos) && !headState.isCollisionShapeFullBlock(level, pos);
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