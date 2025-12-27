package com.maxwell.cyber_ware_port.common.block.cyberskull;

import com.maxwell.cyber_ware_port.common.entity.monster.cyberwither.CyberWitherBoss;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModEntities;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;

import javax.annotation.Nullable;

public class CyberSkullBlock extends SkullBlock {
    @Nullable
    private static BlockPattern witherPatternFull;
    @Nullable
    private static BlockPattern witherPatternBase;

    public CyberSkullBlock(SkullBlock.Type type, Properties properties) {
        super(type, properties);

    }

    public static void checkSpawn(Level level, BlockPos pos, SkullBlockEntity skull) {
        if (!level.isClientSide) {
            BlockState blockstate = skull.getBlockState();
            boolean isBase = blockstate.is(ModBlocks.CYBER_WITHER_SKELETON_SKULL.get()) || blockstate.is(ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get());
            if (isBase && pos.getY() >= level.getMinBuildHeight() && level.getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL) {
                BlockPattern pattern = getOrCreateWitherFull();
                BlockPattern.BlockPatternMatch match = pattern.find(level, pos);
                if (match != null) {
                    for (int i = 0;
                         i < pattern.getWidth();
                         ++i) {
                        for (int j = 0;
                             j < pattern.getHeight();
                             ++j) {
                            BlockInWorld blockinworld = match.getBlock(i, j, 0);
                            level.setBlock(blockinworld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                            level.levelEvent(2001, blockinworld.getPos(), getId(blockinworld.getState()));

                        }
                    }
                    CyberWitherBoss boss = ModEntities.CYBER_WITHER.get().create(level);
                    if (boss != null) {
                        BlockPos blockpos = match.getBlock(1, 2, 0).getPos();
                        boss.moveTo((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.55D, (double) blockpos.getZ() + 0.5D, match.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
                        boss.yBodyRot = match.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
                        for (ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, boss.getBoundingBox().inflate(50.0D))) {
                            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, boss);

                        }
                        level.addFreshEntity(boss);
                        for (int k = 0;
                             k < pattern.getWidth();
                             ++k) {
                            for (int l = 0;
                                 l < pattern.getHeight();
                                 ++l) {
                                level.blockUpdated(match.getBlock(k, l, 0).getPos(), Blocks.AIR);

                            }
                        }
                    }
                }
            }
        }
    }

    private static BlockPattern getOrCreateWitherFull() {
        if (witherPatternFull == null) {
            witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~")
                    .where('#', (block) -> block.getState().is(net.minecraft.tags.BlockTags.WITHER_SUMMON_BASE_BLOCKS))
                    .where('^', (block) -> block.getState().is(ModBlocks.CYBER_WITHER_SKELETON_SKULL.get()) || block.getState().is(ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get()))
                    .where('~', (block) -> block.getState().isAir())
                    .build();

        }
        return witherPatternFull;

    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof SkullBlockEntity) {
            checkSpawn(level, pos, (SkullBlockEntity) blockentity);

        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CyberSkullBlockEntity(pos, state);

    }
}