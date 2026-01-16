package com.maxwell.cyber_ware_port.common.block.radio.tower;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RadioTowerCoreBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(6.5, 0, 6.5, 9.5, 16, 9.5),
            Block.box(3.5, 8, 7.2, 12.5, 14, 8.7),
            Block.box(3.1, 7, 8.7, 5.1, 15, 10.7),
            Block.box(11.1, 7, 8.7, 13.1, 15, 10.7),
            Block.box(3.1, 7, 5.2, 5.1, 15, 7.2),
            Block.box(11.1, 7, 5.2, 13.1, 15, 7.2)
    );
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(6.5, 0, 6.5, 9.5, 16, 9.5),
            Block.box(7.3, 8, 3.5, 8.8, 14, 12.5),
            Block.box(5.3, 7, 3.1, 7.3, 15, 5.1),
            Block.box(5.3, 7, 11.1, 7.3, 15, 13.1),
            Block.box(8.8, 7, 3.1, 10.8, 15, 5.1),
            Block.box(8.8, 7, 11.1, 10.8, 15, 13.1)
    );
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(6.5, 0, 6.5, 9.5, 16, 9.5),
            Block.box(3.5, 8, 7.3, 12.5, 14, 8.8),
            Block.box(10.9, 7, 5.3, 12.9, 15, 7.3),
            Block.box(2.9, 7, 5.3, 4.9, 15, 7.3),
            Block.box(10.9, 7, 8.8, 12.9, 15, 10.8),
            Block.box(2.9, 7, 8.8, 4.9, 15, 10.8)
    );
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(6.5, 0, 6.5, 9.5, 16, 9.5),
            Block.box(7.2, 8, 3.5, 8.7, 14, 12.5),
            Block.box(8.7, 7, 10.9, 10.7, 15, 12.9),
            Block.box(8.7, 7, 2.9, 10.7, 15, 4.9),
            Block.box(5.2, 7, 10.9, 7.2, 15, 12.9),
            Block.box(5.2, 7, 2.9, 7.2, 15, 4.9)
    );
    public static final Map<ResourceKey<Level>, Long> LAST_TOWER_ACTIVE_TIME = new ConcurrentHashMap<>();

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    public RadioTowerCoreBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FORMED, false).setValue(FACING, Direction.NORTH));
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FORMED);
        pBuilder.add(FACING);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        if (!pLevel.isClientSide) {
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if (be instanceof RadioTowerCoreBlockEntity core) {
                core.tryToFormStructure();
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            if (!pLevel.isClientSide && pState.getValue(FORMED)) {
                BlockEntity be = pLevel.getBlockEntity(pPos);
                if (be instanceof RadioTowerCoreBlockEntity core) {
                    core.deformFencesOnly();
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RadioTowerCoreBlockEntity(pPos, pState);
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) return null;
        if (pBlockEntityType == ModBlockEntities.RADIO_TOWER_CORE.get()) {
            return (lvl, pos, st, be) -> {
                RadioTowerCoreBlockEntity.serverTick(lvl, pos, st, (RadioTowerCoreBlockEntity) be);
                if (st.getValue(FORMED) && lvl.getGameTime() % 20 == 0) {
                    LAST_TOWER_ACTIVE_TIME.put(lvl.dimension(), lvl.getGameTime());
                }
            };
        }
        return null;
    }
}