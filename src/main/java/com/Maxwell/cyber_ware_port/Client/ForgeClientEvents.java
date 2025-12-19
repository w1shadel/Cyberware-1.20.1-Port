package com.Maxwell.cyber_ware_port.Client;

import com.Maxwell.cyber_ware_port.Client.Upgrades.CyberEye.CyberwareMenuScreen;
import com.Maxwell.cyber_ware_port.Common.Capability.CyberwareCapabilityProvider;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareSlotType;
import com.Maxwell.cyber_ware_port.Common.Network.A_PacketHandler;
import com.Maxwell.cyber_ware_port.Common.Network.DoubleJumpPacket;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModItems;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    private static final String NBT_DOUBLE_JUMPED = "cyberware_double_jumped";

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        List<Component> tooltip_block = event.getToolTip();
        if (item == ModBlocks.RADIO_KIT_BLOCK.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.radio_kit")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (item == ModBlocks.COMPONENT_BOX.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.component_box")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (item == ModBlocks.RADIO_TOWER_CORE.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.radio_tower_core")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (item == ModBlocks.SCANNER.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.scanner")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (item == ModBlocks.RADIO_TOWER_COMPONENT.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.radio_component")
                    .withStyle(ChatFormatting.GRAY));
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.radio_component2")
                    .withStyle(ChatFormatting.GRAY));
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.radio_component3")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (item == ModBlocks.SURGERY_CHAMBER.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.surgery_chamber")
                    .withStyle(ChatFormatting.GRAY));
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.surgery_chamber2")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (item == ModBlocks.BLUEPRINT_CHEST.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.blueprint_chest")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (item == ModBlocks.CYBERWARE_WORKBENCH.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.cyberware_workbench")
                    .withStyle(ChatFormatting.GRAY));
        }
        if (item == ModBlocks.ROBO_SURGEON.get().asItem()) {
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.robo_surgeon")
                    .withStyle(ChatFormatting.GRAY));
            tooltip_block.add(Component.translatable("tooltip.cyber_ware_port.robo_surgeon2")
                    .withStyle(ChatFormatting.GRAY));

        }
        if (stack.hasTag() && stack.getTag().getBoolean("cyberware_ghost")) {
            var tooltip = event.getToolTip();
            Component name = tooltip.isEmpty() ? stack.getHoverName() : tooltip.get(0);
            tooltip.clear();
            tooltip.add(name);
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("cyberware.tooltip.ghost.remove")
                    .withStyle(ChatFormatting.RED));
            return;

        }
        if (stack.getItem() instanceof CyberwareItem cyberware) {
            ResourceLocation regName = ForgeRegistries.ITEMS.getKey(cyberware);
            if (regName != null && regName.getPath().contains("body_part")) {
                return;

            }
            boolean isShiftDown = Screen.hasShiftDown();
            var tooltip = event.getToolTip();
            if (!isShiftDown) {
                tooltip.add(Component.empty());
                Component keyName = Component.translatable("key.keyboard.shift");
                tooltip.add(Component.translatable("cyberware.tooltip.shiftPrompt", keyName)
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                return;

            }
            ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(cyberware);
            if (registryName != null) {
                String path = registryName.getPath();
                String key = "cyberware.tooltip." + path;
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));

            }
            tooltip.add(Component.empty());
            if (cyberware.canToggle(stack)) {
                boolean isActive = cyberware.isActive(stack);
                Component statusText = Component.translatable(isActive ? "cyberware.gui.active.enable" : "cyberware.gui.active.disable")
                        .withStyle(isActive ? ChatFormatting.GREEN : ChatFormatting.RED);
                tooltip.add(Component.translatable("cyberware.tooltip.status", statusText)
                        .withStyle(ChatFormatting.WHITE));

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
                    tooltip.add(Component.literal(" - ").append(req.getName(new ItemStack(req))).withStyle(ChatFormatting.GRAY));

                }
            }
            Set<Item> incompatibles = cyberware.getIncompatibleItems(stack);
            if (!incompatibles.isEmpty()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("cyberware.tooltip.incompatible").withStyle(ChatFormatting.RED));
                for (Item incompatible : incompatibles) {
                    tooltip.add(Component.literal(" - ")
                            .append(incompatible.getName(new ItemStack(incompatible)))
                            .withStyle(ChatFormatting.GRAY));

                }
            }
            CyberwareSlotType slotType = CyberwareSlotType.fromId(cyberware.getSlot(stack));
            if (slotType != null) {
                tooltip.add(Component.translatable("cyberware.tooltip.slot", slotType.getDisplayName())
                        .withStyle(ChatFormatting.GRAY));

            }
            if (cyberware.isPristine(stack)) {
                tooltip.add(Component.translatable("cyberware.quality.manufactured")
                        .withStyle(ChatFormatting.AQUA));

            } else {
                tooltip.add(Component.translatable("cyberware.quality.scavenged")
                        .withStyle(ChatFormatting.RED));

            }
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
            if (data.isCyberwareInstalled(ModItems.SYNTHETIC_SKIN.get())) {
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


}