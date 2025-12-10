package com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber;

import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class SurgeryChamberBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private static final Map<Direction, VoxelShape> LOWER_SHAPES_OPEN = new EnumMap<>(Direction.class);

    private static final Map<Direction, VoxelShape> UPPER_SHAPES_OPEN = new EnumMap<>(Direction.class);

    private static final Map<Direction, VoxelShape> LOWER_SHAPES_CLOSED = new EnumMap<>(Direction.class);

    private static final Map<Direction, VoxelShape> UPPER_SHAPES_CLOSED = new EnumMap<>(Direction.class);

    static {
        VoxelShape floor = Block.box(0, 0, 0, 16, 1, 16);
        VoxelShape ceiling = Block.box(0, 15, 0, 16, 16, 16);
        VoxelShape backWall = Block.box(0, 0, 14, 16, 16, 16);
        VoxelShape rightWall = Block.box(14, 0, 0, 16, 16, 14);
        VoxelShape leftWall = Block.box(0, 0, 0, 2, 16, 14);
        VoxelShape frontWall = Block.box(0, 0, 0, 16, 16, 2);
        VoxelShape baseLowerOpen = Shapes.or(floor, backWall, rightWall, leftWall);
        VoxelShape baseUpperOpen = Shapes.or(ceiling, backWall, rightWall, leftWall);
        VoxelShape baseLowerClosed = Shapes.or(baseLowerOpen, frontWall);
        VoxelShape baseUpperClosed = Shapes.or(baseUpperOpen, frontWall);
        for (Direction direction : Direction.values()) {
            if (direction.getAxis().isHorizontal()) {
                LOWER_SHAPES_OPEN.put(direction, rotateShape(baseLowerOpen, direction));
                UPPER_SHAPES_OPEN.put(direction, rotateShape(baseUpperOpen, direction));
                LOWER_SHAPES_CLOSED.put(direction, rotateShape(baseLowerClosed, direction));
                UPPER_SHAPES_CLOSED.put(direction, rotateShape(baseUpperClosed, direction));

            }
        }
    }

    public SurgeryChamberBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(OPEN, true));

    }

    protected static void preventCreativeDropFromBottomPart(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pPos.below();
            BlockState blockstate = pLevel.getBlockState(blockpos);
            if (blockstate.is(pState.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                pLevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(blockstate));

            }
        }
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction toDir) {
        if (toDir == Direction.NORTH) return shape;
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = 0;
        if (toDir == Direction.EAST) times = 1;
        else if (toDir == Direction.SOUTH) times = 2;
        else if (toDir == Direction.WEST) times = 3;
        for (int i = 0;
             i < times;
             i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                double newMinX = 1 - maxZ;
                double newMaxX = 1 - minZ;
                double newMinZ = minX;
                double newMaxZ = maxX;
                buffer[1] = Shapes.or(buffer[1], Shapes.box(newMinX, minY, newMinZ, newMaxX, maxY, newMaxZ));

            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();

        }
        return buffer[0];

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HALF, OPEN);

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new SurgeryChamberBlockEntity(pPos, pState);

        }
        return null;

    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;

        }
        if (pHand == InteractionHand.MAIN_HAND) {
            BlockPos targetPos = pState.getValue(HALF) == DoubleBlockHalf.UPPER ? pPos.below() : pPos;
            BlockEntity blockEntity = pLevel.getBlockEntity(targetPos);
            if (blockEntity instanceof SurgeryChamberBlockEntity chamberEntity) {
                chamberEntity.toggleDoor();
                return InteractionResult.CONSUME;

            }
        }
        return InteractionResult.PASS;

    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1
                && level.getBlockState(blockpos.above()).canBeReplaced(pContext)) {
            return this.defaultBlockState()
                    .setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(OPEN, true);

        }
        return null;

    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pBlockEntityType == ModBlockEntities.SURGERY_CHAMBER.get()) {
            return (lvl, pos, st, be) -> SurgeryChamberBlockEntity.tick(lvl, pos, st, (SurgeryChamberBlockEntity) be);

        }
        return null;

    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(OPEN, true), 3);

    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide) {
            if (pPlayer.isCreative()) {
                preventCreativeDropFromBottomPart(pLevel, pPos, pState, pPlayer);
            }
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf half = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            if (!pFacingState.is(this)) {
                return Blocks.AIR.defaultBlockState();

            }
        }
        if (half == DoubleBlockHalf.LOWER && pFacing == Direction.DOWN && !pState.canSurvive(pLevel, pCurrentPos)) {
            return Blocks.AIR.defaultBlockState();

        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);

    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        DoubleBlockHalf half = pState.getValue(HALF);
        boolean isOpen = pState.getValue(OPEN);
        if (isOpen) {
            return half == DoubleBlockHalf.LOWER ? LOWER_SHAPES_OPEN.getOrDefault(facing, Shapes.block()) : UPPER_SHAPES_OPEN.getOrDefault(facing, Shapes.block());

        } else {
            return half == DoubleBlockHalf.LOWER ? LOWER_SHAPES_CLOSED.getOrDefault(facing, Shapes.block()) : UPPER_SHAPES_CLOSED.getOrDefault(facing, Shapes.block());

        }
    }
}