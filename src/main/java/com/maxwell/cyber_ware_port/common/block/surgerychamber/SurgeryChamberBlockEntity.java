package com.maxwell.cyber_ware_port.common.block.surgerychamber;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (entity.animationProgress < target) {
            entity.animationProgress = Math.min(entity.animationProgress + 0.1F, target);
        } else if (entity.animationProgress > target) {
            entity.animationProgress = Math.max(entity.animationProgress - 0.1F, target);
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
        return this.level != null && this.getBlockState().getValue(SurgeryChamberBlock.OPEN);
    }

    public void setDoorState(boolean open) {
        if (this.level == null || this.level.isClientSide) return;
        BlockState currentState = this.getBlockState();
        if (currentState.getValue(SurgeryChamberBlock.OPEN) != open) {
            this.level.setBlock(this.worldPosition, currentState.setValue(SurgeryChamberBlock.OPEN, open), 3);
            this.level.playSound(null, this.worldPosition, open ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 0.5F, 1.2F);
            this.level.playSound(null, this.worldPosition, open ? SoundEvents.PISTON_EXTEND : SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, 1.2F);
            BlockPos abovePos = this.worldPosition.above();
            BlockState aboveState = this.level.getBlockState(abovePos);
            if (aboveState.is(currentState.getBlock())) {
                this.level.setBlock(abovePos, aboveState.setValue(SurgeryChamberBlock.OPEN, open), 3);
            }
            setChanged();
        }
    }

    public void toggleDoor() {
        setDoorState(!isOpen());
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putFloat("AnimationProgress", this.animationProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("AnimationProgress")) {
            this.animationProgress = pTag.getFloat("AnimationProgress");
            this.prevAnimationProgress = this.animationProgress;
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}