package com.maxwell.cyber_ware_port.common.block.component_box;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentBoxBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<ComponentBoxBlock> CODEC = simpleCodec(ComponentBoxBlock::new);
    private static final VoxelShape SHAPE_NS = Block.box(1, 0, 4, 15, 11, 12);
    private static final VoxelShape SHAPE_EW = Block.box(4, 0, 1, 12, 11, 15);

    public ComponentBoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_EW : SHAPE_NS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
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
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            if (pLevel.getBlockEntity(pPos) instanceof ComponentBoxBlockEntity boxEntity) {
                pPlayer.openMenu(boxEntity, pPos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (pLevel.getBlockEntity(pPos) instanceof ComponentBoxBlockEntity box) {
            if (pStack.has(DataComponents.CUSTOM_NAME)) {
                box.setCustomName(pStack.getHoverName());
            }
            CustomData customData = pStack.get(DataComponents.CUSTOM_DATA);
            if (customData != null && customData.contains("Inventory")) {
                CompoundTag invTag = customData.copyTag().getCompoundOrEmpty("Inventory");
                box.getItemHandler().deserialize(TagValueInput.create(
                        ProblemReporter.DISCARDING,
                        pLevel.registryAccess(),
                        invTag
                ));
                box.setChanged();
            }
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Player pPlayer) {
        if (!pLevel.isClientSide() && !pPlayer.isCreative()) {
            if (pLevel.getBlockEntity(pPos) instanceof ComponentBoxBlockEntity box) {
                ItemStack itemStack = new ItemStack(this);
                TagValueOutput output = TagValueOutput.createWithContext(
                        ProblemReporter.DISCARDING,
                        pLevel.registryAccess()
                );
                box.getItemHandler().serialize(output);
                CompoundTag inventoryTag = output.buildResult();
                itemStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data ->
                        data.update(tag -> tag.put("Inventory", inventoryTag))
                );
                if (box.hasCustomName()) {
                    itemStack.set(DataComponents.CUSTOM_NAME, box.getDisplayName());
                }
                ItemEntity itementity = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, itemStack);
                pLevel.addFreshEntity(itementity);
            }
        }
        return super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }
}