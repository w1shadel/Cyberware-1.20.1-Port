package com.maxwell.cyber_ware_port.common.block.surgerychamber;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    public static final MapCodec<SurgeryChamberBlock> CODEC = simpleCodec(SurgeryChamberBlock::new);
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

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction toDir) {
        if (toDir == Direction.NORTH) return shape;
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = switch (toDir) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        for (int i = 0; i < times; i++) {
            VoxelShape current = buffer[0];
            VoxelShape rotated = Shapes.empty();
            current.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (pLevel.isClientSide) return InteractionResult.SUCCESS;
        BlockPos targetPos = pState.getValue(HALF) == DoubleBlockHalf.UPPER ? pPos.below() : pPos;
        BlockEntity blockEntity = pLevel.getBlockEntity(targetPos);
        if (blockEntity instanceof SurgeryChamberBlockEntity chamberEntity) {
            chamberEntity.toggleDoor();
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide && pPlayer.isCreative()) {
            DoubleBlockHalf half = pState.getValue(HALF);
            if (half == DoubleBlockHalf.UPPER) {
                BlockPos blockpos = pPos.below();
                BlockState blockstate = pLevel.getBlockState(blockpos);
                if (blockstate.is(pState.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    pLevel.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
        return pState;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        DoubleBlockHalf half = pState.getValue(HALF);
        if (pFacing.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
            return pFacingState.is(this) ? pState : Blocks.AIR.defaultBlockState();
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
        return pState.getValue(OPEN) ?
                (half == DoubleBlockHalf.LOWER ? LOWER_SHAPES_OPEN.get(facing) : UPPER_SHAPES_OPEN.get(facing)) :
                (half == DoubleBlockHalf.LOWER ? LOWER_SHAPES_CLOSED.get(facing) : UPPER_SHAPES_CLOSED.get(facing));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HALF, OPEN);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? new SurgeryChamberBlockEntity(pPos, pState) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(pContext)) {
            return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModBlockEntities.SURGERY_CHAMBER.get() ? (lvl, pos, st, be) -> SurgeryChamberBlockEntity.tick(lvl, pos, st, (SurgeryChamberBlockEntity) be) : null;
    }
}