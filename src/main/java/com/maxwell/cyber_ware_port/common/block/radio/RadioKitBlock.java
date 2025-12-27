package com.maxwell.cyber_ware_port.common.block.radio;

import com.maxwell.cyber_ware_port.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RadioKitBlock extends HorizontalDirectionalBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(3, 0, 6, 15, 4, 14),
            Block.box(2, 0, 1, 4, 15, 3)
    );

    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(1, 0, 2, 13, 4, 10),
            Block.box(12, 0, 13, 14, 15, 15)
    );

    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(6, 0, 1, 14, 4, 13),
            Block.box(1, 0, 12, 3, 15, 14)
    );

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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
        pBuilder.add(FACING);

    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            boolean isPowered = pLevel.hasNeighborSignal(pPos);
            if (isPowered) {
                pLevel.scheduleTick(pPos, this, 20);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            boolean isPoweredNow = pLevel.hasNeighborSignal(pPos);
            boolean wasPowered = pState.getValue(POWERED);
            if (isPoweredNow != wasPowered) {
                pLevel.setBlock(pPos, pState.setValue(POWERED, isPoweredNow), 3);
                if (isPoweredNow) {
                    pLevel.scheduleTick(pPos, this, 20);
                }
            }
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pState.getValue(POWERED) || pLevel.isClientSide()) {
            return;
        }
        if (pRandom.nextFloat() < 0.3F) {
            spawnCyberMob(pLevel, pPos, pRandom);
        }
        pLevel.scheduleTick(pPos, this, 400);
    }

    private void spawnCyberMob(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        List<EntityType<? extends Monster>> spawnList = new ArrayList<>();
        if (pLevel.dimension() == Level.NETHER) {
            spawnList.add(ModEntities.CYBER_WITHER_SKELETON.get());
        } else {
            spawnList.add(ModEntities.CYBER_ZOMBIE.get());
            spawnList.add(ModEntities.CYBER_SKELETON.get());
            spawnList.add(ModEntities.CYBER_CREEPER.get());
        }
        EntityType<? extends Monster> mobToSpawn = spawnList.get(pRandom.nextInt(spawnList.size()));
        for (int i = 0; i < 10; i++) {
            int x = pPos.getX() + pRandom.nextInt(17) - 8;
            int z = pPos.getZ() + pRandom.nextInt(17) - 8;
            BlockPos spawnPos = findValidSpawnPos(pLevel, new BlockPos(x, pPos.getY(), z));
            if (spawnPos != null) {
                Monster monster = mobToSpawn.create(pLevel);
                if (monster != null) {
                    monster.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, pRandom.nextFloat() * 360.0F, 0.0F);
                    monster.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(spawnPos), MobSpawnType.EVENT, null, null);
                    pLevel.addFreshEntity(monster);
                    return;
                }
            }
        }
    }

    @Nullable
    private BlockPos findValidSpawnPos(Level pLevel, BlockPos pPos) {
        BlockPos groundPos = new BlockPos(pPos.getX(), pPos.getY(), pPos.getZ());
        for (int yOffset = 0; yOffset <= 3; yOffset++) {
            BlockPos currentPos = groundPos.above(yOffset);
            if (isValidSpawnLocation(pLevel, currentPos)) return currentPos;
            currentPos = groundPos.below(yOffset);
            if (isValidSpawnLocation(pLevel, currentPos)) return currentPos;
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