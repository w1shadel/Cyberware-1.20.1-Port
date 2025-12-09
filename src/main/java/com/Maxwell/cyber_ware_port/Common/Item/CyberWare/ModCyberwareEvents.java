package com.Maxwell.cyber_ware_port.Common.Item.CyberWare;

import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import com.Maxwell.cyber_ware_port.Common.Network.A_PacketHandler;
import com.Maxwell.cyber_ware_port.Common.Network.DoubleJumpPacket;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod.EventBusSubscriber(modid = CyberWare.MODID)
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
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event) {

        if (event.getItem().getItem() instanceof BowItem) {

            event.getEntity().getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
                if (userData.isCyberwareInstalled(ModItems.RAPID_FIRE_FLYWHEEL.get())) {event.setDuration(event.getDuration() - 1);
                }
            });
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
        if (event.getEntity() instanceof Player player) {

            if (event.getEffectInstance().getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {

                    if (data.isCyberwareInstalled(ModItems.LIVER_FILTER.get())) {
                        int cost = 50;
                        if (data.getEnergyStored() >= cost) {
                            data.extractEnergy(cost, false);

                            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                        }
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
    }
    @SubscribeEvent
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

    private static final String NBT_CROUCH_TIME = "cyberware_crouch_time";
    private static final String NBT_DOUBLE_JUMPED = "cyberware_double_jumped";@SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (data.isCyberwareInstalled(ModItems.LINEAR_ACTUATORS.get())) {
                CompoundTag tag = player.getPersistentData();

                if (player.isCrouching() && player.onGround()) {
                    int time = tag.getInt(NBT_CROUCH_TIME);
                    tag.putInt(NBT_CROUCH_TIME, time + 1);

                } else {

                    if (!player.isCrouching()) {
                        tag.putInt(NBT_CROUCH_TIME, 0);
                    }
                }

                if (player.horizontalCollision && !player.onGround()) {tag.putBoolean(NBT_DOUBLE_JUMPED, false);

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
        });
    }@SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (data.isCyberwareInstalled(ModItems.LINEAR_ACTUATORS.get())) {
                CompoundTag tag = player.getPersistentData();
                int time = tag.getInt(NBT_CROUCH_TIME);

                if (time >= 60) {
                    Vec3 look = player.getLookAngle();double forwardSpeed = 1.5; 
                    double upSpeed = 1.25;     

                    player.setDeltaMovement(
                            look.x * forwardSpeed,
                            upSpeed,
                            look.z * forwardSpeed
                    );

                    tag.putInt(NBT_CROUCH_TIME, 0);
                }
            }
        });
    }
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (event.getKey() == mc.options.keyJump.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
            Player player = mc.player;

            if (!player.onGround() && !player.isCreative() && !player.isSpectator()) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    if (data.isCyberwareInstalled(ModItems.LINEAR_ACTUATORS.get())) {

                        if (!player.getPersistentData().getBoolean(NBT_DOUBLE_JUMPED)) {

                            A_PacketHandler.INSTANCE.sendToServer(new DoubleJumpPacket());

                            performDoubleJump(player);
                        }
                    }
                });
            }
        }
    }

    public static void performDoubleJump(Player player) {
        Vec3 currentMotion = player.getDeltaMovement();
        Vec3 look = player.getLookAngle();double boost = 0.5; 
        double jumpHeight = 0.6; 

        player.setDeltaMovement(
                currentMotion.x + (look.x * boost),
                jumpHeight,
                currentMotion.z + (look.z * boost)
        );

        player.fallDistance = 0;

        player.getPersistentData().putBoolean(NBT_DOUBLE_JUMPED, true);
    }
}