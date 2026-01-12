package com.maxwell.cyber_ware_port.client;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.client.upgrades.cyberEye.CyberwareMenuScreen;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.A_PacketHandler;
import com.maxwell.cyber_ware_port.common.network.DoubleJumpPacket;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    private static final String NBT_DOUBLE_JUMPED = "cyberware_double_jumped";

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        List<Component> tooltip = event.getToolTip();
        addBlockTooltips(item, tooltip);
        if (stack.hasTag() && stack.getTag().getBoolean("cyberware_ghost")) {
            Component name = tooltip.isEmpty() ? stack.getHoverName() : tooltip.get(0);
            tooltip.clear();
            tooltip.add(name);
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("cyberware.tooltip.ghost.remove")
                    .withStyle(ChatFormatting.RED));
            return;
        }
    }

    private static void addBlockTooltips(Item item, List<Component> tooltip) {
        if (item == ModBlocks.RADIO_KIT_BLOCK.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.radio_kit").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.COMPONENT_BOX.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.component_box").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.RADIO_TOWER_CORE.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.radio_tower_core").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.SCANNER.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.scanner").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.RADIO_TOWER_COMPONENT.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.radio_component").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.radio_component2").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.radio_component3").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.CHARGER.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.charger").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.charger2").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.charger3").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.SURGERY_CHAMBER.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.surgery_chamber").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.surgery_chamber2").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.BLUEPRINT_CHEST.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.blueprint_chest").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.CYBERWARE_WORKBENCH.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.cyberware_workbench").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.ROBO_SURGEON.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.robo_surgeon").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.robo_surgeon2").withStyle(ChatFormatting.GRAY));
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;
        if (KeyInit.MENU_KEY.consumeClick()) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
                if (userData.isCyberwareInstalled(ModItems.CYBER_EYE.get())) {
                    if (mc.screen == null) {
                        mc.setScreen(new CyberwareMenuScreen());
                    }
                } else {
                    player.displayClientMessage(Component.translatable("message.cyber_ware_port.no_hud_installed"), true);
                }
            });
        }
        if (event.getKey() == mc.options.keyJump.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
            if (!player.onGround() && !player.isCreative() && !player.isSpectator()) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    if (data.isCyberwareInstalled(ModItems.LINEAR_ACTUATORS.get())) {
                        if (!player.getPersistentData().getBoolean(NBT_DOUBLE_JUMPED)) {
                            A_PacketHandler.INSTANCE.sendToServer(new DoubleJumpPacket());
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        model.leftArm.visible = true;
        model.leftSleeve.visible = true;
        model.rightArm.visible = true;
        model.rightSleeve.visible = true;
        model.leftLeg.visible = true;
        model.leftPants.visible = true;
        model.rightLeg.visible = true;
        model.rightPants.visible = true;
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (hasSkinUpgrade(data)) {
                return;
            }
            if (data.hasCyberLeftArm()) {
                model.leftArm.visible = false;
                model.leftSleeve.visible = false;
            }
            if (data.hasCyberRightArm()) {
                model.rightArm.visible = false;
                model.rightSleeve.visible = false;
            }
            if (data.hasCyberLeftLeg()) {
                model.leftLeg.visible = false;
                model.leftPants.visible = false;
            }
            if (data.hasCyberRightLeg()) {
                model.rightLeg.visible = false;
                model.rightPants.visible = false;
            }
        });
    }

    private static boolean hasSkinUpgrade(CyberwareUserData data) {
        ItemStackHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cw) {
                if (cw.getBodyPartType(stack) == BodyPartType.SKIN) {
                    return true;
                }
            }
        }
        return false;
    }
}