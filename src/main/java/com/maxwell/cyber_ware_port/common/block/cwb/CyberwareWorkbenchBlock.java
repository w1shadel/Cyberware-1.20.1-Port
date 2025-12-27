package com.maxwell.cyber_ware_port.common.block.cwb;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class CyberwareWorkbenchBlock extends HorizontalDirectionalBlock implements EntityBlock {
    private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            BASE,
            Block.box(4, 16, 12, 12, 32, 16),
            Block.box(4, 24, 3, 12, 32, 12)
    );
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            BASE,
            Block.box(4, 16, 0, 12, 32, 4),
            Block.box(4, 24, 4, 12, 32, 13)
    );
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            BASE,
            Block.box(12, 16, 4, 16, 32, 12),
            Block.box(3, 24, 4, 12, 32, 12)
    );
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            BASE,
            Block.box(0, 16, 4, 4, 32, 12),
            Block.box(4, 24, 4, 13, 32, 12)
    );

    public CyberwareWorkbenchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH));

    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);

    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return switch (direction) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_NORTH;

        };

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CyberwareWorkbenchBlockEntity(pPos, pState);

    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof CyberwareWorkbenchBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, (CyberwareWorkbenchBlockEntity) entity, pPos);

            } else {
                throw new IllegalStateException("Our Container provider is missing!");

            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());

    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pBlockEntityType == ModBlockEntities.CYBERWARE_WORKBENCH.get()) {
            return (lvl, pos, st, be) -> CyberwareWorkbenchBlockEntity.tick(lvl, pos, st, (CyberwareWorkbenchBlockEntity) be);

        }
        return null;

    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CyberwareWorkbenchBlockEntity) {
                ((CyberwareWorkbenchBlockEntity) blockEntity).drops();

            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);

    }
}