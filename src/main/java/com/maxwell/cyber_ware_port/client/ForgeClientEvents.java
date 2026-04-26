package com.maxwell.cyber_ware_port.client;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.client.upgrades.cybereye.CyberwareMenuScreen;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareSlotType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.ClientPacketHandler;
import com.maxwell.cyber_ware_port.common.network.DoubleJumpPacket;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = CyberWare.MODID, value = Dist.CLIENT)
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
        if (stack.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
            Component name = tooltip.isEmpty() ? stack.getHoverName() : tooltip.get(0);
            tooltip.clear();
            tooltip.add(name);
            tooltip.add(Component.translatable("cyberware.tooltip.ghost.remove").withStyle(ChatFormatting.RED));
            return;
        }
        ICyberware cyberware = CyberwareAPI.getCyberware(stack);
        if (cyberware != null) {
            Identifier registryName = BuiltInRegistries.ITEM.getKey(item);
            if (registryName.getPath().contains("body_part")) return;
            boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
            if (!isShiftDown) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("cyberware.tooltip.shiftPrompt", Component.translatable("key.keyboard.shift")).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                return;
            }
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("cyberware.tooltip." + registryName.getPath()).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.empty());
            if (cyberware.canToggle(stack)) {
                boolean isActive = cyberware.isActive(stack);
                tooltip.add(Component.translatable("cyberware.tooltip.status", Component.translatable(isActive ? "cyberware.gui.active.enable" : "cyberware.gui.active.disable").withStyle(isActive ? ChatFormatting.GREEN : ChatFormatting.RED)).withStyle(ChatFormatting.WHITE));
            }
            if (cyberware.hasEnergyProperties(stack)) {
                int consumption = cyberware.getEnergyConsumption(stack);
                if (consumption > 0)
                    tooltip.add(Component.translatable("cyberware.tooltip.powerConsumption", consumption).withStyle(ChatFormatting.RED));
                int generation = cyberware.getEnergyGeneration(stack);
                if (generation > 0)
                    tooltip.add(Component.translatable("cyberware.tooltip.powerProduction", generation).withStyle(ChatFormatting.GREEN));
                int storage = cyberware.getEnergyStorage(stack);
                if (storage > 0)
                    tooltip.add(Component.translatable("cyberware.tooltip.capacity", storage).withStyle(ChatFormatting.AQUA));
                int eventCost = cyberware.getEventConsumption(stack);
                if (eventCost > 0)
                    tooltip.add(Component.translatable("cyberware.tooltip.eventCost", eventCost).withStyle(ChatFormatting.RED));
            }
            if (cyberware.getMaxInstallAmount(stack) > 1)
                tooltip.add(Component.translatable("cyberware.tooltip.maxInstall", cyberware.getMaxInstallAmount(stack)).withStyle(ChatFormatting.BLUE));
            tooltip.add(Component.translatable("cyberware.tooltip.essence", cyberware.getEssenceCost(stack)).withStyle(ChatFormatting.DARK_PURPLE));
            Set<Item> reqs = cyberware.getPrerequisites(stack);
            if (!reqs.isEmpty()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("cyberware.tooltip.requires").withStyle(ChatFormatting.AQUA));
                for (Item req : reqs)
                    tooltip.add(Component.literal(" - ").append(req.getName(new ItemStack(req))).withStyle(ChatFormatting.GRAY));
            }
            Set<Item> incompatibles = cyberware.getIncompatibleItems(stack);
            if (!incompatibles.isEmpty()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("cyberware.tooltip.incompatible").withStyle(ChatFormatting.RED));
                for (Item incompatible : incompatibles)
                    tooltip.add(Component.literal(" - ").append(incompatible.getName(new ItemStack(incompatible))).withStyle(ChatFormatting.GRAY));
            }
            CyberwareSlotType slotType = CyberwareSlotType.fromId(cyberware.getSlot(stack));
            if (slotType != null)
                tooltip.add(Component.translatable("cyberware.tooltip.slot", slotType.getDisplayName()).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable(cyberware.isPristine(stack) ? "cyberware.quality.manufactured" : "cyberware.quality.scavenged").withStyle(cyberware.isPristine(stack) ? ChatFormatting.AQUA : ChatFormatting.RED));
        }
    }

    @SubscribeEvent
    public static void onClientLogout(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        ClientPacketHandler.reset();
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (KeyInit.MENU_KEY.consumeClick()) {
            CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            if (data.isCyberwareInstalled(ModItems.CYBER_EYE.get())) {
                if (mc.screen == null) mc.setScreen(new CyberwareMenuScreen());
            } else {
                player.sendOverlayMessage(Component.translatable("message.cyber_ware_port.no_hud_installed"));
            }
        }
        if (event.getKey() == mc.options.keyJump.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
            if (!player.onGround() && !player.isCreative() && !player.isSpectator()) {
                CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                ItemStack actuatorStack = getInstalledStack(data, ModItems.LINEAR_ACTUATORS.get());
                if (!actuatorStack.isEmpty()) {
                    ICyberware cw = CyberwareAPI.getCyberware(actuatorStack);
                    if (cw != null && cw.isActive(actuatorStack) && !player.getPersistentData().getBooleanOr(NBT_DOUBLE_JUMPED, false)) {
                        ClientPacketDistributor.sendToServer(new DoubleJumpPacket());
                    }
                }
            }
        }
    }

    private static ItemStack getInstalledStack(CyberwareUserData data, Item item) {
        ItemStackHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.is(item)) return stack;
        }
        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!(event.getRenderState() instanceof AvatarRenderState state)) return;
        int entityId = state.id;
        var model = event.getRenderer().getModel();
        Entity entity = Minecraft.getInstance().level.getEntity(entityId);
        if (entity instanceof Player player) {
            CyberwareUserData data = player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            if (hasSkinUpgrade(data)) return;
            state.showLeftSleeve = true;
            state.showRightSleeve = true;
            state.showLeftPants = true;
            state.showRightPants = true;
            if (data.hasCyberLeftArm()) {
                state.showLeftSleeve = false;
            }
            if (data.hasCyberRightArm()) {
                state.showRightSleeve = false;
            }
            if (data.hasCyberLeftLeg()) {
                state.showLeftPants = false;
            }
            if (data.hasCyberRightLeg()) {
                state.showRightPants = false;
            }
        }
    }

    private static boolean hasSkinUpgrade(CyberwareUserData data) {
        ItemStackHandler handler = data.getInstalledCyberware();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null && cw.getBodyPartType(stack) == BodyPartType.SKIN) return true;
        }
        return false;
    }
}