package com.Maxwell.cyber_ware_port.Common.Block.Component_Box;


import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;

import net.minecraft.nbt.CompoundTag;

import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.InteractionHand;

import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.item.ItemEntity;

import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.context.BlockPlaceContext;

import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.*;

import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.StateDefinition;

import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.phys.shapes.CollisionContext;

import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.Nullable;


public class ComponentBoxBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public ComponentBoxBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));

    }private static final VoxelShape SHAPE_NORTH = Block.box(1, 0, 4, 15, 11, 12);
private static final VoxelShape SHAPE_EAST = Block.box(4, 0, 1, 12, 11, 15);
private static final VoxelShape SHAPE_SOUTH = Block.box(1, 0, 4, 15, 11, 12);
private static final VoxelShape SHAPE_WEST = Block.box(4, 0, 1, 12, 11, 15);


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {

        Direction facing = state.getValue(FACING);


        switch (facing) {
            case EAST:
                return SHAPE_EAST;

            case SOUTH:
                return SHAPE_SOUTH;

            case WEST:
                return SHAPE_WEST;

            case NORTH:
            default: 
                return SHAPE_NORTH;

        }
    }@Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ComponentBoxBlockEntity(pos, state);

    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);

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
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;

    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
            BlockEntity be = pLevel.getBlockEntity(pPos);

            if (be instanceof ComponentBoxBlockEntity boxEntity) {
                NetworkHooks.openScreen(serverPlayer, boxEntity, pPos);

            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide);

    }
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);


        BlockEntity be = pLevel.getBlockEntity(pPos);

        if (be instanceof ComponentBoxBlockEntity box) {

            if (pStack.hasCustomHoverName()) {
                box.setCustomName(pStack.getHoverName());

            }

            CompoundTag stackTag = pStack.getTag();

            if (stackTag != null && stackTag.contains("Inventory")) {
                box.itemHandler.deserializeNBT(stackTag.getCompound("Inventory"));

                box.setChanged();

            }
        }
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (!pLevel.isClientSide && !pPlayer.isCreative()) {
            BlockEntity be = pLevel.getBlockEntity(pPos);

            if (be instanceof ComponentBoxBlockEntity box) {

                ItemStack itemStack = new ItemStack(this.asItem());


                CompoundTag tag = new CompoundTag();

                tag.put("Inventory", box.itemHandler.serializeNBT());

                itemStack.setTag(tag);


                if (box.hasCustomName()) {
                    itemStack.setHoverName(box.getDisplayName());

                }

                ItemEntity itementity = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, itemStack);

                pLevel.addFreshEntity(itementity);

            }
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);

    }
}