package com.maxwell.cyber_ware_port.client.upgrades.cybereye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.client.ClientCyberwareSettings;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.ToggleCyberwarePacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class CyberwareMenuScreen extends Screen {
    private static final Identifier HUD_COLOR_ICON =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_color.png");
    private static final Identifier HUD_POS_ICON =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_pos.png");
    private static final Identifier HUD_RESET_ICON =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_reset.png");
    private static final float INNER_RADIUS = 40.0f;
    private static final float OUTER_RADIUS = 100.0f;
    private static final float ITEM_RADIUS = (INNER_RADIUS + OUTER_RADIUS) / 2.0f;
    private static final int HUD_WIDTH = 80;
    private static final int HUD_HEIGHT = 25;
    private static final int POS_BTN_X = 338;
    private static final int POS_BTN_Y = 63;
    private static final int BTN_SIZE = 16;
    private static final int[] PRESET_COLORS = {
            0xFF00FFFF, 0xFF00FF00, 0xFFFF0000, 0xFFFFFF00,
            0xFFFFFFFF, 0xFFFF00FF, 0xFF0000FF, 0xFFFF8000
    };
    private final List<ToggleablePart> parts = new ArrayList<>();
    public boolean isColorSettingsOpen = false;
    private boolean isHudMoveMode = false;
    private boolean isDraggingHud = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private EditBox hexInput;

    public CyberwareMenuScreen() {
        super(Component.translatable("gui.cyber_ware_port.menu"));
    }

    @Override
    protected void init() {
        super.init();
        parts.clear();
        if (this.minecraft != null && this.minecraft.player != null) {
            CyberwareUserData data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            IItemHandler handler = data.getInstalledCyberware();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (cw != null && cw.canToggle(stack)) {
                    parts.add(new ToggleablePart(i, stack, cw));
                }
            }
        }
        int boxWidth = 80;
        int boxHeight = 20;
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        this.hexInput = new EditBox(this.font, centerX - boxWidth / 2, centerY + 30, boxWidth, boxHeight, Component.literal("Color Hex"));
        this.hexInput.setMaxLength(8);
        this.hexInput.setValue(ClientCyberwareSettings.getHudColorAsHex());
        this.hexInput.setBordered(true);
        this.hexInput.setVisible(false);
        this.hexInput.setResponder(ClientCyberwareSettings::setHudColorFromHex);
        this.addRenderableWidget(this.hexInput);
    }

    @Override
    public void tick() {
        super.tick();
        handleMovementInput();
    }

    private void handleMovementInput() {
        if (this.minecraft == null || this.minecraft.player == null) return;
        if (this.hexInput.isFocused()) return;
        updateKey(this.minecraft.options.keyUp);
        updateKey(this.minecraft.options.keyDown);
        updateKey(this.minecraft.options.keyLeft);
        updateKey(this.minecraft.options.keyRight);
        updateKey(this.minecraft.options.keyJump);
        updateKey(this.minecraft.options.keySprint);
        updateKey(this.minecraft.options.keyShift);
    }

    private void updateKey(KeyMapping keyMapping) {
        Window window = Minecraft.getInstance().getWindow();
        InputConstants.Key key = keyMapping.getKey();
        boolean isDown = InputConstants.isKeyDown(window, key.getValue());
        keyMapping.setDown(isDown);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (this.isColorSettingsOpen && this.hexInput.isFocused()) {
            if (this.hexInput.keyPressed(event)) return true;
            if (keyCode == InputConstants.KEY_ESCAPE) {
                toggleColorSettings();
                return true;
            }
        }
        if (this.isHudMoveMode) {
            if (keyCode == InputConstants.KEY_ESCAPE || keyCode == this.minecraft.options.keyInventory.getKey().getValue()) {
                this.isHudMoveMode = false;
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);
        int centerX = width / 2;
        int centerY = height / 2;
        if (isColorSettingsOpen || isHudMoveMode) {
            g.fillGradient(0, 0, width, height, 0x80000000, 0x80000000);
        }
        if (!isColorSettingsOpen && !isHudMoveMode) {
            renderRadialMenu(g, centerX, centerY, mouseX, mouseY);
        }
        renderHudPreview(g, mouseX, mouseY);
        if (!isHudMoveMode) {
            renderIconButton(g, HUD_COLOR_ICON, POS_BTN_X, POS_BTN_Y + 20, mouseX, mouseY, "HUD Color Settings");
            if (!isColorSettingsOpen) {
                renderIconButton(g, HUD_POS_ICON, POS_BTN_X, POS_BTN_Y, mouseX, mouseY, "Move HUD Position");
            }
        }
        if (isColorSettingsOpen) {
            renderColorSettings(g, centerX, centerY, mouseX, mouseY);
        }
        if (isHudMoveMode) {
            g.centeredText(this.font, Component.literal("HUD MOVE MODE"), centerX, 40, 0xFF00FF00);
            g.centeredText(this.font, Component.literal("Drag HUD to move / Press ESC to finish"), centerX, 55, 0xFFFFFFFF);
        }
    }

    private void renderIconButton(GuiGraphicsExtractor g, Identifier texture, int x, int y, int mouseX, int mouseY, String tooltip) {
        float[] rgba = ClientCyberwareSettings.getColorFloats();
        int color = ARGB.color((int) (rgba[3] * 255), (int) (rgba[0] * 255), (int) (rgba[1] * 255), (int) (rgba[2] * 255));
        g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, BTN_SIZE, BTN_SIZE, BTN_SIZE, BTN_SIZE, color);
        if (mouseX >= x && mouseX <= x + BTN_SIZE && mouseY >= y && mouseY <= y + BTN_SIZE) {
            g.outline(x - 1, y - 1, BTN_SIZE + 2, BTN_SIZE + 2, 0xFFFFFFFF);
            if (!isColorSettingsOpen && !isHudMoveMode) {
                g.setTooltipForNextFrame(Component.literal(tooltip), mouseX, mouseY);
            }
        }
    }

    private void renderRadialMenu(GuiGraphicsExtractor g, int centerX, int centerY, int mouseX, int mouseY) {
        if (!parts.isEmpty()) {
            double angleStep = 2 * Math.PI / parts.size();
            for (int i = 0; i < parts.size(); i++) {
                ToggleablePart part = parts.get(i);
                double itemAngle = i * angleStep - Math.PI / 2;
                int x = centerX + (int) (ITEM_RADIUS * Math.cos(itemAngle));
                int y = centerY + (int) (ITEM_RADIUS * Math.sin(itemAngle));
                boolean isActive = part.item.isActive(part.stack);
                boolean isHovered = (mouseX >= x - 12 && mouseX <= x + 12 && mouseY >= y - 12 && mouseY <= y + 12);
                if (isHovered) {
                    Component statusText = isActive ? Component.translatable("cyberware.gui.active") : Component.translatable("cyberware.gui.inactive");
                    g.centeredText(this.font, statusText, x, y - 20, 0xFFFFFF00);
                }
                g.item(part.stack, x - 8, y - 8);
                int outlineColor = isActive ? 0xFF00FF00 : 0xFFFF0000;
                g.outline(x - 10, y - 10, 20, 20, outlineColor);
                if (isHovered) {
                    g.setTooltipForNextFrame(this.font, part.stack, mouseX, mouseY);
                }
            }
        }
    }

    private void renderColorSettings(GuiGraphicsExtractor g, int centerX, int centerY, int mouseX, int mouseY) {
        this.hexInput.setPosition(centerX - 40, centerY + 15);
        this.hexInput.setVisible(true);
        int swatchSize = 20, gap = 4;
        int totalWidth = (swatchSize * PRESET_COLORS.length) + (gap * (PRESET_COLORS.length - 1));
        int startX = centerX - totalWidth / 2;
        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int color = PRESET_COLORS[i];
            int x = startX + (swatchSize + gap) * i;
            int y = centerY - 15;
            g.fill(x, y, x + swatchSize, y + swatchSize, color);
            g.outline(x, y, swatchSize, swatchSize, 0xFF888888);
            if (mouseX >= x && mouseX <= x + swatchSize && mouseY >= y && mouseY <= y + swatchSize) {
                g.outline(x - 1, y - 1, swatchSize + 2, swatchSize + 2, 0xFFFFFFFF);
            }
        }
    }

    private void renderHudPreview(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        if (this.minecraft.player == null) return;
        int hudX = ClientCyberwareSettings.hudX;
        int hudY = ClientCyberwareSettings.hudY;
        if (isHudMoveMode) {
            g.outline(hudX - 1, hudY - 1, HUD_WIDTH + 2, HUD_HEIGHT + 2, 0xFF00FF00);
            g.centeredText(this.font, Component.literal("DRAG TO MOVE"), hudX + HUD_WIDTH / 2, hudY - 10, 0xFF00FF00);
            if (isDraggingHud || (mouseX >= hudX && mouseX <= hudX + HUD_WIDTH && mouseY >= hudY && mouseY <= hudY + HUD_HEIGHT)) {
                g.outline(hudX - 1, hudY - 1, HUD_WIDTH + 2, HUD_HEIGHT + 2, 0xFFFFFFFF);
            }
            int resetBtnX = hudX + (HUD_WIDTH - BTN_SIZE) / 2;
            int resetBtnY = hudY + HUD_HEIGHT + 5;
            float[] rgba = ClientCyberwareSettings.getColorFloats();
            int iconColor = net.minecraft.util.ARGB.color(
                    (int) (rgba[3] * 255),
                    (int) (rgba[0] * 255),
                    (int) (rgba[1] * 255),
                    (int) (rgba[2] * 255)
            );
            g.blit(
                    net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                    HUD_RESET_ICON,
                    resetBtnX,
                    resetBtnY,
                    0.0F, 0.0F,
                    BTN_SIZE, BTN_SIZE,
                    BTN_SIZE, BTN_SIZE,
                    iconColor
            );
            if (mouseX >= resetBtnX && mouseX <= resetBtnX + BTN_SIZE && mouseY >= resetBtnY && mouseY <= resetBtnY + BTN_SIZE) {
                g.outline(resetBtnX - 1, resetBtnY - 1, BTN_SIZE + 2, BTN_SIZE + 2, 0xFFFFFFFF);
                g.setTooltipForNextFrame(Component.literal("Reset Position"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (button == 0) {
            int centerX = width / 2;
            int centerY = height / 2;
            if (!isHudMoveMode) {
                if (mouseX >= POS_BTN_X && mouseX <= POS_BTN_X + BTN_SIZE && mouseY >= POS_BTN_Y + 21 && mouseY <= POS_BTN_Y + 21 + BTN_SIZE) {
                    toggleColorSettings();
                    playClickSound();
                    return true;
                }
            }
            if (!isColorSettingsOpen && !isHudMoveMode) {
                if (mouseX >= POS_BTN_X && mouseX <= POS_BTN_X + BTN_SIZE && mouseY >= POS_BTN_Y + 1 && mouseY <= POS_BTN_Y + BTN_SIZE + 1) {
                    isHudMoveMode = true;
                    playClickSound();
                    return true;
                }
            }
            if (isColorSettingsOpen) {
                if (this.hexInput.mouseClicked(event, false)) {
                    this.setFocused(this.hexInput);
                    return true;
                }
                int swatchSize = 20, gap = 4;
                int totalWidth = (swatchSize * PRESET_COLORS.length) + (gap * (PRESET_COLORS.length - 1));
                int startX = centerX - totalWidth / 2;
                for (int i = 0; i < PRESET_COLORS.length; i++) {
                    int x = startX + (swatchSize + gap) * i;
                    if (mouseX >= x && mouseX <= x + swatchSize && mouseY >= centerY - 15 && mouseY <= centerY - 15 + swatchSize) {
                        ClientCyberwareSettings.hudColor = PRESET_COLORS[i];
                        this.hexInput.setValue(ClientCyberwareSettings.getHudColorAsHex());
                        playClickSound();
                        return true;
                    }
                }
            }
            if (isHudMoveMode) {
                int hudX = ClientCyberwareSettings.hudX, hudY = ClientCyberwareSettings.hudY;
                int rX = hudX + (HUD_WIDTH - BTN_SIZE) / 2, rY = hudY + HUD_HEIGHT + 5;
                if (mouseX >= rX && mouseX <= rX + BTN_SIZE && mouseY >= rY && mouseY <= rY + BTN_SIZE) {
                    ClientCyberwareSettings.hudX = 10;
                    ClientCyberwareSettings.hudY = 10;
                    playClickSound();
                    return true;
                }
                if (mouseX >= hudX && mouseX <= hudX + HUD_WIDTH && mouseY >= hudY && mouseY <= hudY + HUD_HEIGHT) {
                    isDraggingHud = true;
                    dragOffsetX = (int) mouseX - hudX;
                    dragOffsetY = (int) mouseY - hudY;
                    playClickSound();
                    return true;
                }
            }
            if (!isColorSettingsOpen && !isHudMoveMode && !parts.isEmpty()) {
                double angleStep = 2 * Math.PI / parts.size();
                for (int i = 0; i < parts.size(); i++) {
                    ToggleablePart part = parts.get(i);
                    double itemAngle = i * angleStep - Math.PI / 2;
                    int x = centerX + (int) (ITEM_RADIUS * Math.cos(itemAngle));
                    int y = centerY + (int) (ITEM_RADIUS * Math.sin(itemAngle));
                    if (mouseX >= x - 12 && mouseX <= x + 12 && mouseY >= y - 12 && mouseY <= y + 12) {
                        ClientPacketDistributor.sendToServer(new ToggleCyberwarePacket(part.slotId));
                        playClickSound();
                        part.item.toggle(part.stack);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (isHudMoveMode && isDraggingHud && event.button() == 0) {
            ClientCyberwareSettings.hudX = (int) event.x() - dragOffsetX;
            ClientCyberwareSettings.hudY = (int) event.y() - dragOffsetY;
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && isDraggingHud) {
            isDraggingHud = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    private void toggleColorSettings() {
        isColorSettingsOpen = !isColorSettingsOpen;
        this.hexInput.setVisible(isColorSettingsOpen);
        if (!isColorSettingsOpen) {
            this.hexInput.setFocused(false);
            this.setFocused(null);
        }
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record ToggleablePart(int slotId, ItemStack stack, ICyberware item) {
    }
}