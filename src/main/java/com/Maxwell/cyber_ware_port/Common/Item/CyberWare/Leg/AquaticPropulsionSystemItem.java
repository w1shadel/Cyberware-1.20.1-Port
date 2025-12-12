package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

public class AquaticPropulsionSystemItem extends CyberwareItem {
    public AquaticPropulsionSystemItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BOOTS)
                .maxInstall(1)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT)
                .energy(1, 0, 0, StackingRule.LINEAR));
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer.isInWater()) {
            wearer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                int cost = 1;
                if (data.extractEnergy(cost, false) == cost) {
                    boolean isMoving = wearer.getDeltaMovement().lengthSqr() > 0.001 || Math.abs(wearer.xxa) > 0 || Math.abs(wearer.zza) > 0;
                    if (isMoving) {
                        Vec3 look = wearer.getLookAngle();
                        Vec3 motion = wearer.getDeltaMovement();
                        double speed = 0.15;
                        wearer.setDeltaMovement(motion.add(look.x * speed, look.y * speed, look.z * speed));
                    }
                }
            });
        }
    }

    @Override
    public int getEnergyConsumption(ItemStack stack) {
        return 0;
    }
}