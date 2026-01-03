package com.maxwell.cyber_ware_port.client.upgrades.cyberEye;

import com.maxwell.cyber_ware_port.client.ClientCyberwareSettings;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.A_PacketHandler;
import com.maxwell.cyber_ware_port.common.network.ToggleCyberwarePacket;
import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CyberwareMenuScreen extends Screen {


    private static final ResourceLocation HUD_COLOR_ICON =
             ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_color.png");
    private static final ResourceLocation HUD_POS_ICON =
             ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_pos.png");

    private static final ResourceLocation HUD_RESET_ICON =
            ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_reset.png");

    private static final float INNER_RADIUS = 40.0f;
    private static final float OUTER_RADIUS = 100.0f;
    private static final float ITEM_RADIUS = (INNER_RADIUS + OUTER_RADIUS) / 2.0f;
    private final List<ToggleablePart> parts = new ArrayList<>();

    private boolean isHudMoveMode = false;
    private boolean isDraggingHud = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    private static final int HUD_WIDTH = 80;
    private static final int HUD_HEIGHT = 25;

    private static final int POS_BTN_X = 338;
    private static final int POS_BTN_Y = 63;
    private static final int BTN_SIZE = 16;

    public boolean isColorSettingsOpen = false;
    private EditBox hexInput;
    private static final int[] PRESET_COLORS = {
            0xFF00FFFF, 0xFF00FF00, 0xFFFF0000, 0xFFFFFF00,
            0xFFFFFFFF, 0xFFFF00FF, 0xFF0000FF, 0xFFFF8000
    };

    public CyberwareMenuScreen() {
        super(Component.translatable("gui.cyber_ware_port.menu"));
    }

    @Override
    protected void init() {
        super.init();
        parts.clear();
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
                ItemStackHandler handler = data.getInstalledCyberware();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cw) {
                        if (cw.canToggle(stack)) {
                            parts.add(new ToggleablePart(i, stack, cw));
                        }
                    }
                }
            });
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
        this.hexInput.tick();
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
        long window = Minecraft.getInstance().getWindow().getWindow();
        int keyCode = keyMapping.getKey().getValue();
        boolean isDown = InputConstants.isKeyDown(window, keyCode);
        keyMapping.setDown(isDown);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isColorSettingsOpen && this.hexInput.isFocused()) {
            if (this.hexInput.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (keyCode == InputConstants.KEY_ESCAPE) {
                toggleColorSettings();
                return true;
            }
        }

        if (this.isHudMoveMode && (keyCode == InputConstants.KEY_ESCAPE || keyCode == this.minecraft.options.keyInventory.getKey().getValue())) {
            this.isHudMoveMode = false;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.isColorSettingsOpen && this.hexInput.isFocused()) {
            if (this.hexInput.charTyped(codePoint, modifiers)) return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
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
            g.drawCenteredString(this.font, "HUD MOVE MODE", centerX, 40, 0xFF00FF00);
            g.drawCenteredString(this.font, "Drag HUD to move / Press ESC to finish", centerX, 55, 0xFFFFFFFF);
        }

        super.render(g, mouseX, mouseY, partialTick);
    }
    private void renderIconButton(GuiGraphics g, ResourceLocation texture, int x, int y, int mouseX, int mouseY, String tooltip) {
        RenderSystem.setShaderTexture(0, texture);

        float[] rgba = ClientCyberwareSettings.getColorFloats();
        RenderSystem.setShaderColor(rgba[0], rgba[1], rgba[2], rgba[3]);


        g.blit(texture, x, y, BTN_SIZE, BTN_SIZE, 0, 0, 8, 8, 8, 8);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (mouseX >= x && mouseX <= x + BTN_SIZE && mouseY >= y && mouseY <= y + BTN_SIZE) {
            g.renderOutline(x - 1, y - 1, BTN_SIZE + 2, BTN_SIZE + 2, 0xFFFFFFFF);
            if (!isColorSettingsOpen && !isHudMoveMode) {
                g.renderTooltip(this.font, Component.literal(tooltip), mouseX, mouseY);
            }
        }
    }
    private void renderRadialMenu(GuiGraphics g, int centerX, int centerY, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        Matrix4f matrix = g.pose().last().pose();

        float red = 1.0f; float green = 0.0f; float blue = 0.0f; float alpha = 0.4f;
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        int segments = 60;
        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            buffer.vertex(matrix, centerX + cos * OUTER_RADIUS, centerY + sin * OUTER_RADIUS, 0)
                    .color(red, green, blue, alpha).endVertex();
            buffer.vertex(matrix, centerX + cos * INNER_RADIUS, centerY + sin * INNER_RADIUS, 0)
                    .color(red, green, blue, alpha).endVertex();
        }
        tesselator.end();
        RenderSystem.disableBlend();

        if (!parts.isEmpty()) {
            double angleStep = 2 * Math.PI / parts.size();
            for (int i = 0; i < parts.size(); i++) {
                ToggleablePart part = parts.get(i);
                double itemAngle = i * angleStep - Math.PI / 2;
                int x = centerX + (int) (ITEM_RADIUS * Math.cos(itemAngle));
                int y = centerY + (int) (ITEM_RADIUS * Math.sin(itemAngle));
                boolean isActive = part.item.isActive(part.stack);

                boolean isHovered = (mouseX >= x - 12 && mouseX <= x + 12 && mouseY >= y - 12 && mouseY <= y + 12);

                g.pose().pushPose();
                if (isHovered) {
                    Component statusText = isActive
                            ? Component.translatable("cyberware.gui.active")
                            : Component.translatable("cyberware.gui.inactive");
                    g.drawCenteredString(this.font, statusText, x, y - 20, 0xFFFFFF00);
                }

                g.renderItem(part.stack, x - 8, y - 8);
                int outlineColor = isActive ? 0xFF00FF00 : 0xFFFF0000;
                g.renderOutline(x - 10, y - 10, 20, 20, outlineColor);
                if (isHovered) {
                    g.renderTooltip(this.font, part.stack, mouseX, mouseY);
                }
                g.pose().popPose();
            }
        }
    }

    private void renderColorSettings(GuiGraphics g, int centerX, int centerY, int mouseX, int mouseY) {
        int boxWidth = 80;
        this.hexInput.setX(centerX - boxWidth / 2);
        this.hexInput.setY(centerY + 15);
        this.hexInput.setVisible(true);

        int swatchSize = 20;
        int gap = 4;
        int totalWidth = (swatchSize * PRESET_COLORS.length) + (gap * (PRESET_COLORS.length - 1));
        int startX = centerX - totalWidth / 2;
        int startY = centerY - 15;

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int color = PRESET_COLORS[i];
            int x = startX + (swatchSize + gap) * i;
            int y = startY;
            g.fill(x, y, x + swatchSize, y + swatchSize, color);
            g.renderOutline(x, y, swatchSize, swatchSize, 0xFF888888);
            if (mouseX >= x && mouseX <= x + swatchSize && mouseY >= y && mouseY <= y + swatchSize) {
                g.renderOutline(x - 1, y - 1, swatchSize + 2, swatchSize + 2, 0xFFFFFFFF);
            }
            if (color == ClientCyberwareSettings.hudColor) {
                g.renderOutline(x - 2, y - 2, swatchSize + 4, swatchSize + 4, 0xFFFFFF00);
            }
        }
        g.drawCenteredString(this.font, "HUD Color Palette", centerX, startY - 15, 0xFFFFFFFF);
        g.drawCenteredString(this.font, "(Hex)", centerX, this.hexInput.getY() + 20 + 2, 0xFFAAAAAA);
    }

    private void renderHudPreview(GuiGraphics g, int mouseX, int mouseY) {
        if (this.minecraft.player == null) return;
        this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(data -> {
            int hudX = ClientCyberwareSettings.hudX;
            int hudY = ClientCyberwareSettings.hudY;





            if (isHudMoveMode) {

                g.renderOutline(hudX - 1, hudY - 1, HUD_WIDTH + 2, HUD_HEIGHT + 2, 0xFF00FF00);
                g.drawCenteredString(this.font, "DRAG TO MOVE", hudX + HUD_WIDTH / 2, hudY - 10, 0xFF00FF00);

                if (isDraggingHud || (mouseX >= hudX && mouseX <= hudX + HUD_WIDTH && mouseY >= hudY && mouseY <= hudY + HUD_HEIGHT)) {
                    g.renderOutline(hudX - 1, hudY - 1, HUD_WIDTH + 2, HUD_HEIGHT + 2, 0xFFFFFFFF);
                }

                int resetBtnX = hudX + (HUD_WIDTH - BTN_SIZE) / 2;
                int resetBtnY = hudY + HUD_HEIGHT + 5;

                RenderSystem.setShaderTexture(0, HUD_RESET_ICON);

                float[] rgba = ClientCyberwareSettings.getColorFloats();
                RenderSystem.setShaderColor(rgba[0], rgba[1], rgba[2], rgba[3]);


                g.blit(HUD_RESET_ICON, resetBtnX, resetBtnY, BTN_SIZE, BTN_SIZE, 0, 0, 8, 8, 8, 8);

                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

                if (mouseX >= resetBtnX && mouseX <= resetBtnX + BTN_SIZE && mouseY >= resetBtnY && mouseY <= resetBtnY + BTN_SIZE) {
                    g.renderOutline(resetBtnX - 1, resetBtnY - 1, BTN_SIZE + 2, BTN_SIZE + 2, 0xFFFFFFFF);
                    g.renderTooltip(this.font, Component.literal("Reset Position"), mouseX, mouseY);
                }
            }
        });
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int centerX = width / 2;
            int centerY = height / 2;

            if (!isHudMoveMode) {
                int colorBtnX = POS_BTN_X;
                int colorBtnY = POS_BTN_Y + 21;

                if (mouseX >= colorBtnX && mouseX <= colorBtnX + BTN_SIZE && mouseY >= colorBtnY && mouseY <= colorBtnY + BTN_SIZE) {
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
                if (this.hexInput.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused(this.hexInput);
                    return true;
                } else {
                    this.setFocused(null);
                    this.hexInput.setFocused(false);
                }
                int swatchSize = 20;
                int gap = 4;
                int totalWidth = (swatchSize * PRESET_COLORS.length) + (gap * (PRESET_COLORS.length - 1));
                int startX = centerX - totalWidth / 2;
                int startY = centerY - 15;
                for (int i = 0; i < PRESET_COLORS.length; i++) {
                    int x = startX + (swatchSize + gap) * i;
                    int y = startY;
                    if (mouseX >= x && mouseX <= x + swatchSize && mouseY >= y && mouseY <= y + swatchSize) {
                        ClientCyberwareSettings.hudColor = PRESET_COLORS[i];
                        this.hexInput.setValue(ClientCyberwareSettings.getHudColorAsHex());
                        playClickSound();
                        return true;
                    }
                }
                return true;
            }

            if (isHudMoveMode) {
                int hudX = ClientCyberwareSettings.hudX;
                int hudY = ClientCyberwareSettings.hudY;

                int resetBtnX = hudX + (HUD_WIDTH - BTN_SIZE) / 2;
                int resetBtnY = hudY + HUD_HEIGHT + 5;
                if (mouseX >= resetBtnX && mouseX <= resetBtnX + BTN_SIZE && mouseY >= resetBtnY && mouseY <= resetBtnY + BTN_SIZE) {

                    ClientCyberwareSettings.hudX = 10;
                    ClientCyberwareSettings.hudY = 10;
                    playClickSound();
                    return true;
                }

                if (mouseX >= hudX && mouseX <= hudX + HUD_WIDTH && mouseY >= hudY && mouseY <= hudY + HUD_HEIGHT) {
                    isDraggingHud = true;
                    dragOffsetX = (int)mouseX - hudX;
                    dragOffsetY = (int)mouseY - hudY;
                    playClickSound();
                    return true;
                }
                return true;
            }

            if (!isColorSettingsOpen && !isHudMoveMode) {
                double angleStep = 2 * Math.PI / parts.size();
                for (int i = 0; i < parts.size(); i++) {
                    ToggleablePart part = parts.get(i);
                    double itemAngle = i * angleStep - Math.PI / 2;
                    int x = centerX + (int) (ITEM_RADIUS * Math.cos(itemAngle));
                    int y = centerY + (int) (ITEM_RADIUS * Math.sin(itemAngle));
                    if (mouseX >= x - 12 && mouseX <= x + 12 && mouseY >= y - 12 && mouseY <= y + 12) {
                        A_PacketHandler.INSTANCE.sendToServer(new ToggleCyberwarePacket(part.slotId));
                        playClickSound();
                        part.item.toggle(part.stack);
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isHudMoveMode && isDraggingHud && button == 0) {
            ClientCyberwareSettings.hudX = (int)mouseX - dragOffsetX;
            ClientCyberwareSettings.hudY = (int)mouseY - dragOffsetY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDraggingHud) {
            isDraggingHud = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record ToggleablePart(int slotId, ItemStack stack, ICyberware item) {
    }
}