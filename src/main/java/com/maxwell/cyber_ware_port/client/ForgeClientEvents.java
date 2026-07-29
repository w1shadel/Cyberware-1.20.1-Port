package com.maxwell.cyber_ware_port.client;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.client.upgrades.cybereye.CyberwareMenuScreen;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareSlotType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.A_PacketHandler;
import com.maxwell.cyber_ware_port.common.network.ClientPacketHandler;
import com.maxwell.cyber_ware_port.common.network.DoubleJumpPacket;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    private static final String NBT_DOUBLE_JUMPED = "cyberware_double_jumped";

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        List<Component> tooltip = event.getToolTip();
        if (item == ModBlocks.RADIO_KIT_BLOCK.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.radio_kit").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.COMPONENT_BOX.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.component_box").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.RADIO_TOWER_CORE.get().asItem()) {
            tooltip.add(
                    Component.translatable("tooltip.cyber_ware_port.radio_tower_core").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.SCANNER.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.scanner").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.RADIO_TOWER_COMPONENT.get().asItem()) {
            tooltip.add(
                    Component.translatable("tooltip.cyber_ware_port.radio_component").withStyle(ChatFormatting.GRAY));
            tooltip.add(
                    Component.translatable("tooltip.cyber_ware_port.radio_component2").withStyle(ChatFormatting.GRAY));
            tooltip.add(
                    Component.translatable("tooltip.cyber_ware_port.radio_component3").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.CHARGER.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.charger").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.charger2").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.charger3").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.SURGERY_CHAMBER.get().asItem()) {
            tooltip.add(
                    Component.translatable("tooltip.cyber_ware_port.surgery_chamber").withStyle(ChatFormatting.GRAY));
            tooltip.add(
                    Component.translatable("tooltip.cyber_ware_port.surgery_chamber2").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.BLUEPRINT_CHEST.get().asItem()) {
            tooltip.add(
                    Component.translatable("tooltip.cyber_ware_port.blueprint_chest").withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.CYBERWARE_WORKBENCH.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.cyberware_workbench")
                    .withStyle(ChatFormatting.GRAY));
        } else if (item == ModBlocks.ROBO_SURGEON.get().asItem()) {
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.robo_surgeon").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.cyber_ware_port.robo_surgeon2").withStyle(ChatFormatting.GRAY));
        }
        if (stack.hasTag() && stack.getTag().getBoolean("cyberware_ghost")) {
            Component name = tooltip.isEmpty() ? stack.getHoverName() : tooltip.get(0);
            tooltip.clear();
            tooltip.add(name);
            tooltip.add(Component.translatable("cyberware.tooltip.ghost.remove").withStyle(ChatFormatting.RED));
            return;
        }
        ICyberware cyberware = CyberwareAPI.getCyberware(stack);
        if (cyberware != null) {
            ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
            if (registryName != null && registryName.getPath().contains("body_part")) {
                return;
            }
            boolean isShiftDown = Screen.hasShiftDown();
            if (!isShiftDown) {
                tooltip.add(Component.empty());
                Component keyName = Component.translatable("key.keyboard.shift");
                tooltip.add(Component.translatable("cyberware.tooltip.shiftPrompt", keyName)
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                return;
            }
            if (registryName != null) {
                String key = "cyberware.tooltip." + registryName.getPath();
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
            }
            tooltip.add(Component.empty());
            if (cyberware.canToggle(stack)) {
                boolean isActive = cyberware.isActive(stack);
                Component statusText = Component
                        .translatable(isActive ? "cyberware.gui.active.enable" : "cyberware.gui.active.disable")
                        .withStyle(isActive ? ChatFormatting.GREEN : ChatFormatting.RED);
                tooltip.add(
                        Component.translatable("cyberware.tooltip.status", statusText).withStyle(ChatFormatting.WHITE));
            }
            if (cyberware.hasEnergyProperties(stack)) {
                int consumption = cyberware.getEnergyConsumption(stack);
                if (consumption > 0) {
                    tooltip.add(Component.translatable("cyberware.tooltip.powerConsumption", consumption)
                            .withStyle(ChatFormatting.RED));
                }
                int generation = cyberware.getEnergyGeneration(stack);
                if (generation > 0) {
                    tooltip.add(Component.translatable("cyberware.tooltip.powerProduction", generation)
                            .withStyle(ChatFormatting.GREEN));
                }
                int storage = cyberware.getEnergyStorage(stack);
                if (storage > 0) {
                    tooltip.add(Component.translatable("cyberware.tooltip.capacity", storage)
                            .withStyle(ChatFormatting.AQUA));
                }
                int eventCost = cyberware.getEventConsumption(stack);
                if (eventCost > 0) {
                    tooltip.add(Component.translatable("cyberware.tooltip.eventCost", eventCost)
                            .withStyle(ChatFormatting.RED));
                }
            }
            if (cyberware.getMaxInstallAmount(stack) > 1) {
                tooltip.add(Component.translatable("cyberware.tooltip.maxInstall", cyberware.getMaxInstallAmount(stack))
                        .withStyle(ChatFormatting.BLUE));
            }
            tooltip.add(Component.translatable("cyberware.tooltip.essence", cyberware.getEssenceCost(stack))
                    .withStyle(ChatFormatting.DARK_PURPLE));
            Set<Item> reqs = cyberware.getPrerequisites(stack);
            if (!reqs.isEmpty()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("cyberware.tooltip.requires").withStyle(ChatFormatting.AQUA));
                for (Item req : reqs) {
                    tooltip.add(Component.literal(" - ").append(req.getName(new ItemStack(req)))
                            .withStyle(ChatFormatting.GRAY));
                }
            }
            Set<Item> incompatibles = cyberware.getIncompatibleItems(stack);
            if (!incompatibles.isEmpty()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("cyberware.tooltip.incompatible").withStyle(ChatFormatting.RED));
                for (Item incompatible : incompatibles) {
                    tooltip.add(Component.literal(" - ").append(incompatible.getName(new ItemStack(incompatible)))
                            .withStyle(ChatFormatting.GRAY));
                }
            }
            CyberwareSlotType slotType = CyberwareSlotType.fromId(cyberware.getSlot(stack));
            if (slotType != null) {
                tooltip.add(Component.translatable("cyberware.tooltip.slot", slotType.getDisplayName())
                        .withStyle(ChatFormatting.GRAY));
            }
            if (cyberware.isPristine(stack)) {
                tooltip.add(Component.translatable("cyberware.quality.manufactured").withStyle(ChatFormatting.AQUA));
            } else {
                tooltip.add(Component.translatable("cyberware.quality.scavenged").withStyle(ChatFormatting.RED));
            }
        }
    }

    @SubscribeEvent
    public static void onClientLogout(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        ClientPacketHandler.reset();
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null)
            return;
        if (KeyInit.MENU_KEY.consumeClick()) {
            player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(userData -> {
                if (userData.isCyberwareInstalled(ModItems.CYBER_EYE.get())) {
                    if (mc.screen == null) {
                        mc.setScreen(new CyberwareMenuScreen());
                    }
                } else {
                    player.displayClientMessage(Component.translatable("message.cyber_ware_port.no_hud_installed"),
                            true);
                }
            });
        }
        if (event.getKey() == mc.options.keyJump.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
            if (!player.onGround() && !player.isCreative() && !player.isSpectator()) {
                player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                    if (data.isCyberwareActive(ModItems.LINEAR_ACTUATORS.get())) {
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
        model.leftArm.visible = model.leftSleeve.visible = true;
        model.rightArm.visible = model.rightSleeve.visible = true;
        model.leftLeg.visible = model.leftPants.visible = true;
        model.rightLeg.visible = model.rightPants.visible = true;
        player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            if (hasSkinUpgrade(data))
                return;
            if (!data.isCyberwareInstalled(ModItems.HUMAN_LEFT_ARM.get())) {
                model.leftArm.visible = false;
                model.leftSleeve.visible = false;
            }
            if (!data.isCyberwareInstalled(ModItems.HUMAN_RIGHT_ARM.get())) {
                model.rightArm.visible = false;
                model.rightSleeve.visible = false;
            }
            if (!data.isCyberwareInstalled(ModItems.HUMAN_LEFT_LEG.get())) {
                model.leftLeg.visible = false;
                model.leftPants.visible = false;
            }
            if (!data.isCyberwareInstalled(ModItems.HUMAN_RIGHT_LEG.get())) {
                model.rightLeg.visible = false;
                model.rightPants.visible = false;
            }
        });
    }

    private static boolean hasSkinUpgrade(CyberwareUserData data) {
        ItemStackHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null && cw.getBodyPartType(stack) == BodyPartType.SKIN) {
                return true;
            }
        }
        return false;
    }
}