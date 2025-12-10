package com.Maxwell.cyber_ware_port.Common.Capability;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.ICyberware;
import com.Maxwell.cyber_ware_port.Config.CyberwareConfig;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = CyberWare.MODID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(CyberWare.MODID, "cyberware_data"), new CyberwareCapabilityProvider());

            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            syncData(serverPlayer);
            serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(cap -> {
                if (!cap.isInitialized()) {
                    cap.fillWithHumanParts();

                }
                cap.syncToClient(serverPlayer);

            });

        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            syncData(serverPlayer);

        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                data.recalculateCapacity(serverPlayer);
                data.syncToClient(serverPlayer);
                syncData(serverPlayer);

            });

        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();
        original.reviveCaps();
        if (event.isWasDeath()) {
            original.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(oldData -> {
                newPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(newData -> {
                    newData.copyFrom(oldData);
                    newData.setRespawnGracePeriod(12000);
                });
            });
        }
        try {
            if (event.isWasDeath()) {
                if (CyberwareConfig.KEEP_CYBERWARE_ON_DEATH.get()) {
                    original.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(oldData -> {
                        newPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(newData -> {
                            newData.copyFrom(oldData);

                        });

                    });

                } else {
                    DamageSource source = original.getLastDamageSource();
                    newPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(newData -> {
                        newData.setInitialized();

                    });

                }
                newPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(newData -> {
                    ensureEssentialParts(newData);

                });
            } else {
                original.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(oldData -> {
                    newPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(newData -> {
                        newData.copyFrom(oldData);

                    });

                });

            }
        } finally {
            original.invalidateCaps();

        }
        if (newPlayer instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                data.recalculateCapacity(serverPlayer);
                data.syncToClient(serverPlayer);

            });

        }
    }

    private static void ensureEssentialParts(CyberwareUserData userData) {
        ItemStackHandler handler = userData.getInstalledCyberware();
        if (handler.getStackInSlot(RobosurgeonBlockEntity.SLOT_BRAIN).isEmpty()) {
            handler.setStackInSlot(RobosurgeonBlockEntity.SLOT_BRAIN, new ItemStack(ModItems.HUMAN_BRAIN.get()));

        }
        if (handler.getStackInSlot(RobosurgeonBlockEntity.SLOT_HEART).isEmpty()) {
            handler.setStackInSlot(RobosurgeonBlockEntity.SLOT_HEART, new ItemStack(ModItems.HUMAN_HEART.get()));

        }
        if (handler.getStackInSlot(RobosurgeonBlockEntity.SLOT_MUSCLE).isEmpty()) {
            handler.setStackInSlot(RobosurgeonBlockEntity.SLOT_MUSCLE, new ItemStack(ModItems.HUMAN_MUSCLE.get()));

        }
        if (handler.getStackInSlot(RobosurgeonBlockEntity.SLOT_BONES).isEmpty()) {
            handler.setStackInSlot(RobosurgeonBlockEntity.SLOT_BONES, new ItemStack(ModItems.HUMAN_BONE.get()));

        }

    }

    private static void syncData(ServerPlayer player) {
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            data.syncToClient(player);

        });

    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            if (event.player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
                    userData.tick(serverPlayer);
                    if (serverPlayer.tickCount % 20 == 0) {
                        userData.syncToClient(serverPlayer);

                    }
                });

            }
        }
    }

    private static boolean isHandFunctional(Player player, InteractionHand hand) {
        AtomicBoolean isFunctional = new AtomicBoolean(true);
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            ItemStackHandler handler = data.getInstalledCyberware();
            HumanoidArm mainArm = player.getMainArm();
            boolean isRightHand;
            if (hand == InteractionHand.MAIN_HAND) {
                isRightHand = (mainArm == HumanoidArm.RIGHT);

            } else {
                isRightHand = (mainArm == HumanoidArm.LEFT);

            }
            int slotIndex = isRightHand ? RobosurgeonBlockEntity.SLOT_ARMS : RobosurgeonBlockEntity.SLOT_ARMS + 1;
            if (handler.getStackInSlot(slotIndex).isEmpty()) {
                isFunctional.set(false);

            }
        });
        return isFunctional.get();

    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!isHandFunctional(event.getEntity(), InteractionHand.MAIN_HAND)) {
            event.setNewSpeed(0.0f);
            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        net.minecraft.world.level.block.state.BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.is(ModBlocks.ROBO_SURGEON.get()) ||
                state.is(ModBlocks.SURGERY_CHAMBER.get())) {
            return;

        }
        if (!isHandFunctional(event.getEntity(), InteractionHand.MAIN_HAND)) {
            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!isHandFunctional(event.getEntity(), event.getHand())) {
            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        net.minecraft.world.level.block.state.BlockState state = event.getLevel().getBlockState(event.getPos());
        if (state.is(ModBlocks.ROBO_SURGEON.get()) ||
                state.is(ModBlocks.SURGERY_CHAMBER.get())) {
            return;

        }
        if (!isHandFunctional(event.getEntity(), event.getHand())) {
            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!isHandFunctional(event.getEntity(), event.getHand())) {
            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void onBreakBlock(PlayerEvent.BreakSpeed event) {
        if (!isHandFunctional(event.getEntity(), InteractionHand.MAIN_HAND)) {
            event.setCanceled(true);

        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                ItemStackHandler handler = data.getInstalledCyberware();
                boolean hasSkin = false;
                for (int i = 0;
                     i < handler.getSlots();
                     i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cw) {
                        BodyPartType type = cw.getBodyPartType(stack);
                        if (type == BodyPartType.SKIN) hasSkin = true;
                    }
                }
                float multiplier = 1.0f;
                if (!hasSkin) multiplier += 0.5f;
                if (multiplier > 1.0f) {
                    event.setAmount(event.getAmount() * multiplier);

                }
            });

        }
    }
}