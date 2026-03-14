package com.maxwell.cyber_ware_port.common.block.charger;

import com.maxwell.cyber_ware_port.api.event.CyberwareEvents;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChargerBlockEntity extends BlockEntity {
    private final CustomEnergyStorage energyStorage = new CustomEnergyStorage(1000000, 10000);
    private boolean isDrainMode = false;

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
        AABB area = new AABB(pos).inflate(0.2, 1.0, 0.2);
        List<Player> players = level.getEntitiesOfClass(Player.class, area);
        for (Player player : players) {
            CyberwareEvents.Recharge event = new CyberwareEvents.Recharge(player, this, isDrainMode);
            NeoForge.EVENT_BUS.post(event);
            if (((ICancellableEvent) event).isCanceled()) {
                continue;
            }

            IEnergyStorage userData = player.getCapability(Capabilities.EnergyStorage.ENTITY, null);
            if (userData != null) {
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
            }
        }
    }

    private void distributeEnergy(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (this.energyStorage.getEnergyStored() <= 0) break;
            BlockPos targetPos = pos.relative(direction);
            IEnergyStorage targetStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, direction.getOpposite());
            if (targetStorage != null && targetStorage.canReceive()) {
                int extracted = this.energyStorage.extractEnergy(10000, true);
                int received = targetStorage.receiveEnergy(extracted, false);
                this.energyStorage.extractEnergy(received, false);
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

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("Energy", energyStorage.serializeNBT(pRegistries));
        pTag.putBoolean("IsDrainMode", isDrainMode);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("Energy")) {
            energyStorage.deserializeNBT(pRegistries, pTag.get("Energy"));
        }
        this.isDrainMode = pTag.getBoolean("IsDrainMode");
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