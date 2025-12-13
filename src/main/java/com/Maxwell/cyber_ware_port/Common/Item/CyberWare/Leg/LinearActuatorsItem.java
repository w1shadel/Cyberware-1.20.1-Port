package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LinearActuatorsItem extends CyberwareItem {
    private static final String NBT_CROUCH_TIME = "cyberware_crouch_time";
    private static final String NBT_DOUBLE_JUMPED = "cyberware_double_jumped";

    public LinearActuatorsItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_LEGS)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT)
                .maxInstall(1));

    }

    public static void performDoubleJump(Player player) {
        Vec3 currentMotion = player.getDeltaMovement();
        Vec3 look = player.getLookAngle();
        double boost = 0.5;
        double jumpHeight = 0.6;
        player.setDeltaMovement(currentMotion.x + (look.x * boost), jumpHeight, currentMotion.z + (look.z * boost));
        player.fallDistance = 0;
        player.getPersistentData().putBoolean(NBT_DOUBLE_JUMPED, true);
    }

    @Override
    public void onPlayerTick(TickEvent.PlayerTickEvent event, ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player)) return;
        CompoundTag tag = player.getPersistentData();
        if (player.isCrouching() && player.onGround()) {
            int time = tag.getInt(NBT_CROUCH_TIME);
            tag.putInt(NBT_CROUCH_TIME, time + 1);
        } else {
            if (!player.isCrouching()) {
                tag.putInt(NBT_CROUCH_TIME, 0);
            }
        }
        if (player.horizontalCollision && !player.onGround()) {
            tag.putBoolean(NBT_DOUBLE_JUMPED, false);
            Vec3 motion = player.getDeltaMovement();
            if (motion.y < 0) {
                player.setDeltaMovement(motion.x, -0.15, motion.z);
                player.fallDistance = 0;
            }
        }
        if (player.onGround()) {
            tag.putBoolean(NBT_DOUBLE_JUMPED, false);
        }
    }

    @Override
    public void onLivingJump(LivingEvent.LivingJumpEvent event, ItemStack stack, LivingEntity wearer) {
        CompoundTag tag = wearer.getPersistentData();
        int time = tag.getInt(NBT_CROUCH_TIME);
        if (time >= 60) {
            Vec3 look = wearer.getLookAngle();
            double forwardSpeed = 1.5;
            double upSpeed = 1.25;
            wearer.setDeltaMovement(look.x * forwardSpeed, upSpeed, look.z * forwardSpeed);
            tag.putInt(NBT_CROUCH_TIME, 0);
        }
    }
}