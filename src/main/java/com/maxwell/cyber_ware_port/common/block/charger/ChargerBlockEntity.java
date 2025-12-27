package com.maxwell.cyber_ware_port.common.block.charger;

import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChargerBlockEntity extends BlockEntity {

    private boolean isDrainMode = false;

    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(1000000, 10000);

    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    public ChargerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CHARGER.get(), pPos, pBlockState);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        handlePlayerEnergyTransfer(level, pos);
        if (isDrainMode && energyStorage.getEnergyStored() > 0) {
            distributeEnergy(level, pos);
        }
    }

    private void handlePlayerEnergyTransfer(Level level, BlockPos pos) {
        AABB area = new AABB(pos).move(0, 0, 0).expandTowards(0, 0.5, 0);
        List<Player> players = level.getEntitiesOfClass(Player.class, area);
        for (Player player : players) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
                int maxTransfer = 10000;
                if (isDrainMode) {
                    int extracted = userData.extractEnergy(maxTransfer, true);
                    int space = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
                    int toReceive = Math.min(extracted, space);
                    if (toReceive > 0) {
                        userData.extractEnergy(toReceive, false);
                        modifyEnergy(toReceive);
                    }

                } else {
                    int available = energyStorage.getEnergyStored();
                    int received = userData.receiveEnergy(Math.min(available, maxTransfer), true);
                    if (received > 0) {
                        modifyEnergy(-received);
                        userData.receiveEnergy(received, false);
                    }
                }
            });
        }
    }

    private void distributeEnergy(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (this.energyStorage.getEnergyStored() <= 0) break;
            BlockEntity neighborBe = level.getBlockEntity(pos.relative(direction));
            if (neighborBe != null) {
                neighborBe.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(targetStorage -> {
                    if (targetStorage.canReceive()) {
                        int extracted = this.energyStorage.extractEnergy(10000, true);
                        int received = targetStorage.receiveEnergy(extracted, false);
                        this.energyStorage.extractEnergy(received, false);
                    }
                });
            }
        }
    }

    private void modifyEnergy(int amount) {
        int current = energyStorage.getEnergyStored();
        int capacity = energyStorage.getMaxEnergyStored();
        int next = Math.max(0, Math.min(current + amount, capacity));
        energyStorage.setEnergyInternal(next);
        setChanged();
    }

    public void toggleMode(Player player) {
        this.isDrainMode = !this.isDrainMode;
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
        if (this.isDrainMode) {
            player.sendSystemMessage(Component.literal("Charger Mode: DRAIN (Player -> Network)"));
        } else {
            player.sendSystemMessage(Component.literal("Charger Mode: CHARGE (Network -> Player)"));
        }
        setChanged();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", energyStorage.serializeNBT());
        pTag.putBoolean("IsDrainMode", isDrainMode);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("Energy")) {
            energyStorage.deserializeNBT(pTag.get("Energy"));
        }
        this.isDrainMode = pTag.getBoolean("IsDrainMode");
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
    }

    private class CustomEnergyStorage extends EnergyStorage {
        public CustomEnergyStorage(int capacity, int maxTransfer) {
            super(capacity, maxTransfer);
        }

        public void setEnergyInternal(int energy) {
            this.energy = energy;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (isDrainMode) return 0;
            int ret = super.receiveEnergy(maxReceive, simulate);
            if (ret > 0 && !simulate) ChargerBlockEntity.this.setChanged();
            return ret;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!isDrainMode) return 0;
            int ret = super.extractEnergy(maxExtract, simulate);
            if (ret > 0 && !simulate) ChargerBlockEntity.this.setChanged();
            return ret;
        }

        @Override
        public boolean canReceive() {
            return !isDrainMode;
        }

        @Override
        public boolean canExtract() {
            return isDrainMode;
        }
    }
}