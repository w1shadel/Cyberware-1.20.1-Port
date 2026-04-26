package com.maxwell.cyber_ware_port.common.block.charger;

import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.List;

public class ChargerBlockEntity extends BlockEntity {
    private final CustomEnergyHandler energyStorage = new CustomEnergyHandler(1000000);
    private boolean isDrainMode = false;

    public ChargerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CHARGER.get(), pPos, pBlockState);
    }

    private void handlePlayerEnergyTransfer(Level level, BlockPos pos) {
        AABB area = new AABB(pos).inflate(0.2, 1.0, 0.2);
        List<Player> players = level.getEntitiesOfClass(Player.class, area);
        for (Player player : players) {
            try (Transaction tx = Transaction.openRoot()) {
                EnergyHandler playerEnergy = Capabilities.Energy.ENTITY.getCapability(player, null);
                if (playerEnergy != null) {
                    int maxTransfer = 10000;
                    if (isDrainMode) {
                        int extracted = playerEnergy.extract(maxTransfer, tx);
                        this.energyStorage.insert(extracted, tx);
                    } else {
                        int available = (int) this.energyStorage.getAmountAsLong();
                        int toSend = Math.min(available, maxTransfer);
                        int accepted = playerEnergy.insert(toSend, tx);
                        this.energyStorage.extract(accepted, tx);
                    }
                }
                tx.commit();
            }
        }
    }

    public void toggleMode(Player player) {
        this.isDrainMode = !this.isDrainMode;
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        player.sendSystemMessage(Component.literal("Charger Mode: " + (isDrainMode ? "DRAIN" : "CHARGE")));
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("energy", this.energyStorage.getAmountAsLong());
        output.putBoolean("isDrainMode", isDrainMode);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energyStorage.setEnergyDirectly(input.getLongOr("energy", 0));
        this.isDrainMode = input.getBooleanOr("isDrainMode", false);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        handlePlayerEnergyTransfer(level, pos);
    }

    public EnergyHandler getEnergyStorage() {
        return energyStorage;
    }

    private class CustomEnergyHandler extends SnapshotJournal<Long> implements EnergyHandler {
        private final long capacity;
        private long energy = 0;

        public CustomEnergyHandler(long capacity) {
            this.capacity = capacity;
        }

        public void setEnergyDirectly(long val) {
            this.energy = val;
        }

        @Override
        public long getAmountAsLong() {
            return energy;
        }

        @Override
        public long getCapacityAsLong() {
            return capacity;
        }

        @Override
        public int insert(int amount, TransactionContext tx) {
            if (isDrainMode || amount <= 0) return 0;
            int inserted = (int) Math.min(amount, capacity - energy);
            if (inserted > 0) {
                this.updateSnapshots(tx);
                this.energy += inserted;
                ChargerBlockEntity.this.setChanged();
            }
            return inserted;
        }

        @Override
        public int extract(int amount, TransactionContext tx) {
            if (!isDrainMode || amount <= 0) return 0;
            int extracted = (int) Math.min(amount, energy);
            if (extracted > 0) {
                this.updateSnapshots(tx);
                this.energy -= extracted;
                ChargerBlockEntity.this.setChanged();
            }
            return extracted;
        }

        @Override
        protected Long createSnapshot() {
            return energy;
        }

        @Override
        protected void revertToSnapshot(Long snapshot) {
            this.energy = snapshot;
        }
    }
}