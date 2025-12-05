package com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber;

import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SurgeryChamberBlockEntity extends BlockEntity {

    public float animationProgress = 0;
    public float prevAnimationProgress = 0;

    public SurgeryChamberBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SURGERY_CHAMBER.get(), pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SurgeryChamberBlockEntity entity) {

        entity.prevAnimationProgress = entity.animationProgress;

        boolean isOpen = state.getValue(SurgeryChamberBlock.OPEN);
        float target = isOpen ? 1.0F : 0.0F;

        float speed = 0.04F;
        if (entity.animationProgress < target) {
            entity.animationProgress = Math.min(entity.animationProgress + speed, target);
        } else if (entity.animationProgress > target) {
            entity.animationProgress = Math.max(entity.animationProgress - speed, target);
        }
    }

    public boolean isOpen() {
        if (this.level == null) return true;
        return this.getBlockState().getValue(SurgeryChamberBlock.OPEN);
    }

    public void setDoorState(boolean open) {
        if (this.level == null || this.level.isClientSide) return;

        BlockState currentState = this.getBlockState();

        if (currentState.getValue(SurgeryChamberBlock.OPEN) != open) {

            this.level.setBlock(this.worldPosition, currentState.setValue(SurgeryChamberBlock.OPEN, open), 3);

            BlockPos abovePos = this.worldPosition.above();
            BlockState aboveState = this.level.getBlockState(abovePos);
            if (aboveState.getBlock() instanceof SurgeryChamberBlock) {
                this.level.setBlock(abovePos, aboveState.setValue(SurgeryChamberBlock.OPEN, open), 3);
            }
        }
    }

    public void toggleDoor() {
        setDoorState(!isOpen());
    }
}