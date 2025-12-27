package com.maxwell.cyber_ware_port.client.screen.roboSurgeon;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.container.RobosurgeonMenu;
import com.maxwell.cyber_ware_port.common.entity.playerpartsmodel.PlayerInternalPartsModel;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.network.A_PacketHandler;
import com.maxwell.cyber_ware_port.common.network.SurgeryGhostTogglePacket;
import com.maxwell.cyber_ware_port.common.risk.SurgeryAlert;
import com.maxwell.cyber_ware_port.common.risk.SurgeryAnalyzer;
import com.maxwell.cyber_ware_port.CyberWare;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("removal")
public class RobosurgeonScreen extends AbstractContainerScreen<RobosurgeonMenu> {
    private static final ResourceLocation INTERNAL_PARTS_TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/player_internal_part.png");
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/surgery.png");
    private static final ResourceLocation MARKER_TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/gui/marker.png");
    private static final ResourceLocation RED_SLOT_TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/gui/red_slot.png");
    private static final ResourceLocation BLUE_SLOT_TEXTURE = new ResourceLocation(CyberWare.MODID, "textures/gui/blue_slot.png");
    private static final ResourceLocation ALERT_ICON =
            new ResourceLocation(CyberWare.MODID, "textures/gui/risk_icons.png");
    private static final float ANIMATION_DURATION = 2000f;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = 2;
    private static final int GUI_WIDTH = 175;
    private static final int TOP_HEIGHT = 131;
    private static final int BOTTOM_HEIGHT = 91;
    private static final int TEXTURE_INVENTORY_START_Y = 131;
    private static final float BASE_SCALE = 45f;
    private PlayerInternalPartsModel internalPartsModel;
    private BodyPart selectedPart = BodyPart.NONE;
    private TargetMarker selectedMarker = null;
    private Skeleton dummySkeleton;
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

    public RobosurgeonScreen(RobosurgeonMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = TOP_HEIGHT + BOTTOM_HEIGHT;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelY = 6;
    }

    public static void renderEntityWithRotation(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, float rotationYaw, LivingEntity pEntity) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float) pX, (float) pY, 50.0F);
        pGuiGraphics.pose().mulPoseMatrix((new Matrix4f()).scaling((float) pScale, (float) pScale, (float) (-pScale)));
        Quaternionf quaternionf = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf rotation = Axis.YP.rotationDegrees(rotationYaw + 180.0F);
        quaternionf.mul(rotation);
        pGuiGraphics.pose().mulPose(quaternionf);
        float f2 = pEntity.yBodyRot;
        float f3 = pEntity.getYRot();
        float f4 = pEntity.getXRot();
        float f5 = pEntity.yHeadRotO;
        float f6 = pEntity.yHeadRot;
        pEntity.yBodyRot = 0;
        pEntity.setYRot(0);
        pEntity.setXRot(0);
        pEntity.yHeadRot = 0;
        pEntity.yHeadRotO = 0;
        float originalLimbSwing = pEntity.walkAnimation.position();
        float originalLimbSwingAmount = pEntity.walkAnimation.speed();
        float originalAttackAnim = pEntity.attackAnim;
        pEntity.walkAnimation.setSpeed(0.0f);
        pEntity.attackAnim = 0.0f;
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        rotation.conjugate();
        entityrenderdispatcher.overrideCameraOrientation(rotation);
        entityrenderdispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> {
            entityrenderdispatcher.render(pEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, pGuiGraphics.pose(), pGuiGraphics.bufferSource(), 15728880);

        });
        pGuiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        pEntity.yBodyRot = f2;
        pEntity.setYRot(f3);
        pEntity.setXRot(f4);
        pEntity.yHeadRotO = f5;
        pEntity.yHeadRot = f6;
        pEntity.walkAnimation.setSpeed(originalLimbSwingAmount);
        pEntity.attackAnim = originalAttackAnim;
        pGuiGraphics.pose().popPose();
        Lighting.setupFor3DItems();

    }

    private static int[] slots(int start) {
        int[] slots = new int[9];
        for (int i = 0;
             i < 9;
             i++) {
            slots[i] = start + i;

        }
        return slots;

    }

    public static void renderCustomModel(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, float rotationYaw, Model pModel) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float) pX, (float) pY, 50.0F);
        pGuiGraphics.pose().mulPoseMatrix((new Matrix4f()).scaling((float) pScale, (float) pScale, (float) (-pScale)));
        Quaternionf quaternionf = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf rotation = Axis.YP.rotationDegrees(rotationYaw + 180.0F);
        quaternionf.mul(rotation);
        pGuiGraphics.pose().mulPose(quaternionf);
        Lighting.setupForEntityInInventory();
        VertexConsumer vertexConsumer = pGuiGraphics.bufferSource().getBuffer(pModel.renderType(INTERNAL_PARTS_TEXTURE));
        pModel.renderToBuffer(pGuiGraphics.pose(), vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pGuiGraphics.flush();
        pGuiGraphics.pose().popPose();
        Lighting.setupFor3DItems();

    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null && this.minecraft.level != null) {
            this.dummySkeleton = new Skeleton(EntityType.SKELETON, this.minecraft.level);
            this.internalPartsModel = new PlayerInternalPartsModel(
                    this.minecraft.getEntityModels().bakeLayer(PlayerInternalPartsModel.LAYER_LOCATION)
            );
        }
        this.startTime = System.currentTimeMillis();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int listBtnX = x + 158;
        int listBtnY = y + 4;
        this.installedListButton = new AbstractWidget(listBtnX, listBtnY, 10, 10, Component.empty()) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                guiGraphics.blit(TEXTURE, this.getX(), this.getY(), this.width, this.height, 176, 122, 10, 10, 256, 256);
                if (this.isHovered()) {
                    guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0x50FFFFFF);
                    guiGraphics.renderTooltip(font, Component.translatable("gui.cyber_ware_port.button.view_installed"), mouseX, mouseY);

                }
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                Minecraft.getInstance().setScreen(new InstalledCyberwareScreen(RobosurgeonScreen.this));

            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
                this.defaultButtonNarrationText(narrationElementOutput);

            }
        };
        this.installedListButton.visible = true;
        this.addRenderableWidget(this.installedListButton);

    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        long elapsedTime = System.currentTimeMillis() - this.startTime;
        if (elapsedTime < ANIMATION_DURATION) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);

        }
        updateSlotPositions();
        if (!this.menu.getCarried().isEmpty()) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);

        }
        if (pButton == 0 || pButton == 1) {
            if (this.selectedMarker != null) {
                Slot hoveredSlot = null;
                for (Slot slot : this.menu.slots) {
                    if (slot.x > 10000 || slot.y > 10000) continue;
                    int slotLeft = this.leftPos + slot.x;
                    int slotTop = this.topPos + slot.y;
                    if (pMouseX >= slotLeft && pMouseX < slotLeft + 16 &&
                            pMouseY >= slotTop && pMouseY < slotTop + 16) {
                        hoveredSlot = slot;
                        break;

                    }
                }
                if (hoveredSlot != null && hoveredSlot.index < RobosurgeonBlockEntity.TOTAL_SLOTS) {
                    if (hoveredSlot.hasItem()) {
                        ItemStack stack = hoveredSlot.getItem();
                        if (stack.hasTag() && stack.getTag().getBoolean("cyberware_ghost")) {
                            A_PacketHandler.INSTANCE.sendToServer(new SurgeryGhostTogglePacket(
                                    this.menu.blockEntity.getBlockPos(), hoveredSlot.index));
                            hoveredSlot.set(ItemStack.EMPTY);
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            return true;

                        }
                    } else {
                        A_PacketHandler.INSTANCE.sendToServer(new SurgeryGhostTogglePacket(
                                this.menu.blockEntity.getBlockPos(), hoveredSlot.index));
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        return true;

                    }
                }
            }
        }
        if (pButton == 0) {
            if (this.selectedPart != BodyPart.NONE && this.selectedMarker == null) {
                int modelCenterX = (int) (this.leftPos + 88 + currentOffsetX);
                int modelCenterY = (int) (this.topPos + TOP_HEIGHT - 15 + currentOffsetY);
                if (this.selectedPart == BodyPart.INTERNAL) {
                    modelCenterX -= 48;
                    modelCenterY += 26;

                }
                float radRot = (float) Math.toRadians(this.viewRotation);
                float sin = (float) Math.sin(radRot);
                float cos = (float) Math.cos(radRot);
                float scaleFactor = currentScale * 0.065f;
                for (TargetMarker marker : this.selectedPart.markers) {
                    float modelX, modelY, modelZ;
                    {
                        modelX = marker.modelX();
                        modelY = marker.modelY();
                        modelZ = marker.modelZ();

                    }
                    float screenOffsetX = (modelX * cos) - (modelZ * sin);
                    float screenOffsetY = modelY;
                    int markerX = modelCenterX + (int) (screenOffsetX * scaleFactor) - 8;
                    int markerY = modelCenterY - (int) (screenOffsetY * scaleFactor) - 8;
                    if (pMouseX >= markerX && pMouseX < markerX + 16 &&
                            pMouseY >= markerY && pMouseY < markerY + 16) {
                        {
                            this.selectedMarker = marker;
                            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.2F));

                        }
                        return true;

                    }
                }
            }
            if (this.selectedPart == BodyPart.NONE) {
                int x = (this.width - this.imageWidth) / 2;
                int y = (this.height - this.imageHeight) / 2;
                int subBaseX = x + 40;
                int subBaseY = y + TOP_HEIGHT + 11;
                int boxWidth = 37;
                int boxHeight = 37;
                int boxOffsetY = -30;
                int boxCenterY = subBaseY - (boxHeight / 2) + boxOffsetY;
                double minX = subBaseX - (boxWidth / 2.0);
                double maxX = subBaseX + (boxWidth / 2.0);
                double minY = boxCenterY - (boxHeight / 2.0);
                double maxY = boxCenterY + (boxHeight / 2.0);
                if (pMouseX >= minX && pMouseX <= maxX && pMouseY >= minY && pMouseY <= maxY) {
                    this.selectedPart = BodyPart.INTERNAL;
                    this.hideName = true;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;

                }
            }
            final Slot slot = this.getSlotUnderMouse();
            if (this.menu.getCarried().isEmpty() && slot == null) {
                boolean inModelArea = pMouseX >= this.leftPos && pMouseX < this.leftPos + this.imageWidth &&
                        pMouseY >= this.topPos && pMouseY < this.topPos + TOP_HEIGHT;
                if (inModelArea) {
                    this.potentialDrag = true;
                    this.dragStartX = pMouseX;
                    return true;

                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);

    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        updateSlotPositions();
        if (this.potentialDrag && pButton == 0) {
            if (!this.isDraggingModel) {
                this.isDraggingModel = true;
                this.rotationStart = this.viewRotation;

            }
            this.viewRotation = this.rotationStart + (float) (pMouseX - this.dragStartX);
            return true;

        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);

    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        updateSlotPositions();
        if (pButton != 0) {
            return super.mouseReleased(pMouseX, pMouseY, pButton);

        }
        if (this.isDraggingModel) {
            this.isDraggingModel = false;
            this.potentialDrag = false;
            return true;

        }
        if (this.potentialDrag) {
            boolean partClicked = false;
            if (this.selectedPart == BodyPart.NONE) {
                int entityX = this.leftPos + 88;
                int entityY = this.topPos + TOP_HEIGHT - 15;
                for (BodyPart part : BodyPart.values()) {
                    if (part == BodyPart.NONE) continue;
                    double minX = entityX + part.hitX - (part.hitW / 2.0);
                    double maxX = entityX + part.hitX + (part.hitW / 2.0);
                    double minY = entityY + part.hitY - (part.hitH / 2.0);
                    double maxY = entityY + part.hitY + (part.hitH / 2.0);
                    if (pMouseX >= minX && pMouseX <= maxX && pMouseY >= minY && pMouseY <= maxY) {
                        this.selectedPart = part;
                        this.hideName = true;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        partClicked = true;
                        break;

                    }
                }
            }
            if (!partClicked && this.selectedPart != BodyPart.NONE) {
                this.selectedPart = BodyPart.NONE;
                this.selectedMarker = null;
                this.hideName = false;

            }
            this.potentialDrag = false;
            return true;

        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);

    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        updateSlotPositions();
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int maxTolerance = 100;
        SurgeryAlert alert = SurgeryAnalyzer.check(this.menu.slots, maxTolerance);
        if (alert != null) {
            int iconX = this.leftPos + 155;
            int iconY = this.topPos + 20;
            RenderSystem.setShaderTexture(0, ALERT_ICON);
            RenderSystem.enableBlend();
            pGuiGraphics.blit(ALERT_ICON, iconX, iconY, 0, 0, 16, 16, 16, 16);
            RenderSystem.disableBlend();
            if (pMouseX >= iconX && pMouseX < iconX + 16 && pMouseY >= iconY && pMouseY < iconY + 16) {
                pGuiGraphics.renderTooltip(this.font, alert.message(), pMouseX, pMouseY);

            }
        }
        if (this.selectedPart != BodyPart.NONE && this.selectedMarker == null) {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, 0, 100);
            int modelCenterX = (int) (this.leftPos + 88 + currentOffsetX);
            int modelCenterY = (int) (this.topPos + TOP_HEIGHT - 15 + currentOffsetY);
            if (this.selectedPart == BodyPart.INTERNAL) {
                modelCenterX -= 48;
                modelCenterY += 26;

            }
            float radRot = (float) Math.toRadians(this.viewRotation);
            float sin = (float) Math.sin(radRot);
            float cos = (float) Math.cos(radRot);
            float scaleFactor = currentScale * 0.065f;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.8F);
            for (TargetMarker marker : this.selectedPart.markers) {
                float modelX, modelY, modelZ;
                {
                    modelX = marker.modelX();
                    modelY = marker.modelY();
                    modelZ = marker.modelZ();

                }
                float screenOffsetX = (modelX * cos) - (modelZ * sin);
                float screenOffsetY = modelY;
                int markerX = modelCenterX + (int) (screenOffsetX * scaleFactor) - 8;
                int markerY = modelCenterY - (int) (screenOffsetY * scaleFactor) - 8;
                pGuiGraphics.blit(MARKER_TEXTURE, markerX, markerY, 0, 0, 16, 16, 16, 16);
                if (pMouseX >= markerX && pMouseX < markerX + 16 && pMouseY >= markerY && pMouseY < markerY + 16) {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    pGuiGraphics.renderTooltip(this.font, marker.name(), pMouseX, pMouseY);
                    pGuiGraphics.fill(markerX, markerY, markerX + 16, markerY + 16, 0x50FFFFFF);
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.8F);

                }
            }
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            pGuiGraphics.pose().popPose();

        }
        if (this.selectedMarker != null) {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, 0, 100);
            int slotCount = this.selectedMarker.relatedSlots().length;
            int uiWidth = (SLOT_SIZE * slotCount) + (SLOT_SPACING * (slotCount - 1));
            int uiX = this.leftPos + (this.imageWidth - uiWidth) / 2;
            int installedY = this.topPos + 80;
            int stagingY = this.topPos + 105;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            for (int i = 0;
                 i < slotCount;
                 i++) {
                int slotX = uiX + (i * (SLOT_SIZE + SLOT_SPACING));
                pGuiGraphics.blit(BLUE_SLOT_TEXTURE, slotX - 1, stagingY - 1, 0, 0, 18, 18, 18, 18);
                pGuiGraphics.blit(RED_SLOT_TEXTURE, slotX - 1, installedY - 1, 0, 0, 18, 18, 18, 18);

            }
            RenderSystem.disableBlend();
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(cyberware -> {
                    ItemStackHandler installed = cyberware.getInstalledCyberware();
                    for (int i = 0;
                         i < slotCount;
                         i++) {
                        int slotId = this.selectedMarker.relatedSlots()[i];
                        int itemX = uiX + (i * (SLOT_SIZE + SLOT_SPACING));
                        if (slotId < installed.getSlots()) {
                            ItemStack installedStack = installed.getStackInSlot(slotId);
                            pGuiGraphics.renderItem(installedStack, itemX, installedY);
                            pGuiGraphics.renderItemDecorations(this.font, installedStack, itemX, installedY);
                            if (this.menu.getSlot(slotId).getItem().isEmpty() && !installedStack.isEmpty()) {
                                pGuiGraphics.pose().pushPose();
                                pGuiGraphics.pose().translate(0, 0, 150);
                                pGuiGraphics.renderItem(installedStack, itemX, stagingY);
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                pGuiGraphics.fill(itemX, stagingY, itemX + 16, stagingY + 16, 0x80000000);
                                RenderSystem.disableBlend();
                                pGuiGraphics.pose().popPose();

                            }
                        }
                    }
                });

            }
        }
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        if (this.selectedPart != BodyPart.NONE && this.selectedMarker == null) {
            int modelCenterX = (int) (this.leftPos + 88 + currentOffsetX);
            int modelCenterY = (int) (this.topPos + TOP_HEIGHT - 15 + currentOffsetY);
            float radRot = (float) Math.toRadians(this.viewRotation);
            float sin = (float) Math.sin(radRot);
            float cos = (float) Math.cos(radRot);
            float scaleFactor = currentScale * 0.065f;
            for (TargetMarker marker : this.selectedPart.markers) {
                float screenOffsetX = (marker.modelX() * cos) - (marker.modelZ() * sin);
                float screenOffsetY = marker.modelY();
                int markerX = modelCenterX + (int) (screenOffsetX * scaleFactor) - 8;
                int markerY = modelCenterY - (int) (screenOffsetY * scaleFactor) - 8;
                if (pMouseX >= markerX && pMouseX < markerX + 16 && pMouseY >= markerY && pMouseY < markerY + 16) {
                    pGuiGraphics.renderTooltip(this.font, marker.name(), pMouseX, pMouseY);

                }
            }
        }
        if (this.selectedMarker != null) {
            int slotCount = this.selectedMarker.relatedSlots().length;
            int uiWidth = (SLOT_SIZE * slotCount) + (SLOT_SPACING * (slotCount - 1));
            int uiX = this.leftPos + (this.imageWidth - uiWidth) / 2;
            int installedY = this.topPos + 80;
            int stagingY = this.topPos + 105;
            renderGhostConflict(pGuiGraphics, slotCount, uiX, stagingY, installedY);
            for (int i = 0;
                 i < slotCount;
                 i++) {
                int slotX = uiX + (i * (SLOT_SIZE + SLOT_SPACING));
                int targetSlotId = this.selectedMarker.relatedSlots()[i];
                if (pMouseX >= slotX && pMouseX < slotX + SLOT_SIZE && pMouseY >= stagingY && pMouseY < stagingY + SLOT_SIZE) {
                    if (targetSlotId < RobosurgeonBlockEntity.TOTAL_SLOTS) {
                        ItemStack stack = this.menu.getSlot(targetSlotId).getItem();
                        if (!stack.isEmpty()) {
                            pGuiGraphics.renderTooltip(this.font, stack, pMouseX, pMouseY);

                        }
                    }
                }
                if (pMouseX >= slotX && pMouseX < slotX + SLOT_SIZE && pMouseY >= installedY && pMouseY < installedY + SLOT_SIZE) {
                    if (this.minecraft != null && this.minecraft.player != null) {
                        this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY).ifPresent(cyberware -> {
                            ItemStackHandler installed = cyberware.getInstalledCyberware();
                            if (targetSlotId < installed.getSlots()) {
                                ItemStack stack = installed.getStackInSlot(targetSlotId);
                                if (!stack.isEmpty()) {
                                    pGuiGraphics.renderTooltip(this.font, stack, pMouseX, pMouseY);

                                }
                            }
                        });

                    }
                }
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        g.blit(TEXTURE, x, y, 0, 0, GUI_WIDTH, TOP_HEIGHT);
        g.blit(TEXTURE, x, y + TOP_HEIGHT, 0, TEXTURE_INVENTORY_START_Y, GUI_WIDTH, BOTTOM_HEIGHT);
        int maxEssence = 100;
        int currentEssence = 0;
        if (this.minecraft.player != null) {
            var cap = this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY);
            if (cap.isPresent()) currentEssence = cap.resolve().get().getTolerance();

        }
        int projectedCost = 0;
        for (int i = 0;
             i < RobosurgeonBlockEntity.TOTAL_SLOTS;
             i++) {
            if (i >= this.menu.slots.size()) break;
            ItemStack stack = this.menu.getSlot(i).getItem();
            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cw) {
                projectedCost += cw.getEssenceCost(stack) * stack.getCount();

            }
        }
        int projectedEssence = maxEssence - projectedCost;
        int barX = x + 5;
        int barY = y + 4;
        int barW = 8;
        int barH = 48;
        g.blit(TEXTURE, barX, barY, barW, barH, 211, 61, barW, barH, 256, 256);
        drawEssenceBar(g, projectedEssence, maxEssence, barX, barY, barW, barH);
        if (currentEssence > projectedEssence) {
            float time = (System.currentTimeMillis() % 1000) / 1000f;
            float alpha = 0.45f + 0.25f * (float) Math.sin(time * 2 * Math.PI);
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
            drawEssenceBar(g, currentEssence, maxEssence, barX, barY, barW, barH);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        }
        float tScale = (selectedPart == BodyPart.NONE) ? BASE_SCALE : selectedPart.zoomScale;
        float tOffX = (selectedPart == BodyPart.NONE) ? 0 : selectedPart.zoomOffsetX;
        float tOffY = (selectedPart == BodyPart.NONE) ? 0 : selectedPart.zoomOffsetY;
        float spd = 0.05f;
        currentScale += (tScale - currentScale) * spd;
        currentOffsetX += (tOffX - currentOffsetX) * spd;
        currentOffsetY += (tOffY - currentOffsetY) * spd;
        int baseX = x + 88;
        int baseY = y + TOP_HEIGHT - 15;
        int drawX = (int) (baseX + currentOffsetX);
        int drawY = (int) (baseY + currentOffsetY);
        int drawScale = (int) currentScale;
        int scaleBoost = 5;
        int raiseAmount = 0;
        if (!this.hideName && this.minecraft.player != null) {
            String name = "_" + this.minecraft.player.getName().getString().toUpperCase();
            int w = this.font.width(name);
            g.drawString(this.font, name, drawX - w / 2, drawY, 0x00FFFF, true);

        }
        long elapsed = System.currentTimeMillis() - startTime;
        boolean isAnimating = elapsed < ANIMATION_DURATION;
        float currentRotation;
        float ease = 0;
        if (isAnimating) {
            float r = Math.min(elapsed / ANIMATION_DURATION, 1f);
            ease = 1f - (float) Math.pow(1f - r, 3);
            currentRotation = ease * 360f;

        } else {
            currentRotation = this.viewRotation;
            ease = 1.0f;

        }
        boolean isOverview = (this.selectedPart == BodyPart.NONE);
        boolean isInternalZoomed = (this.selectedPart == BodyPart.INTERNAL);
        boolean isMainZoomed = (!isOverview && !isInternalZoomed);
        if (this.internalPartsModel != null && !isMainZoomed) {
            int subX, subY, subScale;
            if (isInternalZoomed) {
                subX = drawX - 48;
                subY = drawY;
                subScale = drawScale;

            } else {
                subX = x + 40;
                subY = y + TOP_HEIGHT + 11;
                subScale = 40;

            }
            if (isOverview) {
                int boxWidth = 37;
                int boxHeight = 37;
                int boxOffsetY = -34;
                int boxCenterX = subX;
                int boxCenterY = subY - (boxHeight / 2) + boxOffsetY;
                int boxLeft = boxCenterX - (boxWidth / 2);
                int boxRight = boxCenterX + (boxWidth / 2);
                int boxTop = boxCenterY - (boxHeight / 2);
                int boxBottom = boxCenterY + (boxHeight / 2);
                int cyanColor = 0xFF00FFFF;
                int lineWidth = 1;
                g.fill(boxLeft, boxTop, boxRight, boxTop + lineWidth, cyanColor);
                g.fill(boxLeft, boxBottom, boxRight, boxBottom + lineWidth, cyanColor);
                g.fill(boxLeft, boxTop, boxLeft + lineWidth, boxBottom, cyanColor);
                g.fill(boxRight - lineWidth, boxTop, boxRight, boxBottom, cyanColor);
                int lineStartX = boxCenterX + 10;
                int lineUpHeight = 20;
                int lineStartY = boxTop;
                int cornerY = boxTop - lineUpHeight;
                int lineEndX = drawX;
                g.fill(lineStartX, cornerY, lineStartX + lineWidth, lineStartY, cyanColor);
                g.fill(lineStartX, cornerY, lineEndX, cornerY + lineWidth, cyanColor);

            }
            this.internalPartsModel.setVisibleLayer(0);
            renderCustomModel(g, subX, subY, subScale, currentRotation, this.internalPartsModel);
            this.internalPartsModel.setVisibleLayer(1);
            renderCustomModel(g, subX, subY, subScale, currentRotation, this.internalPartsModel);
            this.internalPartsModel.setVisibleLayer(2);
            renderCustomModel(g, subX, subY, subScale, currentRotation, this.internalPartsModel);

        }
        int Pscale = 45 + scaleBoost;
        int Sscale = 42 + scaleBoost;
        int modelH = 115;
        int scan = (int) (modelH * ease);
        if (!isInternalZoomed) {
            double guiScale = this.minecraft.getWindow().getGuiScale();
            if (!isAnimating) {
                if (dummySkeleton != null) {
                    int scX = (int) ((x + 5) * guiScale);
                    int scY = (int) ((this.height - (y + TOP_HEIGHT)) * guiScale);
                    int scW = (int) ((GUI_WIDTH - 10) * guiScale);
                    int scH = (int) ((TOP_HEIGHT - 10) * guiScale);
                    float skeletonBaseSize = 42f;
                    float playerBaseSize = 45f;
                    float ratio = skeletonBaseSize / playerBaseSize;
                    int adjustedScale = (int) (currentScale * ratio) + scaleBoost;
                    RenderSystem.enableScissor(scX, scY, scW, scH);
                    renderEntityWithRotation(g, drawX, drawY - raiseAmount, adjustedScale, currentRotation, dummySkeleton);
                    RenderSystem.disableScissor();

                }
            } else {
                int scX = (int) ((drawX - 50) * guiScale);
                int scW = (int) (100 * guiScale);
                int scFeet = (int) ((this.height - drawY) * guiScale);
                if (this.minecraft.player != null) {
                    int ph = modelH - scan;
                    if (ph > 0) {
                        RenderSystem.enableScissor(scX, scFeet, scW, (int) (ph * guiScale));
                        renderEntityWithRotation(g, drawX, drawY - raiseAmount, Pscale, currentRotation, this.minecraft.player);
                        RenderSystem.disableScissor();

                    }
                }
                if (dummySkeleton != null && scan > 0) {
                    int sy = scFeet + (int) ((modelH - scan) * guiScale);
                    RenderSystem.enableScissor(scX, sy, scW, (int) (scan * guiScale));
                    renderEntityWithRotation(g, drawX, drawY - raiseAmount, Sscale, currentRotation, dummySkeleton);
                    RenderSystem.disableScissor();

                }
            }
        }
        if (ease > 0 && ease < 1) {
            int scanY = (drawY - modelH) + scan;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            g.pose().pushPose();
            g.pose().translate(0, 0, 60);
            g.blit(TEXTURE, drawX - 40, scanY, 176, 110, 80, 1);
            g.pose().popPose();

        }
        RenderSystem.disableBlend();

    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        int maxTolerance = 100;
        if (Objects.requireNonNull(this.minecraft).player != null) {
            var cap = this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY);
            if (cap.isPresent()) {
                maxTolerance = cap.resolve().get().getMaxTolerance();
            }
        }
        int currentCost = 0;
        for (int i = 0;
             i < RobosurgeonBlockEntity.TOTAL_SLOTS;
             i++) {
            if (i >= this.menu.slots.size()) break;
            ItemStack stack = this.menu.getSlot(i).getItem();
            if (!stack.isEmpty() && stack.getItem() instanceof ICyberware cw) {
                currentCost += cw.getEssenceCost(stack) * stack.getCount();

            }
        }
        int val = maxTolerance - currentCost;
        int color = (val < 30) ? 0xFF0000 : 0x00FFFF;
        pGuiGraphics.drawString(this.font, val + " / " + maxTolerance, 18, 6, color, true);

    }

    private void updateSlotPositions() {
        for (int i = 0;
             i < RobosurgeonBlockEntity.TOTAL_SLOTS;
             i++) {
            if (i < this.menu.slots.size()) {
                Slot slot = this.menu.slots.get(i);
                slot.x = 20000;
                slot.y = 20000;

            }
        }
        if (this.selectedMarker != null) {
            int slotCount = this.selectedMarker.relatedSlots().length;
            int uiWidth = (SLOT_SIZE * slotCount) + (SLOT_SPACING * (slotCount - 1));
            int uiX = (this.width - uiWidth) / 2;
            int stagingY = (this.height - this.imageHeight) / 2 + 105;
            int relativeX = uiX - this.leftPos;
            int relativeY = stagingY - this.topPos;
            int[] targetSlots = this.selectedMarker.relatedSlots();
            for (int i = 0;
                 i < targetSlots.length;
                 i++) {
                int slotId = targetSlots[i];
                if (slotId < this.menu.slots.size()) {
                    Slot slot = this.menu.slots.get(slotId);
                    slot.x = relativeX + (i * (SLOT_SIZE + SLOT_SPACING));
                    slot.y = relativeY;

                }
            }
        }
    }

    private void renderGhostConflict(GuiGraphics g, int slotCount, int uiX, int stagingY, int installedY) {
        ItemStack carriedStack = this.menu.getCarried();
        if (carriedStack.isEmpty()) return;
        if (!(carriedStack.getItem() instanceof ICyberware carriedItem)) return;
        Slot hover = this.hoveredSlot;
        if (hover == null || hover.index >= RobosurgeonBlockEntity.TOTAL_SLOTS) return;
        ItemStackHandler installedHandler = null;
        if (this.minecraft.player != null) {
            var cap = this.minecraft.player.getCapability(CyberwareCapabilityProvider.CYBERWARE_CAPABILITY);
            if (cap.isPresent()) {
                installedHandler = cap.resolve().get().getInstalledCyberware();

            }
        }
        if (installedHandler == null) return;
        int[] related = this.selectedMarker.relatedSlots();
        boolean isHoveringTarget = false;
        for (int id : related) {
            if (id == hover.index) {
                isHoveringTarget = true;
                break;

            }
        }
        if (!isHoveringTarget) return;
        for (int j = 0;
             j < slotCount;
             j++) {
            int targetId = related[j];
            if (targetId == hover.index) continue;
            ItemStack otherStack = this.menu.getSlot(targetId).getItem();
            if (otherStack.isEmpty()) {
                otherStack = installedHandler.getStackInSlot(targetId);

            }
            if (!otherStack.isEmpty()) {
                if (carriedItem.isIncompatible(carriedStack, otherStack)) {
                    int x2 = uiX + (j * (SLOT_SIZE + SLOT_SPACING));
                    boolean isBlue = !this.menu.getSlot(targetId).getItem().isEmpty();
                    int y2 = isBlue ? stagingY : installedY;
                    g.pose().pushPose();
                    g.pose().translate(0, 0, 400);
                    g.fill(x2, y2, x2 + 18, y2 + 18, 0x80FF0000);
                    g.renderOutline(x2 - 1, y2 - 1, 20, 20, 0xFFFF0000);
                    g.drawString(this.font, "!", x2 + 6, y2 + 4, 0xFFFF0000, true);
                    g.pose().popPose();

                }
            }
        }
    }

    private void drawEssenceBar(GuiGraphics g, int essence, int maxEssence, int x, int y, int w, int h) {
        int dangerThreshold = (int) (maxEssence * 0.25f);
        if (essence < 0) essence = 0;
        int redEssence = Math.min(essence, dangerThreshold);
        int blueEssence = Math.max(0, essence - dangerThreshold);
        int redHeight = (int) (h * ((float) redEssence / maxEssence));
        int blueHeight = (int) (h * ((float) blueEssence / maxEssence));
        if (redHeight > 0) {
            int redDrawY = y + (h - redHeight);
            int redTextureV = 61 + (48 - redHeight);
            g.blit(TEXTURE, x, redDrawY, w, redHeight, 220, redTextureV, w, redHeight, 256, 256);

        }
        if (blueHeight > 0) {
            int blueDrawY = y + (h - redHeight - blueHeight);
            int totalFilledHeight = redHeight + blueHeight;
            int blueTextureV = 61 + (48 - totalFilledHeight);
            g.blit(TEXTURE, x, blueDrawY, w, blueHeight, 176, blueTextureV, w, blueHeight, 256, 256);

        }
    }

    private enum BodyPart {
        HEAD(0, -80, 32, 32, 0, 160, 120f, List.of(
                new TargetMarker(Component.literal("Left Eye"), 2, 25.5f, -3.4f, slots(RobosurgeonBlockEntity.SLOT_EYES)),
                new TargetMarker(Component.literal("Right Eye"), -2f, 25.5f, -3.4f, slots(RobosurgeonBlockEntity.SLOT_EYES)),
                new TargetMarker(Component.literal("Brain"), -0.13f, 27.56f, 1.52f, slots(RobosurgeonBlockEntity.SLOT_BRAIN))
        )),

        TORSO(0, -54, 26, 32, 0, 120, 120f, List.of(
                new TargetMarker(Component.literal("Heart"), 0f, 21f, -0.5f, slots(RobosurgeonBlockEntity.SLOT_HEART)),
                new TargetMarker(Component.literal("Left Lung"), 2.3f, 20f, 0, slots(RobosurgeonBlockEntity.SLOT_LUNGS)),
                new TargetMarker(Component.literal("Stomach"), 0.0f, 16f, -1.5f, slots(RobosurgeonBlockEntity.SLOT_STOMACH)),
                new TargetMarker(Component.literal("Right Lung"), -2.3f, 20f, 0f, slots(RobosurgeonBlockEntity.SLOT_LUNGS))
        )),

        ARM_LEFT(18, -54, 12, 34, -60, 120, 120f, List.of(
                new TargetMarker(Component.literal("Left Arm"), 4.7f, 21.0f, -0, slots(RobosurgeonBlockEntity.SLOT_ARMS)),
                new TargetMarker(Component.literal("Left Hand"), 5.8f, 14f, 0f, slots(RobosurgeonBlockEntity.SLOT_HANDS))
        )),

        ARM_RIGHT(-18, -54, 12, 34, 60, 120, 120f, List.of(
                new TargetMarker(Component.literal("Right Arm"), -4.7f, 21.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_ARMS)),
                new TargetMarker(Component.literal("Right Hand"), -5.8f, 14f, 0f, slots(RobosurgeonBlockEntity.SLOT_HANDS))
        )),

        LEG_LEFT(5, -19, 12, 38, -50, 20, 120f, List.of(
                new TargetMarker(Component.literal("Left Leg"), 2f, 10.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_LEGS)),
                new TargetMarker(Component.literal("Left Foot"), 2.1f, 3.9f, 0f, slots(RobosurgeonBlockEntity.SLOT_BOOTS))
        )),

        LEG_RIGHT(-5, -19, 12, 38, 50, 20, 120f, List.of(
                new TargetMarker(Component.literal("Right Leg"), -2f, 10.0f, 0f, slots(RobosurgeonBlockEntity.SLOT_LEGS)),
                new TargetMarker(Component.literal("Right Foot"), -2.1f, 3.9f, 0f, slots(RobosurgeonBlockEntity.SLOT_BOOTS))
        )),

        INTERNAL(0, 0, 40, 50, 48, 130, 150f, List.of(
                new TargetMarker(Component.literal("Skin"), -3.0f, 24.6f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_SKIN)),
                new TargetMarker(Component.literal("Muscle"), -0, 22.7f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_MUSCLE)),
                new TargetMarker(Component.literal("Bone"), 3.0f, 20.8f, -5.5f, slots(RobosurgeonBlockEntity.SLOT_BONES))
        )),

        NONE(0, 0, 0, 0, 0, 0, 45f, List.of());
        final int hitX, hitY, hitW, hitH;

        final int zoomOffsetX, zoomOffsetY;

        final float zoomScale;

        final List<TargetMarker> markers;

        BodyPart(int hitX, int hitY, int hitW, int hitH, int zoomOffsetX, int zoomOffsetY, float zoomScale, List<TargetMarker> markers) {
            this.hitX = hitX;
            this.hitY = hitY;
            this.hitW = hitW;
            this.hitH = hitH;
            this.zoomOffsetX = zoomOffsetX;
            this.zoomOffsetY = zoomOffsetY;
            this.zoomScale = zoomScale;
            this.markers = markers;

        }
    }

    public record TargetMarker(
            Component name,
            float modelX,
            float modelY,
            float modelZ,
            int[] relatedSlots
    ) {
    }
}