package com.Maxwell.cyber_ware_port.Common.Capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CyberwareCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<CyberwareUserData> CYBERWARE_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {
            });
    private CyberwareUserData backend = null;

    private final LazyOptional<CyberwareUserData> optional = LazyOptional.of(this::createBackend);
    private final LazyOptional<IEnergyStorage> energyOptional = optional.cast();

    private CyberwareUserData createBackend() {
        if (this.backend == null) {
            this.backend = new CyberwareUserData();

        }
        return this.backend;

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CYBERWARE_CAPABILITY) {
            return optional.cast();

        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyOptional.cast();

        }
        return LazyOptional.empty();

    }

    @Override
    public CompoundTag serializeNBT() {
        return createBackend().serializeNBT();

    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createBackend().deserializeNBT(nbt);

    }
}