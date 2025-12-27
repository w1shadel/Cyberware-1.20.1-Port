package com.maxwell.cyber_ware_port.common.block.surgerychamber;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

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
        float speed = 0.1F;
        if (entity.animationProgress < target) {
            entity.animationProgress = Math.min(entity.animationProgress + speed, target);

        } else if (entity.animationProgress > target) {
            entity.animationProgress = Math.max(entity.animationProgress - speed, target);

        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level != null) {
            boolean isOpen = this.getBlockState().getValue(SurgeryChamberBlock.OPEN);
            this.animationProgress = isOpen ? 1.0F : 0.0F;
            this.prevAnimationProgress = this.animationProgress;

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
            setChanged();

        }
    }

    public void toggleDoor() {
        setDoorState(!isOpen());

    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).expandTowards(0, 2.0, 0);

    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putFloat("AnimationProgress", this.animationProgress);

    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("AnimationProgress")) {
            this.animationProgress = pTag.getFloat("AnimationProgress");
            this.prevAnimationProgress = this.animationProgress;

        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);

    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();

    }
}