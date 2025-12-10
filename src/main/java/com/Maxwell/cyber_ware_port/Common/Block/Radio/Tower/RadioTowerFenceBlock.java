package com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower;import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;public class RadioTowerFenceBlock extends FenceBlock {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    private static final int MAX_SEARCH_HEIGHT = 10;

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
         return true;

    }
    public RadioTowerFenceBlock(Properties pProperties) {
        super(pProperties);

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, Boolean.valueOf(false))
                .setValue(EAST, Boolean.valueOf(false))
                .setValue(SOUTH, Boolean.valueOf(false))
                .setValue(WEST, Boolean.valueOf(false))
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(FORMED, Boolean.valueOf(false))
        );

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);

        pBuilder.add(FORMED);

    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {

        if (!pLevel.isClientSide && pState.getValue(FORMED) && !pState.is(pNewState.getBlock())) {

            BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();for (int y = 1;
 y <= MAX_SEARCH_HEIGHT;
 y++) {
                for (int x = -1;
 x <= 1;
 x++) {
                    for (int z = -1;
 z <= 1;
 z++) {
                        searchPos.set(pPos.getX() + x, pPos.getY() + y, pPos.getZ() + z);BlockEntity be = pLevel.getBlockEntity(searchPos);

                        if (be instanceof RadioTowerCoreBlockEntity core) {

                            core.deformStructure();}
                    }
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);

    }
}