package com.maxwell.cyber_ware_port.client.screen.robosurgeon;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.container.RobosurgeonMenu;
import com.maxwell.cyber_ware_port.common.entity.misc.PlayerTempModelState;
import com.maxwell.cyber_ware_port.common.entity.misc.SkeletonPreviewState;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.SurgeryGhostTogglePacket;
import com.maxwell.cyber_ware_port.common.risk.SurgeryAlert;
import com.maxwell.cyber_ware_port.common.risk.SurgeryAnalyzer;
import com.maxwell.cyber_ware_port.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class RobosurgeonScreen extends AbstractContainerScreen<RobosurgeonMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/surgery.png");
    private static final Identifier MARKER_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/marker.png");
    private static final Identifier RED_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/red_slot.png");
    private static final Identifier BLUE_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/blue_slot.png");
    private static final Identifier RISK_ICON = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/risk_icons.png");
    private static final float ANIMATION_DURATION = 2000f;
    private static final int GUI_WIDTH = 175;
    private static final int TOP_HEIGHT = 131;
    private static final float BASE_SCALE = 45f;
    private static final Field slotX, slotY;

    static {
        try {
            slotX = Slot.class.getDeclaredField("x");
            slotX.setAccessible(true);
            slotY = Slot.class.getDeclaredField("y");
            slotY.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final PlayerTempModelState internalPartsPreview = new PlayerTempModelState();
    private final SkeletonPreviewState skeletonPreview = new SkeletonPreviewState();
    private BodyPart selectedPart = BodyPart.NONE;
    private TargetMarker selectedMarker = null;
    private boolean isDraggingModel = false;
    private float viewRotation = 0f;
    private double dragStartX = 0;
    private float rotationStart = 0f;
    private boolean potentialDrag = false;
    private long startTime;
    private float currentScale = 45;
    private float currentOffsetX = 0f;
    private float currentOffsetY = 0f;
    private SurgeryAlert currentAlert = null;

    public RobosurgeonScreen(RobosurgeonMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, GUI_WIDTH, TOP_HEIGHT + 91);
        this.titleLabelX = -1000;
        this.inventoryLabelY = this.imageHeight - 94 + 2;
    }

    private static int[] slots(int start) {
        int[] slots = new int[9];
        for (int i = 0; i < 9; i++) slots[i] = start + i;
        return slots;
    }

    private int getModelBaseX() {
        return this.leftPos + 88 + (int) currentOffsetX;
    }

    private int getModelBaseY() {
        return this.topPos + 120 + (int) currentOffsetY;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateSlotPositions();
    }

    @Override
    protected void init() {
        super.init();
        this.skeletonPreview.entityType = EntityType.SKELETON;
        this.internalPartsPreview.entityType = ModEntities.PLAYER_INTERNAL_PARTS.get();
        this.startTime = System.currentTimeMillis();
        this.addRenderableWidget(new AbstractWidget(this.leftPos + 158, this.topPos + 6, 12, 10, Component.empty()) {
            @Override
            protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                int color = this.isHovered ? 0xFFFFFFFF : 0xFF00FFFF;
                int x = this.getX();
                int y = this.getY();
                graphics.fill(x, y + 1, x + 12, y + 2, color);
                graphics.fill(x, y + 4, x + 12, y + 5, color);
                graphics.fill(x, y + 7, x + 12, y + 8, color);
                if (this.isHovered) {
                    graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font,
                            List.of(Component.translatable("gui.cyber_ware_port.button.view_installed")), mouseX, mouseY);
                }
            }

            @Override
            public void onClick(MouseButtonEvent event, boolean doubleClick) {
                Minecraft.getInstance().setScreen(new InstalledCyberwareScreen(RobosurgeonScreen.this));
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput narration) {
            }
        });
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        updateSlotPositions();
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed < ANIMATION_DURATION && !isDraggingModel) {
            float ease = 1f - (float) Math.pow(1f - (elapsed / ANIMATION_DURATION), 3);
            this.viewRotation = ease * 360f;
        }
        this.skeletonPreview.yRot = this.viewRotation;
        this.skeletonPreview.xRot = 0.0F;
        this.internalPartsPreview.yRot = this.viewRotation;
        this.internalPartsPreview.xRot = 0.0F;
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int guiX = this.leftPos, guiY = this.topPos;
        float spd = 0.1f;
        float targetScale = (selectedPart == BodyPart.NONE ? BASE_SCALE : selectedPart.zoomScale);
        float targetOffsetX = (selectedPart == BodyPart.NONE ? 0 : selectedPart.zoomOffsetX);
        float targetOffsetY = (selectedPart == BodyPart.NONE ? 0 : selectedPart.zoomOffsetY);
        currentScale += (targetScale - currentScale) * spd;
        currentOffsetX += (targetOffsetX - currentOffsetX) * spd;
        currentOffsetY += (targetOffsetY - currentOffsetY) * spd;
        if (this.minecraft.player != null) {
            var data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            int maxTolerance = data.getMaxTolerance(this.minecraft.player);
            this.currentAlert = SurgeryAnalyzer.check(this.menu.slots, maxTolerance);
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX, guiY, 0, 0, GUI_WIDTH, TOP_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX, guiY + TOP_HEIGHT, 0, 131, GUI_WIDTH, 91, 256, 256);
        graphics.nextStratum();
        drawEssenceLogic(graphics, guiX, guiY);
        int centerX = guiX + 88;
        int y0 = guiY + 5;
        int y1 = guiY + TOP_HEIGHT - 10;
        Quaternionf rotation = new Quaternionf().rotationXYZ(0, (float) Math.toRadians(this.viewRotation) + (float) Math.PI, (float) Math.PI);
        boolean showInternal = (this.selectedPart == BodyPart.NONE || this.selectedPart == BodyPart.INTERNAL);
        if (showInternal) {
            boolean isInternalZoom = (this.selectedPart == BodyPart.INTERNAL);
            int intX0, intY0, intX1, intY1;
            float intScale;
            Vector3f intTrans;
            if (isInternalZoom) {
                intX0 = centerX - 70;
                intY0 = y0;
                intX1 = centerX + 70;
                intY1 = y1;
                intScale = currentScale;
                intTrans = new Vector3f(0.0F, 0.2F, 0.0F);
            } else {
                int boxWidth = 80;
                int leftOffset = -50;
                intScale = 40f;
                intTrans = new Vector3f(0.0F, 0.7F, 0.0F);
                intX0 = centerX + leftOffset - (boxWidth / 2);
                intX1 = centerX + leftOffset + (boxWidth / 2);
                intY0 = guiY + 20;
                intY1 = guiY + 100;
                int color = 0xFF00FFFF;
                int bx = intX0 + 21;
                int by = intY0 + 42;
                int bw = 37;
                int bh = 37;
                graphics.outline(bx, by, bw, bh, color);
                int targetX = centerX - 5;
                int lx1 = bx + 8;
                int ly1 = guiY + 50;
                graphics.fill(lx1, ly1, lx1 + 1, by, color);
                graphics.fill(lx1, ly1, targetX, ly1 + 1, color);
            }
            graphics.entity(this.internalPartsPreview, intScale, intTrans, rotation, null, intX0, intY0, intX1, intY1);
        }
        if (this.selectedPart != BodyPart.INTERNAL) {
            float transX = currentOffsetX / currentScale;
            float transY = 1.1F + (currentOffsetY / currentScale);
            Vector3f dynamicTranslation = new Vector3f(transX, transY, 0.0F);
            graphics.entity(this.skeletonPreview, currentScale, dynamicTranslation, rotation, null,
                    centerX - 70, y0, centerX + 70, y1);
        }
        graphics.nextStratum();
        drawScanLine(graphics, guiY);
        if (this.selectedPart == BodyPart.NONE && this.minecraft.player != null) {
            String name = "_" + this.minecraft.player.getName().getString().toUpperCase();
            graphics.text(this.font, Component.literal(name), getModelBaseX() - this.font.width(name) / 2, guiY + TOP_HEIGHT - 15, 0xFF00FFFF, true);
        }
        if (this.currentAlert != null) {
            int iconX = guiX + 156;
            int iconY = guiY + 20;
            graphics.blit(RenderPipelines.GUI_TEXTURED, RISK_ICON, iconX, iconY, 0, 0, 16, 16, 16, 16, this.currentAlert.color() | 0xFF000000);

            if (mouseX >= iconX && mouseX < iconX + 16 && mouseY >= iconY && mouseY < iconY + 16) {
                graphics.setComponentTooltipForNextFrame(this.font, List.of(this.currentAlert.message()), mouseX, mouseY);
            }
        }

        drawMarkersAndAlerts(graphics, guiX, guiY, mouseX, mouseY);
        drawMarkerSlotBackgrounds(graphics, mouseX, mouseY);
        super.extractContents(graphics, mouseX, mouseY, a);
    }
    private int calculateTotalEssenceCost() {
        int cost = 0;
        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            ItemStack stack = this.menu.getSlot(i).getItem();
            if (!stack.isEmpty()) {
                if (stack.getOrDefault(CyberWare.REMOVAL_COMPONENT.get(), false)) {
                    continue;
                }

                ICyberware cw = CyberwareAPI.getCyberware(stack);
                if (cw != null) {
                    cost += cw.getEssenceCost(stack) * stack.getCount();
                }
            }
        }
        return cost;
    }

    @Override
    protected void renderSlotContents(GuiGraphicsExtractor graphics, ItemStack itemStack, Slot slot, @Nullable String itemCount) {
        boolean isBeingRemoved = !itemStack.isEmpty() && itemStack.getOrDefault(CyberWare.REMOVAL_COMPONENT.get(), false);

        int x = slot.x;
        int y = slot.y;
        int seed = x + y * this.imageWidth;

        if (slot.isFake()) {
            graphics.fakeItem(itemStack, x, y, seed);
        } else {
            graphics.item(itemStack, x, y, seed);
        }

        var font = IClientItemExtensions.of(itemStack).getFont(itemStack, IClientItemExtensions.FontContext.ITEM_COUNT);
        graphics.itemDecorations(font != null ? font : this.font, itemStack, x, y, itemCount);

        if (isBeingRemoved) {

            graphics.fill(x, y, x + 16, y + 16, 0x80000000);
        }
    }


    private void drawEssenceLogic(GuiGraphicsExtractor graphics, int guiX, int guiY) {
        if (this.minecraft.player == null) return;
        var data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        int maxTolerance = data.getMaxTolerance(this.minecraft.player);
        int currentTolerance = data.getTolerance(this.minecraft.player);
        int projectedCost = calculateTotalEssenceCost();
        int projectedEssence = maxTolerance - projectedCost;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 5, guiY + 4, 211, 61, 8, 48, 256, 256);
        drawEssenceBar(graphics, projectedEssence, maxTolerance, guiX + 5, guiY + 4, 8, 48);
        int color = (projectedEssence < 0) ? 0xFFAA0000 : (projectedEssence < 20 ? 0xFFFF5555 : 0xFF00FFFF);
        String toleranceText = projectedEssence + " / " + maxTolerance;
        graphics.text(this.font, toleranceText, guiX + 18, guiY + 6, color, true);
        if (currentTolerance > projectedEssence) {
            float time = (System.currentTimeMillis() % 1000) / 1000f;
            if (Math.sin(time * 2 * Math.PI) > 0) {
                drawEssenceBar(graphics, currentTolerance, maxTolerance, guiX + 5, guiY + 4, 8, 48);
            }
        }
    }

    private void drawEssenceBar(GuiGraphicsExtractor graphics, int essence, int maxEssence, int x, int y, int w, int h) {
        int val = Math.max(0, essence);
        int danger = (int) (maxEssence * 0.25f);
        int rH = (int) (h * ((float) Math.min(val, danger) / maxEssence));
        int bH = (int) (h * ((float) Math.max(0, val - danger) / maxEssence));
        if (rH > 0)
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + (h - rH), 220, 61 + (48 - rH), w, rH, 256, 256);
        if (bH > 0)
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + (h - rH - bH), 176, 61 + (48 - (rH + bH)), w, bH, 256, 256);
    }

    private void drawScanLine(GuiGraphicsExtractor graphics, int guiY) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed < ANIMATION_DURATION) {
            float r = Math.min(elapsed / ANIMATION_DURATION, 1f);
            float ease = 1f - (float) Math.pow(1f - r, 3);
            int scanY = (guiY + 15) + (int) (100 * ease);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + 48, scanY, 176, 110, 80, 1, 256, 256);
        }
    }

    private void drawMarkersAndAlerts(GuiGraphicsExtractor graphics, int guiX, int guiY, int mouseX, int mouseY) {
        if (this.selectedPart != BodyPart.NONE && this.selectedMarker == null) {
            float radRot = (float) Math.toRadians(this.viewRotation) + (float) Math.PI;
            float sin = (float) Math.sin(radRot), cos = (float) Math.cos(radRot);
            float scaleFactor = currentScale / 16f;
            int baseX = getModelBaseX(), baseY = getModelBaseY();
            if (this.selectedPart == BodyPart.INTERNAL) {
                baseX = guiX + 88;
                baseY = guiY + 65;
            }
            for (TargetMarker marker : this.selectedPart.markers) {
                float screenX = (marker.modelX() * cos) - (marker.modelZ() * sin);
                int markerX = baseX + (int) (screenX * scaleFactor) - 8;
                int markerY = baseY - (int) (marker.modelY() * scaleFactor) - 8;
                graphics.blit(RenderPipelines.GUI_TEXTURED, MARKER_TEXTURE, markerX, markerY, 0, 0, 16, 16, 16, 16, 0xCCFFFFFF);
                if (mouseX >= markerX && mouseX < markerX + 16 && mouseY >= markerY && mouseY < markerY + 16) {
                    graphics.outline(markerX - 1, markerY - 1, 18, 18, 0xFFFFFFFF);
                    graphics.setTooltipForNextFrame(marker.name(), mouseX, mouseY);
                }
            }
        }
    }

    private void drawMarkerSlotBackgrounds(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.selectedMarker != null && this.minecraft.player != null) {
            int slotCount = this.selectedMarker.relatedSlots().length;
            int uiWidth = (18 * slotCount) + (2 * (slotCount - 1));
            int uiX = this.leftPos + (GUI_WIDTH - uiWidth) / 2;

            var data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            var installed = data.getInstalledCyberware();

            for (int i = 0; i < slotCount; i++) {
                int slotX = uiX + (i * 20);
                int slotId = this.selectedMarker.relatedSlots()[i];
                graphics.blit(RenderPipelines.GUI_TEXTURED, BLUE_SLOT_TEXTURE, slotX - 1, this.topPos + 104, 0, 0, 18, 18, 18, 18);
                int redY = this.topPos + 79;
                graphics.blit(RenderPipelines.GUI_TEXTURED, RED_SLOT_TEXTURE, slotX - 1, redY, 0, 0, 18, 18, 18, 18);
                ItemStack installedStack = installed.getResource(slotId).toStack(installed.getAmountAsInt(slotId));

                if (!installedStack.isEmpty()) {
                    int itemX = slotX;
                    int itemY = redY + 1;
                    graphics.item(installedStack, itemX, itemY);
                    var font = IClientItemExtensions.of(installedStack).getFont(installedStack, IClientItemExtensions.FontContext.ITEM_COUNT);
                    graphics.itemDecorations(font != null ? font : this.font, installedStack, itemX, itemY, null);
                    if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
                        graphics.setTooltipForNextFrame(this.font, Screen.getTooltipFromItem(this.minecraft, installedStack), installedStack.getTooltipImage(), mouseX, mouseY);
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x();
        double my = event.y();
        if (mx >= this.leftPos + 158 && mx < this.leftPos + 158 + 12 &&
                my >= this.topPos + 6 && my < this.topPos + 6 + 10) {
            if (super.mouseClicked(event, doubleClick)) return true;
        }
        if (event.button() == 0) {
            for (Slot slot : this.menu.slots) {
                if (mx >= this.leftPos + slot.x && mx < this.leftPos + slot.x + 16 &&
                        my >= this.topPos + slot.y && my < this.topPos + slot.y + 16) {
                    if (slot.index < RobosurgeonBlockEntity.TOTAL_SLOTS) {
                        ItemStack stack = slot.getItem();
                        if (stack.isEmpty() || stack.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                            ClientPacketDistributor.sendToServer(new SurgeryGhostTogglePacket(this.menu.blockEntity.getBlockPos(), slot.index));
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            return true;
                        }
                    }
                    return super.mouseClicked(event, doubleClick);
                }
            }
            if (this.selectedPart != BodyPart.NONE && this.selectedMarker == null) {
                if (checkMarkerClick(mx, my)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.2F));
                    return true;
                }
            }
            if (mx >= this.leftPos && mx < this.leftPos + GUI_WIDTH &&
                    my >= this.topPos && my < this.topPos + TOP_HEIGHT) {
                this.potentialDrag = true;
                this.dragStartX = mx;
                this.rotationStart = this.viewRotation;
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private boolean checkMarkerClick(double mx, double my) {
        float radRot = (float) Math.toRadians(this.viewRotation) + (float) Math.PI;
        float sin = (float) Math.sin(radRot), cos = (float) Math.cos(radRot);
        float scaleFactor = currentScale / 16f;
        int baseX = getModelBaseX(), baseY = getModelBaseY();
        if (this.selectedPart == BodyPart.INTERNAL) {
            baseX = this.leftPos + 88;
            baseY = this.topPos + 65;
        }
        for (TargetMarker marker : this.selectedPart.markers) {
            float screenX = (marker.modelX() * cos) - (marker.modelZ() * sin);
            int markerX = baseX + (int) (screenX * scaleFactor) - 8;
            int markerY = baseY - (int) (marker.modelY() * scaleFactor) - 8;
            if (mx >= markerX && mx < markerX + 16 && my >= markerY && my < markerY + 16) {
                this.selectedMarker = marker;
                updateSlotPositions();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (this.potentialDrag && event.button() == 0) {
            this.viewRotation = this.rotationStart - (float) (event.x() - this.dragStartX);
            this.isDraggingModel = true;
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            if (this.isDraggingModel) {
                this.isDraggingModel = false;
                this.potentialDrag = false;
                return true;
            } else if (this.potentialDrag) {
                if (this.selectedPart == BodyPart.NONE) {
                    handlePartSelection(event.x(), event.y());
                } else {
                    this.selectedPart = BodyPart.NONE;
                    this.selectedMarker = null;
                    updateSlotPositions();
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.8F));
                }
                this.potentialDrag = false;
                return true;
            }
        }
        return super.mouseReleased(event);
    }

    private boolean handlePartSelection(double mouseX, double mouseY) {
        float radRot = (float) Math.toRadians(this.viewRotation) + (float) Math.PI;
        float sin = (float) Math.sin(radRot);
        float cos = (float) Math.cos(radRot);
        float scaleFactor = currentScale / 45f;
        int baseX = getModelBaseX() + (int) currentOffsetX;
        int baseY = getModelBaseY() + (int) currentOffsetY;
        for (BodyPart part : BodyPart.values()) {
            if (part == BodyPart.NONE) continue;
            float rotatedX = (part.hitX * cos) - (part.hitZ * sin);
            int hX = baseX + (int) (rotatedX * scaleFactor);
            int hY = baseY + (int) (part.hitY * scaleFactor);
            int halfW = (int) ((part.hitW / 2.0) * (currentScale / BASE_SCALE));
            int halfH = (int) ((part.hitH / 2.0) * (currentScale / BASE_SCALE));
            if (mouseX >= (hX - halfW) && mouseX <= (hX + halfW) &&
                    mouseY >= (hY - halfH) && mouseY <= (hY + halfH)) {
                this.selectedPart = part;
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                updateSlotPositions();
                return true;
            }
        }
        return false;
    }

    private void updateSlotPositions() {
        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            if (i < this.menu.slots.size()) setSlotPos(this.menu.getSlot(i), 20000, 20000);
        }
        if (this.selectedMarker != null) {
            int slotCount = this.selectedMarker.relatedSlots().length;
            int uiWidth = (18 * slotCount) + (2 * (slotCount - 1));
            int uiX = (this.imageWidth - uiWidth) / 2;
            for (int i = 0; i < slotCount; i++) {
                int slotId = this.selectedMarker.relatedSlots()[i];
                if (slotId < this.menu.slots.size()) setSlotPos(this.menu.getSlot(slotId), uiX + (i * 20), 105);
            }
        }
    }

    private void setSlotPos(Slot slot, int x, int y) {
        try {
            slotX.set(slot, x);
            slotY.set(slot, y);
        } catch (Exception e) {
        }
    }

    private enum BodyPart {
        HEAD(0, -85, 0, 30, 30, 0, 80, 120f, List.of(
                new TargetMarker(Component.literal("Left Eye"), 2f, 17.5f, -3.4f, slots(RobosurgeonBlockEntity.SLOT_EYES)),
                new TargetMarker(Component.literal("Right Eye"), -2f, 17.5f, -3.4f, slots(RobosurgeonBlockEntity.SLOT_EYES)),
                new TargetMarker(Component.literal("Brain"), -0.13f, 19.56f, 1.52f, slots(RobosurgeonBlockEntity.SLOT_BRAIN)))),
        TORSO(0, -54, 0, 24, 32, 0, 20, 120f, List.of(
                new TargetMarker(Component.literal("Heart"), 0f, 12f, -0.5f, slots(RobosurgeonBlockEntity.SLOT_HEART)),
                new TargetMarker(Component.literal("Left Lungs"), 2.3f, 10f, 0.5f, slots(RobosurgeonBlockEntity.SLOT_LUNGS)),
                new TargetMarker(Component.literal("Right Lungs"), -2.3f, 10f, 0.5f, slots(RobosurgeonBlockEntity.SLOT_LUNGS)),
                new TargetMarker(Component.literal("Stomach"), 0.0f, 6f, -1.5f, slots(RobosurgeonBlockEntity.SLOT_STOMACH)))),
        ARM_LEFT(25, -54, 0, 20, 34, 60, 30, 120f, List.of(
                new TargetMarker(Component.literal("Left Arm"), 5.5f, 14.0f, 0, slots(RobosurgeonBlockEntity.SLOT_ARMS)),
                new TargetMarker(Component.literal("Left Hand"), 5.5f, 6.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_HANDS)))),
        ARM_RIGHT(-25, -54, 0, 20, 34, -60, 30, 120f, List.of(
                new TargetMarker(Component.literal("Right Arm"), -5.5f, 14.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_ARMS)),
                new TargetMarker(Component.literal("Right Hand"), -5.5f, 6.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_HANDS)))),
        LEG_LEFT(10, -20, 0, 14, 40, 40, -50, 120f, List.of(
                new TargetMarker(Component.literal("Left Leg"), 2f, 2.5f, 0f, slots(RobosurgeonBlockEntity.SLOT_LEGS)),
                new TargetMarker(Component.literal("Left Foot"), 2f, -2.5f, 0f, slots(RobosurgeonBlockEntity.SLOT_BOOTS)))),
        LEG_RIGHT(-10, -20, 0, 14, 40, -40, -50, 120f, List.of(
                new TargetMarker(Component.literal("Right Leg"), -2f, 2.5f, 0f, slots(RobosurgeonBlockEntity.SLOT_LEGS)),
                new TargetMarker(Component.literal("Right Foot"), -2f, -2.5f, 0f, slots(RobosurgeonBlockEntity.SLOT_BOOTS)))),
        INTERNAL(60, -40, 0, 35, 35, 0, 0, 150f, List.of(
                new TargetMarker(Component.literal("Skin"), -3.0f, 1.9f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_SKIN)),
                new TargetMarker(Component.literal("Muscle"), -0, 0.0f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_MUSCLE)),
                new TargetMarker(Component.literal("Bone"), 3.0f, -2.0f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_BONES)))),
        NONE(0, 0, 0, 0, 0, 0, 0, 45f, List.of());
        final int hitX, hitY, hitZ, hitW, hitH, zoomOffsetX, zoomOffsetY;
        final float zoomScale;
        final List<TargetMarker> markers;

        BodyPart(int hX, int hY, int hZ, int hW, int hH, int zX, int zY, float zS, List<TargetMarker> m) {
            this.hitX = hX;
            this.hitY = hY;
            this.hitZ = hZ;
            this.hitW = hW;
            this.hitH = hH;
            this.zoomOffsetX = zX;
            this.zoomOffsetY = zY;
            this.zoomScale = zS;
            this.markers = m;
        }
    }

    public record TargetMarker(Component name, float modelX, float modelY, float modelZ, int[] relatedSlots) {
    }
}