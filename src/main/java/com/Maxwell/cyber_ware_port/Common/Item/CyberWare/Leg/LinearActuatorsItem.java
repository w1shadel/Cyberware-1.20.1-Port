package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LinearActuatorsItem extends CyberwareItem {
    private static final String NBT_CROUCH_TIME = "cyberware_crouch_time";
    private static final String NBT_DOUBLE_JUMPED = "cyberware_double_jumped";
    private static final String NBT_JUMP_READY = "cyberware_jump_ready";

    public LinearActuatorsItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_LEGS)
                .requires(ModItems.CYBER_LEG_RIGHT, ModItems.CYBER_LEG_LEFT)
                .maxInstall(1)
                .eventCost(0)
                .energy(0, 0, 0, StackingRule.LINEAR)
        );
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
            if (time < 60) {
                time++;
                tag.putInt(NBT_CROUCH_TIME, time);
            }
            if (time >= 60) {
                tag.putBoolean(NBT_JUMP_READY, true);
            }
            if (player.level().isClientSide()) {
                if (time == 60) {
                    player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.7f, 1.5f);
                    for (int i = 0; i < 15; i++) {
                        player.level().addParticle(ParticleTypes.ENCHANT,
                                player.getX() + (player.getRandom().nextDouble() - 0.5) * 0.6,
                                player.getY() + 0.1,
                                player.getZ() + (player.getRandom().nextDouble() - 0.5) * 0.6,
                                0.0D, 0.1D, 0.0D);
                    }
                } else if (time > 0 && time < 60 && time % 20 == 0) {
                    float pitch = 1.0f + (time / 60.0f) * 0.5f;
                    player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 0.5f, pitch);
                    for (int i = 0; i < 3; i++) {
                        player.level().addParticle(ParticleTypes.CRIT,
                                player.getX() + (player.getRandom().nextDouble() - 0.5) * 0.5,
                                player.getY() + 0.1,
                                player.getZ() + (player.getRandom().nextDouble() - 0.5) * 0.5,
                                0.0D, 0.05D, 0.0D);
                    }
                }
            }


        } else if (player.onGround()) {
            tag.putInt(NBT_CROUCH_TIME, 0);
            tag.putBoolean(NBT_JUMP_READY, false);
        }
        if (player.horizontalCollision && !player.onGround()) {
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
        if (wearer.level().isClientSide()) return;
        if (!(wearer instanceof ServerPlayer player)) return;
        CompoundTag tag = player.getPersistentData();
        if (tag.getBoolean(NBT_JUMP_READY)) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                if (tryConsumeEventEnergy(data, stack)) {
                    Vec3 look = wearer.getLookAngle();
                    double forwardSpeed = 1.5;
                    double upSpeed = 1.25;
                    wearer.setDeltaMovement(look.x * forwardSpeed, upSpeed, look.z * forwardSpeed);
                    player.hurtMarked = true;
                    tag.putInt(NBT_CROUCH_TIME, 0);
                    tag.putBoolean(NBT_JUMP_READY, false);
                }
            });
        }
    }
}