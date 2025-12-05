package com.Maxwell.cyber_ware_port.Common.Item.CyberWare;import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

@Mod.EventBusSubscriber
public class ModCyberwareEvents {@SubscribeEvent
    public static void onEnderTeleport(EntityTeleportEvent.EnderEntity event) {
        BlockPos targetPos = new BlockPos((int)event.getTargetX(), (int)event.getTargetY(), (int)event.getTargetZ());
        List<Player> players = event.getEntity().level().getEntitiesOfClass(Player.class, new AABB(targetPos).inflate(16));

        for (Player player : players) {
            boolean jammed = player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).map(data -> {
                if (data.isCyberwareInstalled(ModItems.ENDER_JAMMER.get())) {

                    ItemStack stack = ItemStack.EMPTY;
                    ItemStackHandler handler = data.getInstalledCyberware();
                    for(int i=0; i<handler.getSlots(); i++) {
                        if (handler.getStackInSlot(i).getItem() == ModItems.ENDER_JAMMER.get()) {
                            stack = handler.getStackInSlot(i);
                            break;
                        }
                    }

                    if (!stack.isEmpty() && ((ICyberware)stack.getItem()).isActive(stack)) {
                        return data.extractEnergy(200, false) == 200;
                    }
                }
                return false;
            }).orElse(false);

            if (jammed) {
                event.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {

                boolean isLightlyArmored = player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() &&
                        player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();

                if (isLightlyArmored) {
                    if (data.isCyberwareInstalled(ModItems.THREAT_MATRIX.get()) && data.getEnergyStored() > 0) {if (player.getRandom().nextFloat() < 0.3f) {
                            if (data.extractEnergy(500, false) == 500) {
                                event.setCanceled(true);
                                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 2.0f);
                                return; 
                            }
                        }
                    }
                }

                if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                    if (data.isCyberwareInstalled(ModItems.WIRED_REFLEXES.get())) {player.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, attacker.getEyePosition());
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player.isCreative()) return; 
        if (!player.level().isClientSide) {

            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                if (data.isCyberwareInstalled(ModItems.NEURAL_CONTEXTUALIZER.get())) {
                    net.minecraft.world.level.block.state.BlockState state = player.level().getBlockState(event.getPos());

                    ItemStack currentStack = player.getMainHandItem();
                    if (currentStack.getDestroySpeed(state) > 1.0f) {
                        return;
                    }

                    int bestSlot = -1;
                    float bestSpeed = 1.0f; 

                    for (int i = 0; i < 9; i++) {
                        ItemStack stack = player.getInventory().getItem(i);
                        float speed = stack.getDestroySpeed(state);

                        if (speed > bestSpeed) {
                            bestSpeed = speed;
                            bestSlot = i;
                        }
                    }

                    if (bestSlot != -1 && bestSlot != player.getInventory().selected) {
                        player.getInventory().selected = bestSlot;

                        ((ServerPlayer)player).connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(bestSlot));
                    }
                }
            });
        }
    }@SubscribeEvent
    public static void onPotionAdded(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getEffectInstance().getEffect() == net.minecraft.world.effect.MobEffects.WEAKNESS) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    if (data.isCyberwareInstalled(ModItems.CARDIOMECHANIC_PUMP.get())) {
                        event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {

                if (data.isCyberwareInstalled(ModItems.INTERNAL_DEFIBRILLATOR.get())) {
                    int cost = 500; 
                    if (data.extractEnergy(cost, true) == cost) {
                        data.extractEnergy(cost, false);
                        event.setCanceled(true);
                        player.setHealth(player.getMaxHealth() * 0.5f);
                        player.level().playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

                        return;
                    }
                }

                if (data.isCyberwareInstalled(ModItems.CORTICAL_STACK.get())) {
                    int totalXp = getTotalXp(player);
                    if (totalXp > 0) {
                        ItemStack capsule = new ItemStack(ModItems.EXP_CAPSULE.get());
                        capsule.getOrCreateTag().putInt("xp", totalXp);
                        player.drop(capsule, true);

                        player.totalExperience = 0;
                        player.experienceLevel = 0;
                        player.experienceProgress = 0;
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(oldData -> {
                if (oldData.isCyberwareInstalled(ModItems.CONSCIOUSNESS_TRANSMITTER.get())) {

                }
            });
        }
    }

    private static int getTotalXp(Player player) {

        return (int)(player.experienceLevel * 7 + player.experienceProgress * player.getXpNeededForNextLevel());
    }@SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                if (data.isCyberwareInstalled(ModItems.LINEAR_ACTUATORS.get())) {
                    int cost = 20;
                    if (data.extractEnergy(cost, true) == cost) {
                        data.extractEnergy(cost, false);

                        if (player.isCrouching()) {

                            player.setDeltaMovement(player.getDeltaMovement().add(0, 0.6, 0));
                        } else {

                            player.setDeltaMovement(player.getDeltaMovement().add(0, 0.2, 0));
                        }
                    }
                }
            });
        }
    }@SubscribeEvent
    public static void onHarvestCheck(net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck event) {
        if (event.canHarvest()) return;

        Player player = event.getEntity();
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (data.isCyberwareInstalled(ModItems.REINFORCED_FIST.get())) {

                if (player.getMainHandItem().isEmpty()) {event.setCanHarvest(true);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onBreakSpeed(net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (player.getMainHandItem().isEmpty()) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                if (data.isCyberwareInstalled(ModItems.REINFORCED_FIST.get())) {

                    event.setNewSpeed(4.0f);
                }
            });
        }
    }
}