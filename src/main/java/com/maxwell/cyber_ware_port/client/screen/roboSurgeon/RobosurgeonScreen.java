package com.maxwell.cyber_ware_port.client.screen.robosurgeon;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.client.model.PlayerInternalPartsModel;
import com.maxwell.cyber_ware_port.client.model.SkeletonDisplayModel;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.container.RobosurgeonMenu;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.risk.SurgeryAlert;
import com.maxwell.cyber_ware_port.common.risk.SurgeryAnalyzer;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.lang.reflect.Field;
import java.util.List;

public class RobosurgeonScreen extends AbstractContainerScreen<RobosurgeonMenu> {
    private static final Identifier INTERNAL_PARTS_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/player_internal_part.png");
    private static final Identifier SKELETON_TEXTURE = Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/surgery.png");
    private static final Identifier MARKER_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/marker.png");
    private static final Identifier RED_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/red_slot.png");
    private static final Identifier BLUE_SLOT_TEXTURE = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/blue_slot.png");
    private static final Identifier ALERT_ICON = Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/risk_icons.png");
    private static final float ANIMATION_DURATION = 2000f;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = 2;
    private static final int GUI_WIDTH = 175;
    private static final int TOP_HEIGHT = 131;
    private static final int BOTTOM_HEIGHT = 91;
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

    private final Lighting lightingInstance = new Lighting();
    private PlayerInternalPartsModel internalPartsModel;
    private BodyPart selectedPart = BodyPart.NONE;
    private TargetMarker selectedMarker = null;
    private SkeletonDisplayModel skeletonModel;
    private boolean isDraggingModel = false;
    private float viewRotation = 0f;
    private double dragStartX = 0;
    private float rotationStart = 0f;
    private boolean potentialDrag = false;
    private long startTime;
    private AbstractWidget installedListButton;
    private float currentScale = 45;
    private float currentOffsetX = 0f;
    private float currentOffsetY = 0f;
    private boolean hideName = false;

    public RobosurgeonScreen(RobosurgeonMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, GUI_WIDTH, TOP_HEIGHT + BOTTOM_HEIGHT);
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelY = 6;
    }

    private static int[] slots(int start) {
        int[] s = new int[9];
        for (int i = 0; i < 9; i++) s[i] = start + i;
        return s;
    }

    public void renderCustomModel(GuiGraphicsExtractor graphics, int pX, int pY, int pScale, float rotationYaw, Model pModel, Identifier texture) {
        graphics.pose().pushMatrix();
        graphics.pose().translate((float) pX, (float) pY);
        graphics.pose().scale((float) pScale, (float) pScale);
        Quaternionf tilt = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf rot = Axis.YP.rotationDegrees(rotationYaw + 180.0F);
        tilt.mul(rot);
        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        this.lightingInstance.setupFor(Lighting.Entry.ENTITY_IN_UI);
        com.mojang.blaze3d.vertex.PoseStack poseStack = new com.mojang.blaze3d.vertex.PoseStack();
        poseStack.mulPose(tilt);
        pModel.renderToBuffer(poseStack,
                bufferSource.getBuffer(pModel.renderType(texture)),
                15728880,
                OverlayTexture.NO_OVERLAY);
        bufferSource.endBatch();
        graphics.pose().popMatrix();
        this.lightingInstance.setupFor(Lighting.Entry.ITEMS_3D);
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null) {
            this.skeletonModel = new SkeletonDisplayModel(this.minecraft.getEntityModels().bakeLayer(SkeletonDisplayModel.LAYER_LOCATION));
            this.internalPartsModel = new PlayerInternalPartsModel(this.minecraft.getEntityModels().bakeLayer(PlayerInternalPartsModel.LAYER_LOCATION));
        }
        this.startTime = System.currentTimeMillis();
        this.installedListButton = new AbstractWidget(this.leftPos + 158, this.topPos + 4, 10, 10, Component.empty()) {
            @Override
            protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 176, 122, 10, 10, 256, 256);
                if (this.isHovered) {
                    graphics.fill(0, 0, 10, 10, 0x50FFFFFF);
                    graphics.setTooltipForNextFrame(Minecraft.getInstance().font,
                            List.of(Component.translatable("gui.cyber_ware_port.button.view_installed")),
                            null, ItemStack.EMPTY, mouseX, mouseY, null);
                }
            }

            @Override
            protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {
                this.defaultButtonNarrationText(narration);
            }

            @Override
            public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
                Minecraft.getInstance().setScreen(new InstalledCyberwareScreen(RobosurgeonScreen.this));
            }
        };
        this.addRenderableWidget(this.installedListButton);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        updateSlotPositions();
        super.extractRenderState(graphics, mouseX, mouseY, a);
        drawAbsoluteOverlays(graphics, mouseX, mouseY);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 0, 0, GUI_WIDTH, TOP_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, TOP_HEIGHT, 0, 131, GUI_WIDTH, BOTTOM_HEIGHT, 256, 256);
        drawRelativeModels(graphics);
        super.extractContents(graphics, mouseX, mouseY, a);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.minecraft.player == null) return;
        CyberwareUserData data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        int maxTolerance = data.getMaxTolerance(this.minecraft.player);
        int currentCost = calculateTotalEssenceCost();
        int remaining = maxTolerance - currentCost;
        int color = (remaining < 0) ? 0xFFAA0000 : (remaining < 25 ? 0xFFFF5555 : 0xFF00FFFF);
        graphics.text(this.font, Component.literal(remaining + " / " + maxTolerance), 18, 6, color, true);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFF404040, false);
    }

    private void drawAbsoluteOverlays(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.selectedPart != BodyPart.NONE && this.selectedMarker == null) {
            float scaleFactor = currentScale * 0.065f;
            float radRot = (float) Math.toRadians(this.viewRotation);
            float sin = (float) Math.sin(radRot);
            float cos = (float) Math.cos(radRot);
            int modelCenterX = (int) (this.leftPos + 88 + currentOffsetX);
            int modelCenterY = (int) (this.topPos + TOP_HEIGHT - 15 + currentOffsetY);
            for (TargetMarker marker : this.selectedPart.markers) {
                float screenOffsetX = (marker.modelX() * cos) - (marker.modelZ() * sin);
                int markerX = modelCenterX + (int) (screenOffsetX * scaleFactor) - 8;
                int markerY = modelCenterY - (int) (marker.modelY() * scaleFactor) - 8;
                graphics.blit(RenderPipelines.GUI_TEXTURED, MARKER_TEXTURE, markerX, markerY, 0, 0, 16, 16, 16, 16);
                if (mouseX >= markerX && mouseX < markerX + 16 && mouseY >= markerY && mouseY < markerY + 16) {
                    graphics.setTooltipForNextFrame(this.font, List.of(marker.name()), null, ItemStack.EMPTY, mouseX, mouseY, null);
                }
            }
        }
        SurgeryAlert alert = SurgeryAnalyzer.check(this.menu.slots, 100);
        if (alert != null) {
            int iconX = this.leftPos + 155;
            int iconY = this.topPos + 20;
            graphics.blit(RenderPipelines.GUI_TEXTURED, ALERT_ICON, iconX, iconY, 0, 0, 16, 16, 16, 16);
            if (mouseX >= iconX && mouseX < iconX + 16 && mouseY >= iconY && mouseY < iconY + 16) {
                graphics.setTooltipForNextFrame(this.font, List.of(alert.message()), null, ItemStack.EMPTY, mouseX, mouseY, null);
            }
        }
    }

    private void drawRelativeModels(GuiGraphicsExtractor graphics) {
        int maxEssence = this.minecraft.player != null ?
                this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get()).getMaxTolerance(this.minecraft.player) : 100;
        int projectedEssence = maxEssence - calculateTotalEssenceCost();
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, 5, 4, 211, 61, 8, 48, 256, 256);
        drawEssenceBar(graphics, projectedEssence, maxEssence, 5, 4, 8, 48);
        float spd = 0.05f;
        currentScale += ((selectedPart == BodyPart.NONE ? BASE_SCALE : selectedPart.zoomScale) - currentScale) * spd;
        currentOffsetX += ((selectedPart == BodyPart.NONE ? 0 : selectedPart.zoomOffsetX) - currentOffsetX) * spd;
        currentOffsetY += ((selectedPart == BodyPart.NONE ? 0 : selectedPart.zoomOffsetY) - currentOffsetY) * spd;
        int drawX = (int) (88 + currentOffsetX);
        int drawY = (int) (TOP_HEIGHT - 15 + currentOffsetY);
        if (!this.hideName && this.minecraft.player != null) {
            String name = "_" + this.minecraft.player.getName().getString().toUpperCase();
            graphics.text(this.font, Component.literal(name), drawX - this.font.width(name) / 2, drawY, 0xFF00FFFF, true);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        float ease = (elapsed < ANIMATION_DURATION) ? (1f - (float) Math.pow(1f - Math.min(elapsed / ANIMATION_DURATION, 1f), 3)) : 1.0f;
        float currentRotation = isDraggingModel ? this.viewRotation : ease * 360f;
        if (skeletonModel != null) {
            graphics.enableScissor(this.leftPos + 5, this.topPos + 5, this.leftPos + GUI_WIDTH - 5, this.topPos + TOP_HEIGHT - 10);
            renderCustomModel(graphics, drawX, drawY, (int) (currentScale * 0.933f), currentRotation, skeletonModel, SKELETON_TEXTURE);
            graphics.disableScissor();
        }
    }

    private int calculateTotalEssenceCost() {
        int cost = 0;
        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            ItemStack stack = this.menu.getSlot(i).getItem();
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null) cost += cw.getEssenceCost(stack) * stack.getCount();
        }
        return cost;
    }

    private void drawEssenceBar(GuiGraphicsExtractor graphics, int essence, int maxEssence, int x, int y, int w, int h) {
        int danger = (int) (maxEssence * 0.25f);
        int rH = (int) (h * ((float) Math.min(Math.max(0, essence), danger) / maxEssence));
        int bH = (int) (h * ((float) Math.max(0, essence - danger) / maxEssence));
        if (rH > 0)
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + (h - rH), 220, 61 + (48 - rH), w, rH, 256, 256);
        if (bH > 0)
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + (h - rH - bH), 176, 61 + (48 - (rH + bH)), w, bH, 256, 256);
    }

    private void updateSlotPositions() {
        for (Slot slot : this.menu.slots) {
            setSlotPos(slot, 20000, 20000);
        }
        if (this.selectedMarker != null) {
            int[] targets = this.selectedMarker.relatedSlots();
            int uiWidth = (SLOT_SIZE * targets.length) + (SLOT_SPACING * (targets.length - 1));
            int uiX = (this.imageWidth - uiWidth) / 2;
            for (int i = 0; i < targets.length; i++) {
                int slotIndex = targets[i];
                if (slotIndex < this.menu.slots.size()) {
                    setSlotPos(this.menu.slots.get(slotIndex), uiX + (i * (SLOT_SIZE + SLOT_SPACING)) + 1, 106);
                }
            }
        }
    }

    private void setSlotPos(Slot slot, int x, int y) {
        try {
            slotX.set(slot, x);
            slotY.set(slot, y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private enum BodyPart {
        HEAD(0, -80, 32, 32, 0, 160, 120f, List.of(new TargetMarker(Component.literal("Left Eye"), 2, 25.5f, -3.4f, slots(RobosurgeonBlockEntity.SLOT_EYES)), new TargetMarker(Component.literal("Right Eye"), -2f, 25.5f, -3.4f, slots(RobosurgeonBlockEntity.SLOT_EYES)), new TargetMarker(Component.literal("Brain"), -0.13f, 27.56f, 1.52f, slots(RobosurgeonBlockEntity.SLOT_BRAIN)))),
        TORSO(0, -54, 26, 32, 0, 120, 120f, List.of(new TargetMarker(Component.literal("Heart"), 0f, 21f, -0.5f, slots(RobosurgeonBlockEntity.SLOT_HEART)), new TargetMarker(Component.literal("Left Lung"), 2.3f, 20f, 0, slots(RobosurgeonBlockEntity.SLOT_LUNGS)), new TargetMarker(Component.literal("Stomach"), 0.0f, 16f, -1.5f, slots(RobosurgeonBlockEntity.SLOT_STOMACH)), new TargetMarker(Component.literal("Right Lung"), -2.3f, 20f, 0f, slots(RobosurgeonBlockEntity.SLOT_LUNGS)))),
        ARM_LEFT(18, -54, 12, 34, -60, 120, 120f, List.of(new TargetMarker(Component.literal("Left Arm"), 4.7f, 21.0f, -0, slots(RobosurgeonBlockEntity.SLOT_ARMS)), new TargetMarker(Component.literal("Left Hand"), 5.8f, 14f, 0f, slots(RobosurgeonBlockEntity.SLOT_HANDS)))),
        ARM_RIGHT(-18, -54, 12, 34, 60, 120, 120f, List.of(new TargetMarker(Component.literal("Right Arm"), -4.7f, 21.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_ARMS)), new TargetMarker(Component.literal("Right Hand"), -5.8f, 14f, 0f, slots(RobosurgeonBlockEntity.SLOT_HANDS)))),
        LEG_LEFT(5, -19, 12, 38, -50, 20, 120f, List.of(new TargetMarker(Component.literal("Left Leg"), 2f, 10.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_LEGS)), new TargetMarker(Component.literal("Left Foot"), 2.1f, 3.9f, 0f, slots(RobosurgeonBlockEntity.SLOT_BOOTS)))),
        LEG_RIGHT(-5, -19, 12, 38, 50, 20, 120f, List.of(new TargetMarker(Component.literal("Right Leg"), -2f, 10.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_LEGS)), new TargetMarker(Component.literal("Right Foot"), -2.1f, 3.9f, 0f, slots(RobosurgeonBlockEntity.SLOT_BOOTS)))),
        INTERNAL(0, 0, 40, 50, 48, 130, 150f, List.of(new TargetMarker(Component.literal("Skin"), -3.0f, 24.6f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_SKIN)), new TargetMarker(Component.literal("Muscle"), -0, 22.7f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_MUSCLE)), new TargetMarker(Component.literal("Bone"), 3.0f, 20.8f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_BONES)))),
        NONE(0, 0, 0, 0, 0, 0, 45f, List.of());
        final int hitX, hitY, hitW, hitH, zoomOffsetX, zoomOffsetY;
        final float zoomScale;
        final List<TargetMarker> markers;

        BodyPart(int hX, int hY, int hW, int hH, int zX, int zY, float zS, List<TargetMarker> m) {
            this.hitX = hX;
            this.hitY = hY;
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