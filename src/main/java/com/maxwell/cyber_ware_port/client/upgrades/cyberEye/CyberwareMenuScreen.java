package com.maxwell.cyber_ware_port.client.upgrades.cybereye;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.client.ClientCyberwareSettings;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.ToggleCyberwarePacket;
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
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.ArrayList;
import java.util.List;

public class CyberwareMenuScreen extends Screen {
    private static final Identifier HUD_COLOR_ICON = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_color.png");
    private static final Identifier HUD_POS_ICON = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_pos.png");
    private static final Identifier HUD_RESET_ICON = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/hud_reset.png");
    private static final float INNER_RADIUS = 40.0f;
    private static final float OUTER_RADIUS = 100.0f;
    private static final float ITEM_RADIUS = (INNER_RADIUS + OUTER_RADIUS) / 2.0f;
    private final List<ToggleablePart> parts = new ArrayList<>();

    public boolean isHudMoveMode = false;
    private boolean isDraggingHud = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    // バーのドラッグ位置情報
    private boolean isDraggingBar = false;
    private int dragBarOffsetX = 0;
    private int dragBarOffsetY = 0;

    private static final int HUD_WIDTH = 80;
    private static final int HUD_HEIGHT = 25;

    // スリム化したバーのプレビュー時サイズ
    private static final int BAR_WIDTH = 26;
    private static final int BAR_HEIGHT = 210;

    private static final int BTN_SIZE = 16;
    private int btnX;
    private int btnY;
    public boolean isColorSettingsOpen = false;
    private EditBox hexInput;
    private static final int[] PRESET_COLORS = {
            0xFF00FFFF, 0xFF00FF00, 0xFFFF0000, 0xFFFFFF00,
            0xFFFFFFFF, 0xFFFF00FF, 0xFF0000FF, 0xFFFF8000
    };

    public CyberwareMenuScreen() {
        super(Component.translatable("gui.cyber_ware_port.menu"));
    }

    private static int applyAlpha(int color, float alpha) {
        int a = (int) (((color >> 24) & 0xFF) * alpha);
        return (a << 24) | (color & 0xFFFFFF);
    }

    @Override
    protected void init() {
        super.init();
        parts.clear();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.btnX = centerX + 120;
        this.btnY = centerY - 60;

        if (this.minecraft != null && this.minecraft.player != null) {
            CyberwareUserData data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            ItemStacksResourceHandler handler = data.getInstalledCyberware();
            for (int i = 0; i < handler.size(); i++) {
                ItemStack stack = handler.getResource(i).toStack(handler.getAmountAsInt(i));
                if (stack.isEmpty()) continue;
                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (cw != null && cw.canToggle(stack)) {
                    parts.add(new ToggleablePart(i, stack, cw));
                }
            }
        }
        int boxWidth = 80;
        int boxHeight = 20;
        this.hexInput = new EditBox(this.font, centerX - boxWidth / 2, centerY + 30, boxWidth, boxHeight, Component.literal("Color Hex"));
        this.hexInput.setMaxLength(8);
        this.hexInput.setValue(ClientCyberwareSettings.getHudColorAsHex());
        this.hexInput.setBordered(true);
        this.hexInput.setVisible(false);
        this.hexInput.setResponder(ClientCyberwareSettings::setHudColorFromHex);
        this.addRenderableWidget(this.hexInput);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isColorSettingsOpen && this.hexInput.isFocused()) {
            if (this.hexInput.keyPressed(event)) return true;
            if (event.isEscape()) {
                toggleColorSettings();
                return true;
            }
        }
        if (this.isHudMoveMode) {
            if (event.isEscape() || event.key() == this.minecraft.options.keyInventory.getKey().getValue()) {
                this.isHudMoveMode = false;
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        if (isColorSettingsOpen || isHudMoveMode) {
            g.fillGradient(0, 0, width, height, 0x80000000, 0x80000000);
        }
        if (!isColorSettingsOpen && !isHudMoveMode) {
            renderRadialMenu(g, centerX, centerY, mouseX, mouseY);
        }
        renderHudPreview(g, mouseX, mouseY);
        if (!isHudMoveMode) {
            renderIconButton(g, HUD_COLOR_ICON, btnX, btnY + 20, mouseX, mouseY, "HUD Color Settings");
            if (!isColorSettingsOpen) {
                renderIconButton(g, HUD_POS_ICON, btnX, btnY, mouseX, mouseY, "Move HUD Position");
            }
        }
        if (isColorSettingsOpen) {
            renderColorSettings(g, centerX, centerY, mouseX, mouseY);
        }
        if (isHudMoveMode) {
            g.text(this.font, Component.literal("HUD MOVE MODE"), centerX - this.font.width("HUD MOVE MODE") / 2, 40, 0xFF00FF00, true);
            g.text(this.font, Component.literal("Drag HUD elements to move / Press ESC to finish"), centerX - this.font.width("Drag HUD elements to move / Press ESC to finish") / 2, 55, 0xFFFFFFFF, true);
        }
    }

    private void drawDottedCircle(GuiGraphicsExtractor g, int centerX, int centerY, float radius, int color, int step) {
        int points = (int) (2 * Math.PI * radius);
        if (points <= 0) return;
        for (int i = 0; i < points; i += step) {
            double angle = (i * 2 * Math.PI) / points;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            g.fill(RenderPipelines.GUI, x, y, x + 1, y + 1, color);
        }
    }

    private void drawRadialNotches(GuiGraphicsExtractor g, int centerX, int centerY, float innerRadius, float outerRadius, int color) {
        int notchCount = 24;
        float notchLength = 5.0f;
        int notchColor = applyAlpha(color, 0.4f);

        for (int i = 0; i < notchCount; i++) {
            double angle = Math.toRadians((i * 360.0) / notchCount);
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            // 内側目盛り（内円から外へ）
            for (float r = innerRadius; r < innerRadius + notchLength; r += 1.0f) {
                int x = centerX + (int) (r * cos);
                int y = centerY + (int) (r * sin);
                g.fill(RenderPipelines.GUI, x, y, x + 1, y + 1, notchColor);
            }

            // 外側目盛り（外円から内へ）
            for (float r = outerRadius - notchLength; r < outerRadius; r += 1.0f) {
                int x = centerX + (int) (r * cos);
                int y = centerY + (int) (r * sin);
                g.fill(RenderPipelines.GUI, x, y, x + 1, y + 1, notchColor);
            }
        }
    }

    // デジタル・レティクル（円環）全体を構成するメソッド
    private void drawRadialRing(GuiGraphicsExtractor g, int centerX, int centerY, float innerRadius, float outerRadius, int color) {
        // 内円と外円を美しい点線（2ドットおき）で描画
        drawDottedCircle(g, centerX, centerY, innerRadius, color, 2);
        drawDottedCircle(g, centerX, centerY, outerRadius, color, 2);

        // 背景のうっすらとしたSFグリッドサークル（8pxおき、8ドットおきの非常に粗い点線。超軽量ながらホログラム感が出ます）
        int bgColor = applyAlpha(color, 0.05f);
        for (float r = innerRadius + 8; r < outerRadius; r += 8.0f) {
            drawDottedCircle(g, centerX, centerY, r, bgColor, 8);
        }

        // スタイリッシュな目盛り線（ノッチ）を描画
        drawRadialNotches(g, centerX, centerY, innerRadius, outerRadius, color);
    }

    private void renderRadialMenu(GuiGraphicsExtractor g, int centerX, int centerY, int mouseX, int mouseY) {
        int hudColor = ClientCyberwareSettings.hudColor;

        // 1. 同心円状のSFデジタル・レティクルを描画
        drawRadialRing(g, centerX, centerY, INNER_RADIUS, OUTER_RADIUS, hudColor);

        // 2. ホログラフィックな十字インジケータ（クロスヘア・ノッチ）の描画
        int notchLen = 15;
        g.fill(RenderPipelines.GUI, centerX - (int)OUTER_RADIUS, centerY, centerX - (int)OUTER_RADIUS + notchLen, centerY + 1, applyAlpha(hudColor, 0.35f));
        g.fill(RenderPipelines.GUI, centerX + (int)OUTER_RADIUS - notchLen, centerY, centerX + (int)OUTER_RADIUS, centerY + 1, applyAlpha(hudColor, 0.35f));
        g.fill(RenderPipelines.GUI, centerX, centerY - (int)OUTER_RADIUS, centerX + 1, centerY - (int)OUTER_RADIUS + notchLen, applyAlpha(hudColor, 0.35f));
        g.fill(RenderPipelines.GUI, centerX, centerY + (int)OUTER_RADIUS - notchLen, centerX + 1, centerY + (int)OUTER_RADIUS, applyAlpha(hudColor, 0.35f));

        // 3. レティクルの中心コア・ドット
        g.fill(RenderPipelines.GUI, centerX - 1, centerY - 1, centerX + 2, centerY + 2, applyAlpha(hudColor, 0.5f));

        if (parts.isEmpty()) return;
        double angleStep = 2 * Math.PI / parts.size();
        for (int i = 0; i < parts.size(); i++) {
            ToggleablePart part = parts.get(i);
            double itemAngle = i * angleStep - Math.PI / 2;
            int x = centerX + (int) (ITEM_RADIUS * Math.cos(itemAngle));
            int y = centerY + (int) (ITEM_RADIUS * Math.sin(itemAngle));

            // 修正：Capability データから最新の ItemStack をその場で取得し、リアルタイムに反映
            ItemStack currentStack = ItemStack.EMPTY;
            if (this.minecraft.player != null) {
                CyberwareUserData data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                ItemStacksResourceHandler handler = data.getInstalledCyberware();
                if (part.slotId < handler.size()) {
                    currentStack = handler.getResource(part.slotId).toStack(handler.getAmountAsInt(part.slotId));
                }
            }
            if (currentStack.isEmpty()) continue;

            boolean isActive = part.item.isActive(currentStack);
            boolean isHovered = (mouseX >= x - 12 && mouseX <= x + 12 && mouseY >= y - 12 && mouseY <= y + 12);
            if (isHovered) {
                Component statusText = isActive ? Component.translatable("cyberware.gui.active") : Component.translatable("cyberware.gui.inactive");
                g.text(this.font, statusText, x - this.font.width(statusText) / 2, y - 20, 0xFFFFFF00, true);
                g.setTooltipForNextFrame(this.font, currentStack, mouseX, mouseY);
            }
            g.item(currentStack, x - 8, y - 8);
            int outlineColor = isActive ? 0xFF00FF00 : 0xFFFF0000;
            g.outline(x - 10, y - 10, 20, 20, outlineColor);
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
        int hudX = ClientCyberwareSettings.hudX;
        int hudY = ClientCyberwareSettings.hudY;
        int barX = ClientCyberwareSettings.barX;
        int barY = ClientCyberwareSettings.barY;

        if (this.minecraft.player != null) {
            CyberwareUserData data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            CyberwareHudOverlay.renderBatteryHud(g, this.minecraft, data, hudX, hudY, 0, 0, 1.0f);
        }

        if (isHudMoveMode) {
            float[] rgba = ClientCyberwareSettings.getColorFloats();
            int color = ARGB.color((int) (rgba[3] * 255), (int) (rgba[0] * 255), (int) (rgba[1] * 255), (int) (rgba[2] * 255));

            // ------------------ 1. バッテリー ------------------
            g.outline(hudX - 1, hudY - 1, HUD_WIDTH + 2, HUD_HEIGHT + 2, 0xFF00FF00);
            g.text(this.font, "BATTERY", hudX + (HUD_WIDTH - this.font.width("BATTERY")) / 2, hudY - 10, 0xFF00FF00, true);
            if (isDraggingHud || isInside(mouseX, mouseY, hudX, hudY, HUD_WIDTH, HUD_HEIGHT)) {
                g.outline(hudX - 1, hudY - 1, HUD_WIDTH + 2, HUD_HEIGHT + 2, 0xFFFFFFFF);
            }

            // バッテリー専用リセット
            int batResetBtnX = hudX + (HUD_WIDTH - BTN_SIZE) / 2;
            int batResetBtnY = hudY + HUD_HEIGHT + 5;
            g.blit(RenderPipelines.GUI_TEXTURED, HUD_RESET_ICON, batResetBtnX, batResetBtnY, 0, 0, BTN_SIZE, BTN_SIZE, BTN_SIZE, BTN_SIZE, color);
            if (isInside(mouseX, mouseY, batResetBtnX, batResetBtnY, BTN_SIZE, BTN_SIZE)) {
                g.outline(batResetBtnX - 1, batResetBtnY - 1, BTN_SIZE + 2, BTN_SIZE + 2, 0xFFFFFFFF);
                g.setTooltipForNextFrame(Component.literal("Reset Battery HUD"), mouseX, mouseY);
            }

            // ------------------ 2. ステータスバー ------------------
            g.outline(barX - 1, barY - 1, BAR_WIDTH + 2, BAR_HEIGHT + 2, 0xFF00FF00);
            g.text(this.font, "STATUS BAR", barX + (BAR_WIDTH - this.font.width("STATUS BAR")) / 2, barY - 10, 0xFF00FF00, true);
            if (isDraggingBar || isInside(mouseX, mouseY, barX, barY, BAR_WIDTH, BAR_HEIGHT)) {
                g.outline(barX - 1, barY - 1, BAR_WIDTH + 2, BAR_HEIGHT + 2, 0xFFFFFFFF);
            }

            // ステータスバー専用リセット
            int barResetBtnX = barX + (BAR_WIDTH - BTN_SIZE) / 2;
            int barResetBtnY = barY + BAR_HEIGHT + 5;
            g.blit(RenderPipelines.GUI_TEXTURED, HUD_RESET_ICON, barResetBtnX, barResetBtnY, 0, 0, BTN_SIZE, BTN_SIZE, BTN_SIZE, BTN_SIZE, color);
            if (isInside(mouseX, mouseY, barResetBtnX, barResetBtnY, BTN_SIZE, BTN_SIZE)) {
                g.outline(barResetBtnX - 1, barResetBtnY - 1, BTN_SIZE + 2, BTN_SIZE + 2, 0xFFFFFFFF);
                g.setTooltipForNextFrame(Component.literal("Reset Status Bar"), mouseX, mouseY);
            }

            // 方向選択ボタン
            int ltrBtnX = barResetBtnX - 20;
            int ltrBtnY = barResetBtnY;
            int rltBtnX = barResetBtnX + 20;
            int rltBtnY = barResetBtnY;

            int hudColorInt = ClientCyberwareSettings.hudColor;
            boolean ltrSelected = ClientCyberwareSettings.slideDirection == 0;
            boolean rltSelected = ClientCyberwareSettings.slideDirection == 1;

            // 左から右へのスライド設定 (→)
            g.fill(RenderPipelines.GUI, ltrBtnX, ltrBtnY, ltrBtnX + BTN_SIZE, ltrBtnY + BTN_SIZE, applyAlpha(0x40000000 | (hudColorInt & 0xFFFFFF), 1.0f));
            g.outline(ltrBtnX, ltrBtnY, BTN_SIZE, BTN_SIZE, ltrSelected ? 0xFFFFFFFF : (0x60000000 | (hudColorInt & 0xFFFFFF)));
            g.text(this.font, "→", ltrBtnX + 4, ltrBtnY + 4, ltrSelected ? 0xFFFFFFFF : 0xFFAAAAAA, false);
            if (isInside(mouseX, mouseY, ltrBtnX, ltrBtnY, BTN_SIZE, BTN_SIZE)) {
                g.outline(ltrBtnX - 1, ltrBtnY - 1, BTN_SIZE + 2, BTN_SIZE + 2, 0xFFFFFFFF);
                g.setTooltipForNextFrame(Component.literal("Slide: Left to Right"), mouseX, mouseY);
            }

            // 右から左へのスライド設定 (←)
            g.fill(RenderPipelines.GUI, rltBtnX, rltBtnY, rltBtnX + BTN_SIZE, rltBtnY + BTN_SIZE, applyAlpha(0x40000000 | (hudColorInt & 0xFFFFFF), 1.0f));
            g.outline(rltBtnX, rltBtnY, BTN_SIZE, BTN_SIZE, rltSelected ? 0xFFFFFFFF : (0x60000000 | (hudColorInt & 0xFFFFFF)));
            g.text(this.font, "←", rltBtnX + 4, rltBtnY + 4, rltSelected ? 0xFFFFFFFF : 0xFFAAAAAA, false);
            if (isInside(mouseX, mouseY, rltBtnX, rltBtnY, BTN_SIZE, BTN_SIZE)) {
                g.outline(rltBtnX - 1, rltBtnY - 1, BTN_SIZE + 2, BTN_SIZE + 2, 0xFFFFFFFF);
                g.setTooltipForNextFrame(Component.literal("Slide: Right to Left"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (buttonClick(event, doubleClick)) return true;
        return super.mouseClicked(event, doubleClick);
    }

    private boolean buttonClick(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (button == 0) {
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            if (!isHudMoveMode) {
                int colorBtnX = btnX;
                int colorBtnY = btnY + 20;
                if (isInside(mouseX, mouseY, colorBtnX, colorBtnY, BTN_SIZE, BTN_SIZE)) {
                    toggleColorSettings();
                    playClickSound();
                    return true;
                }
                if (!isColorSettingsOpen && isInside(mouseX, mouseY, btnX, btnY, BTN_SIZE, BTN_SIZE)) {
                    isHudMoveMode = true;
                    playClickSound();
                    return true;
                }
            }
            if (isColorSettingsOpen) {
                if (this.hexInput.mouseClicked(event, false)) return true;
                int swatchSize = 20, gap = 4;
                int totalWidth = (swatchSize * PRESET_COLORS.length) + (gap * (PRESET_COLORS.length - 1));
                int startX = centerX - totalWidth / 2;
                for (int i = 0; i < PRESET_COLORS.length; i++) {
                    int x = startX + (swatchSize + gap) * i;
                    if (isInside(mouseX, mouseY, x, centerY - 15, swatchSize, swatchSize)) {
                        ClientCyberwareSettings.hudColor = PRESET_COLORS[i];
                        this.hexInput.setValue(ClientCyberwareSettings.getHudColorAsHex());
                        playClickSound();
                        return true;
                    }
                }
            }
            if (isHudMoveMode) {
                int hudX = ClientCyberwareSettings.hudX, hudY = ClientCyberwareSettings.hudY;
                int barX = ClientCyberwareSettings.barX, barY = ClientCyberwareSettings.barY;

                int batResetBtnX = hudX + (HUD_WIDTH - BTN_SIZE) / 2;
                int batResetBtnY = hudY + HUD_HEIGHT + 5;

                int barResetBtnX = barX + (BAR_WIDTH - BTN_SIZE) / 2;
                int barResetBtnY = barY + BAR_HEIGHT + 5;

                int ltrBtnX = barResetBtnX - 20;
                int ltrBtnY = barResetBtnY;
                int rltBtnX = barResetBtnX + 20;
                int rltBtnY = barResetBtnY;

                // 1. バッテリーリセット
                if (isInside(mouseX, mouseY, batResetBtnX, batResetBtnY, BTN_SIZE, BTN_SIZE)) {
                    ClientCyberwareSettings.hudX = 10;
                    ClientCyberwareSettings.hudY = 10;
                    playClickSound();
                    return true;
                }

                // 2. ステータスバーリセット
                if (isInside(mouseX, mouseY, barResetBtnX, barResetBtnY, BTN_SIZE, BTN_SIZE)) {
                    ClientCyberwareSettings.barX = 10;
                    ClientCyberwareSettings.barY = 40;
                    playClickSound();
                    return true;
                }

                // 3. 左から右スライド判定 (→)
                if (isInside(mouseX, mouseY, ltrBtnX, ltrBtnY, BTN_SIZE, BTN_SIZE)) {
                    ClientCyberwareSettings.slideDirection = 0;
                    playClickSound();
                    return true;
                }

                // 4. 右から左スライド判定 (←)
                if (isInside(mouseX, mouseY, rltBtnX, rltBtnY, BTN_SIZE, BTN_SIZE)) {
                    ClientCyberwareSettings.slideDirection = 1;
                    playClickSound();
                    return true;
                }

                // バッテリードラッグ
                if (isInside(mouseX, mouseY, hudX, hudY, HUD_WIDTH, HUD_HEIGHT)) {
                    isDraggingHud = true;
                    dragOffsetX = (int) mouseX - hudX;
                    dragOffsetY = (int) mouseY - hudY;
                    return true;
                }

                // 非稼働ステータスバードラッグ
                if (isInside(mouseX, mouseY, barX, barY, BAR_WIDTH, BAR_HEIGHT)) {
                    isDraggingBar = true;
                    dragBarOffsetX = (int) mouseX - barX;
                    dragBarOffsetY = (int) mouseY - barY;
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
                    if (isInside(mouseX, mouseY, x - 12, y - 12, 24, 24)) {
                        ClientPacketDistributor.sendToServer(new ToggleCyberwarePacket(part.slotId));
                        playClickSound();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isInside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (isHudMoveMode) {
            if (isDraggingHud) {
                ClientCyberwareSettings.hudX = (int) event.x() - dragOffsetX;
                ClientCyberwareSettings.hudY = (int) event.y() - dragOffsetY;
                return true;
            }
            if (isDraggingBar) {
                ClientCyberwareSettings.barX = (int) event.x() - dragBarOffsetX;
                ClientCyberwareSettings.barY = (int) event.y() - dragBarOffsetY;
                return true;
            }
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            isDraggingHud = false;
            isDraggingBar = false;
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