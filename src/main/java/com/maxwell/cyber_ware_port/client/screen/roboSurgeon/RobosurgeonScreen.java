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
import com.maxwell.cyber_ware_port.common.network.SurgeryGhostTogglePacket;
import com.maxwell.cyber_ware_port.common.risk.SurgeryAlert;
import com.maxwell.cyber_ware_port.common.risk.SurgeryAnalyzer;
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
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.lang.reflect.Field;
import java.util.List;

public class RobosurgeonScreen extends AbstractContainerScreen<RobosurgeonMenu> {
    private static final ResourceLocation INTERNAL_PARTS_TEXTURE = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/player_internal_part.png");
    private static final ResourceLocation SKELETON_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/surgery.png");
    private static final ResourceLocation MARKER_TEXTURE = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/marker.png");
    private static final ResourceLocation RED_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/red_slot.png");
    private static final ResourceLocation BLUE_SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/blue_slot.png");
    private static final ResourceLocation ALERT_ICON = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/gui/risk_icons.png");
    private static final float ANIMATION_DURATION = 2000f;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_SPACING = 2;
    private static final int GUI_WIDTH = 175;
    private static final int TOP_HEIGHT = 131;
    private static final int BOTTOM_HEIGHT = 91;
    private static final int TEXTURE_INVENTORY_START_Y = 131;
    private static final float BASE_SCALE = 45f;
    private static final Field slotX, slotY;

    static {
        try {
            slotX = Slot.class.getDeclaredField("x");
            slotX.setAccessible(true);
            slotY = Slot.class.getDeclaredField("y");
            slotY.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

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
        pGuiGraphics.pose().mulPose((new Matrix4f()).scaling((float) pScale, (float) pScale, (float) (-pScale)));
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
        float originalLimbSwingAmount = pEntity.walkAnimation.speed();
        pEntity.walkAnimation.setSpeed(0.0f);
        pEntity.attackAnim = 0.0f;
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        rotation.conjugate();
        entityrenderdispatcher.overrideCameraOrientation(rotation);
        entityrenderdispatcher.setRenderShadow(false);
        entityrenderdispatcher.render(pEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, pGuiGraphics.pose(), pGuiGraphics.bufferSource(), 15728880);
        pGuiGraphics.flush();
        entityrenderdispatcher.setRenderShadow(true);
        pEntity.yBodyRot = f2;
        pEntity.setYRot(f3);
        pEntity.setXRot(f4);
        pEntity.yHeadRotO = f5;
        pEntity.yHeadRot = f6;
        pEntity.walkAnimation.setSpeed(originalLimbSwingAmount);
        pGuiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    private static int[] slots(int start) {
        int[] slots = new int[9];
        for (int i = 0; i < 9; i++) {
            slots[i] = start + i;
        }
        return slots;
    }

    public static void renderCustomModel(GuiGraphics pGuiGraphics, int pX, int pY, int pScale, float rotationYaw, Model pModel, ResourceLocation texture) {
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float) pX, (float) pY, 50.0F);
        pGuiGraphics.pose().mulPose((new Matrix4f()).scaling((float) pScale, (float) pScale, (float) (-pScale)));
        Quaternionf quaternionf = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf rotation = Axis.YP.rotationDegrees(rotationYaw + 180.0F);
        quaternionf.mul(rotation);
        pGuiGraphics.pose().mulPose(quaternionf);
        Lighting.setupForEntityInInventory();
        VertexConsumer vertexConsumer = pGuiGraphics.bufferSource().getBuffer(pModel.renderType(texture));
        pModel.renderToBuffer(pGuiGraphics.pose(), vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
        pGuiGraphics.flush();
        pGuiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    private void setSlotPos(Slot slot, int x, int y) {
        try {
            slotX.set(slot, x);
            slotY.set(slot, y);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft != null) {
            this.skeletonModel = new SkeletonDisplayModel(this.minecraft.getEntityModels().bakeLayer(SkeletonDisplayModel.LAYER_LOCATION));
            this.internalPartsModel = new PlayerInternalPartsModel(this.minecraft.getEntityModels().bakeLayer(PlayerInternalPartsModel.LAYER_LOCATION));
        }
        this.startTime = System.currentTimeMillis();
        int listBtnX = this.leftPos + 158;
        int listBtnY = this.topPos + 4;
        this.installedListButton = new AbstractWidget(listBtnX, listBtnY, 10, 10, Component.empty()) {
            @Override
            public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 176, 122, 10, 10, 256, 256);
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
            protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
                this.defaultButtonNarrationText(narrationElementOutput);
            }
        };
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
                    if (pMouseX >= slotLeft && pMouseX < slotLeft + 16 && pMouseY >= slotTop && pMouseY < slotTop + 16) {
                        hoveredSlot = slot;
                        break;
                    }
                }
                if (hoveredSlot != null && hoveredSlot.index < RobosurgeonBlockEntity.TOTAL_SLOTS) {
                    if (hoveredSlot.hasItem() && hoveredSlot.getItem().getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                        PacketDistributor.sendToServer(new SurgeryGhostTogglePacket(this.menu.blockEntity.getBlockPos(), hoveredSlot.index));
                        hoveredSlot.set(ItemStack.EMPTY);
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
                        return true;
                    } else if (!hoveredSlot.hasItem()) {
                        PacketDistributor.sendToServer(new SurgeryGhostTogglePacket(this.menu.blockEntity.getBlockPos(), hoveredSlot.index));
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
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
                    float screenOffsetX = (marker.modelX() * cos) - (marker.modelZ() * sin);
                    int markerX = modelCenterX + (int) (screenOffsetX * scaleFactor) - 8;
                    int markerY = modelCenterY - (int) (marker.modelY() * scaleFactor) - 8;
                    if (pMouseX >= markerX && pMouseX < markerX + 16 && pMouseY >= markerY && pMouseY < markerY + 16) {
                        this.selectedMarker = marker;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.2F));
                        return true;
                    }
                }
            }
            if (this.selectedPart == BodyPart.NONE) {
                int x = (this.width - this.imageWidth) / 2;
                int y = (this.height - this.imageHeight) / 2;
                int subBaseX = x + 40;
                int subBaseY = y + TOP_HEIGHT - 55;
                if (pMouseX >= subBaseX - 18.5 && pMouseX <= subBaseX + 18.5 && pMouseY >= subBaseY - 18.5 && pMouseY <= subBaseY + 18.5) {
                    this.selectedPart = BodyPart.INTERNAL;
                    this.hideName = true;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
                    return true;
                }
            }
            if (this.menu.getCarried().isEmpty() && this.getSlotUnderMouse() == null) {
                if (pMouseX >= this.leftPos && pMouseX < this.leftPos + this.imageWidth && pMouseY >= this.topPos && pMouseY < this.topPos + TOP_HEIGHT) {
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
        if (pButton != 0) return super.mouseReleased(pMouseX, pMouseY, pButton);
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
                    if (pMouseX >= entityX + part.hitX - (part.hitW / 2.0) && pMouseX <= entityX + part.hitX + (part.hitW / 2.0) &&
                            pMouseY >= entityY + part.hitY - (part.hitH / 2.0) && pMouseY <= entityY + part.hitY + (part.hitH / 2.0)) {
                        this.selectedPart = part;
                        this.hideName = true;
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
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
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        updateSlotPositions();
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int maxTolerance = 100;
        if (this.minecraft != null && this.minecraft.player != null) {
            maxTolerance = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get()).getMaxTolerance(this.minecraft.player);
        }
        SurgeryAlert alert = SurgeryAnalyzer.check(this.menu.slots, maxTolerance);
        if (alert != null) {
            int iconX = this.leftPos + 155;
            int iconY = this.topPos + 20;
            pGuiGraphics.blit(ALERT_ICON, iconX, iconY, 0, 0, 16, 16, 16, 16);
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
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.8F);
            for (TargetMarker marker : this.selectedPart.markers) {
                float screenOffsetX = (marker.modelX() * cos) - (marker.modelZ() * sin);
                int markerX = modelCenterX + (int) (screenOffsetX * scaleFactor) - 8;
                int markerY = modelCenterY - (int) (marker.modelY() * scaleFactor) - 8;
                pGuiGraphics.blit(MARKER_TEXTURE, markerX, markerY, 0, 0, 16, 16, 16, 16);
                if (pMouseX >= markerX && pMouseX < markerX + 16 && pMouseY >= markerY && pMouseY < markerY + 16) {
                    pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                    pGuiGraphics.renderTooltip(this.font, marker.name(), pMouseX, pMouseY);
                    pGuiGraphics.fill(markerX, markerY, markerX + 16, markerY + 16, 0x50FFFFFF);
                    pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 0.8F);
                }
            }
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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
            for (int i = 0; i < slotCount; i++) {
                int slotX = uiX + (i * (SLOT_SIZE + SLOT_SPACING));
                pGuiGraphics.blit(BLUE_SLOT_TEXTURE, slotX - 1, stagingY - 1, 0, 0, 18, 18, 18, 18);
                pGuiGraphics.blit(RED_SLOT_TEXTURE, slotX - 1, installedY - 2, 0, 0, 18, 18, 18, 18);
            }
            RenderSystem.disableBlend();
            if (this.minecraft != null && this.minecraft.player != null) {
                CyberwareUserData cyberware = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                IItemHandler installed = cyberware.getInstalledCyberware();
                for (int i = 0; i < slotCount; i++) {
                    int slotId = this.selectedMarker.relatedSlots()[i];

                    int itemX = uiX + (i * (SLOT_SIZE + SLOT_SPACING));

                    if (slotId < installed.getSlots()) {
                        ItemStack installedStack = installed.getStackInSlot(slotId);

                        pGuiGraphics.renderItem(installedStack, itemX, installedY - 1);
                        pGuiGraphics.renderItemDecorations(this.font, installedStack, itemX, installedY - 1);

                        if (this.menu.getSlot(slotId).getItem().isEmpty() && !installedStack.isEmpty()) {
                            pGuiGraphics.pose().pushPose();
                            pGuiGraphics.pose().translate(0, 0, 150);

                            pGuiGraphics.renderItem(installedStack, itemX, stagingY );
                            pGuiGraphics.fill(itemX, stagingY, itemX + 16, stagingY + 16, 0x80000000);

                            pGuiGraphics.pose().popPose();
                        }
                    }
                }
            }
            renderGhostConflict(pGuiGraphics, slotCount, uiX, stagingY, installedY);
            pGuiGraphics.pose().popPose();
        }
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partial, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        g.blit(TEXTURE, x, y, 0, 0, GUI_WIDTH, TOP_HEIGHT);
        g.blit(TEXTURE, x, y + TOP_HEIGHT, 0, TEXTURE_INVENTORY_START_Y, GUI_WIDTH, BOTTOM_HEIGHT);

        int maxEssence = 100, currentEssence = 0;
        CyberwareUserData data = null;
        if (this.minecraft.player != null) {
            data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
            maxEssence = data.getMaxTolerance(this.minecraft.player);
            currentEssence = data.getTolerance(this.minecraft.player);
        }

        int barX = x + 5, barY = y + 4, barW = 8, barH = 48;


        g.blit(TEXTURE, barX, barY, 211.0F, 61.0F, barW, barH, 256, 256);

        if (data != null) {
            int futureEssence = getProjectedFutureEssence(data);

            if (futureEssence < currentEssence) {


                float flash = 0.5f + 0.3f * (float) Math.sin((System.currentTimeMillis() % 1000) / 1000f * 2 * Math.PI);
                g.setColor(1.0f, 0.6f, 0.0f, flash);
                drawEssenceBar(g, currentEssence, maxEssence, barX, barY, barW, barH);
                g.setColor(1.0f, 1.0f, 1.0f, 1.0f); 

                drawEssenceBar(g, futureEssence, maxEssence, barX, barY, barW, barH);
            } else if (futureEssence > currentEssence) {


                float flash = 0.5f + 0.3f * (float) Math.sin((System.currentTimeMillis() % 1000) / 1000f * 2 * Math.PI);
                g.setColor(0.2f, 1.0f, 0.2f, flash);
                drawEssenceBar(g, futureEssence, maxEssence, barX, barY, barW, barH);
                g.setColor(1.0f, 1.0f, 1.0f, 1.0f); 

                drawEssenceBar(g, currentEssence, maxEssence, barX, barY, barW, barH);
            } else {

                drawEssenceBar(g, currentEssence, maxEssence, barX, barY, barW, barH);
            }
        }

        float spd = 0.05f;
        currentScale += ((selectedPart == BodyPart.NONE ? BASE_SCALE : selectedPart.zoomScale) - currentScale) * spd;
        currentOffsetX += ((selectedPart == BodyPart.NONE ? 0 : selectedPart.zoomOffsetX) - currentOffsetX) * spd;
        currentOffsetY += ((selectedPart == BodyPart.NONE ? 0 : selectedPart.zoomOffsetY) - currentOffsetY) * spd;
        int drawX = (int) (x + 88 + currentOffsetX), drawY = (int) (y + TOP_HEIGHT - 15 + currentOffsetY);
        if (!this.hideName && this.minecraft.player != null) {
            String name = "_" + this.minecraft.player.getName().getString().toUpperCase();
            g.drawString(this.font, name, drawX - this.font.width(name) / 2, drawY, 0x00FFFF, true);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        float ease = (elapsed < ANIMATION_DURATION) ? (1f - (float) Math.pow(1f - Math.min(elapsed / ANIMATION_DURATION, 1f), 3)) : 1.0f;
        float currentRotation;
        if (elapsed < ANIMATION_DURATION) {
            currentRotation = ease * 360f;
            this.viewRotation = currentRotation;
        } else {
            currentRotation = this.viewRotation;
        }

        if (this.internalPartsModel != null && this.selectedPart != BodyPart.ARM_LEFT && this.selectedPart != BodyPart.ARM_RIGHT && this.selectedPart != BodyPart.LEG_LEFT && this.selectedPart != BodyPart.LEG_RIGHT && this.selectedPart != BodyPart.HEAD && this.selectedPart != BodyPart.TORSO) {
            int subX = (this.selectedPart == BodyPart.INTERNAL) ? drawX - 48 : x + 40;
            int subY = (this.selectedPart == BodyPart.INTERNAL) ? drawY : y + TOP_HEIGHT - 21;
            int subScale = (this.selectedPart == BodyPart.INTERNAL) ? (int) currentScale : 40;
            if (this.selectedPart == BodyPart.NONE) {
                int boxX = subX - 18, boxY = subY - 52;
                g.renderOutline(boxX, boxY, 37, 37, 0xFF00FFFF);
                g.fill(boxX + 28, boxY + 18, drawX, boxY + 19, 0xFF00FFFF);
            }
            for (int i = 0; i < 3; i++) {
                this.internalPartsModel.setVisibleLayer(i);
                int renderY = subY + (this.selectedPart == BodyPart.NONE ? 17 : 0);
                renderCustomModel(g, subX, renderY, subScale, currentRotation, this.internalPartsModel, INTERNAL_PARTS_TEXTURE);
            }
        }
        if (this.selectedPart != BodyPart.INTERNAL) {
            double guiScale = this.minecraft.getWindow().getGuiScale();
            int modelH = 115;
            int scan = (int) (modelH * ease);
            if (!isAnimating(elapsed)) {
                if (skeletonModel != null) {
                    RenderSystem.enableScissor((int) ((x + 5) * guiScale), (int) ((this.height - (y + TOP_HEIGHT)) * guiScale), (int) ((GUI_WIDTH - 10) * guiScale), (int) ((TOP_HEIGHT - 10) * guiScale));
                    renderCustomModel(g, drawX, drawY, (int) (currentScale * 0.933f) + 5, currentRotation, skeletonModel, SKELETON_TEXTURE);
                    RenderSystem.disableScissor();
                }
            } else if (this.minecraft.player != null) {
                int scX = (int) ((drawX - 50) * guiScale), scW = (int) (100 * guiScale), scFeet = (int) ((this.height - drawY) * guiScale);
                if (modelH - scan > 0) {
                    RenderSystem.enableScissor(scX, scFeet, scW, (int) ((modelH - scan) * guiScale));
                    renderEntityWithRotation(g, drawX, drawY, 50, currentRotation, this.minecraft.player);
                    RenderSystem.disableScissor();
                }
                if (skeletonModel != null && scan > 0) {
                    RenderSystem.enableScissor(scX, scFeet + (int) ((modelH - scan) * guiScale), scW, (int) (scan * guiScale));
                    renderCustomModel(g, drawX, drawY, 47, currentRotation, skeletonModel, SKELETON_TEXTURE);
                    RenderSystem.disableScissor();
                }
                g.blit(TEXTURE, drawX - 40, (drawY - modelH) + scan, 176, 110, 80, 1);
            }
        }
    }

    private boolean isAnimating(long elapsed) {
        return elapsed < ANIMATION_DURATION;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (this.minecraft == null || this.minecraft.player == null) return;
        CyberwareUserData data = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
        int maxTolerance = data.getMaxTolerance(this.minecraft.player), currentCost = 0;
        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            ItemStack stack = this.menu.getSlot(i).getItem();
            ICyberware cw = CyberwareAPI.getCyberware(stack);
            if (cw != null) currentCost += cw.getEssenceCost(stack) * stack.getCount();
        }
        int remaining = maxTolerance - currentCost;
        pGuiGraphics.drawString(this.font, remaining + " / " + maxTolerance, 18, 6, (remaining < 0) ? 0xAA0000 : (remaining < 25 ? 0xFF5555 : 0x00FFFF), true);
    }

    private void updateSlotPositions() {
        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            if (i < this.menu.slots.size()) setSlotPos(this.menu.slots.get(i), 20000, 20000);
        }
        if (this.selectedMarker != null) {
            int[] targets = this.selectedMarker.relatedSlots();
            int uiWidth = (SLOT_SIZE * targets.length) + (SLOT_SPACING * (targets.length - 1));
            int uiX = (this.width - uiWidth) / 2;
            for (int i = 0; i < targets.length; i++) {
                if (targets[i] < this.menu.slots.size()) {
                    int slotXVal = uiX - this.leftPos + (i * (SLOT_SIZE + SLOT_SPACING));
                    int slotYVal = 105;
                    setSlotPos(this.menu.slots.get(targets[i]), slotXVal, slotYVal);
                }
            }
        }
    }
    private void renderGhostConflict(GuiGraphics g, int slotCount, int uiX, int stagingY, int installedY) {
        ItemStack carried = this.menu.getCarried();
        if (carried.isEmpty()) return;
        ICyberware carriedCw = CyberwareAPI.getCyberware(carried);
        if (carriedCw == null) return;
        IItemHandler installed = this.minecraft.player.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get()).getInstalledCyberware();
        int[] related = this.selectedMarker.relatedSlots();
        for (int j = 0; j < slotCount; j++) {
            int targetId = related[j];
            ItemStack other = this.menu.getSlot(targetId).getItem();
            boolean isStaging = !other.isEmpty();
            if (other.isEmpty() && targetId < installed.getSlots()) other = installed.getStackInSlot(targetId);
            if (!other.isEmpty()) {
                ICyberware otherCw = CyberwareAPI.getCyberware(other);
                if (otherCw != null && ((carriedCw.getBodyPartType(carried) != com.maxwell.cyber_ware_port.common.item.base.BodyPartType.NONE && carriedCw.getBodyPartType(carried) == otherCw.getBodyPartType(other)) || carriedCw.isIncompatible(carried, other) || otherCw.isIncompatible(other, carried))) {

                    int x = uiX + (j * (SLOT_SIZE + SLOT_SPACING)) - 1;
                    int y = isStaging ? stagingY - 1 : installedY - 1;

                    g.pose().pushPose();
                    g.pose().translate(0, 0, 400);
                    g.fill(x, y, x + 18, y + 18, 0x80FF0000);
                    g.renderOutline(x, y, 18, 18, 0xFFFF0000);
                    g.drawString(this.font, "!", x + 7, y + 5, 0xFFFF0000, true);
                    g.pose().popPose();
                }
            }
        }
    }

    private void drawEssenceBar(GuiGraphics g, int essence, int maxEssence, int x, int y, int w, int h) {
        // 修正：ブレンド機能を明示的に有効化
        // これにより、赤・青のバーのアルファ透過（半透明）が有効になり、
        // 背後にある背景の「水色・灰色」の縞模様が美しく透けて見えるようになります
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int danger = (int) (maxEssence * 0.25f);
        int rH = (int) (h * ((float) Math.min(Math.max(0, essence), danger) / maxEssence));
        int bH = (int) (h * ((float) Math.max(0, essence - danger) / maxEssence));

        if (rH > 0) {
            g.blit(TEXTURE, x, y + (h - rH), w, rH, 220, 61 + (48 - rH), w, rH, 256, 256);
        }
        if (bH > 0) {
            g.blit(TEXTURE, x, y + (h - rH - bH), w, bH, 176, 61 + (48 - (rH + bH)), w, bH, 256, 256);
        }

        // 描画後はブレンド機能を無効化（他のGUIへの影響を防ぐため）
        RenderSystem.disableBlend();
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


    private int getProjectedFutureEssence(CyberwareUserData data) {
        if (this.minecraft == null || this.minecraft.player == null) return 100;
        net.neoforged.neoforge.items.ItemStackHandler playerBody = data.getInstalledCyberware();
        int futureCost = 0;

        for (int i = 0; i < RobosurgeonBlockEntity.TOTAL_SLOTS; i++) {
            ItemStack tableStack = this.menu.getSlot(i).getItem();
            ItemStack finalStack;

            if (tableStack.isEmpty()) {

                finalStack = ItemStack.EMPTY;
            } else if (tableStack.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {

                finalStack = playerBody.getStackInSlot(i);
            } else {

                finalStack = tableStack;
            }

            ICyberware cw = CyberwareAPI.getCyberware(finalStack);
            if (cw != null) {
                futureCost += cw.getEssenceCost(finalStack) * finalStack.getCount();
            }
        }

        int maxTolerance = data.getMaxTolerance(this.minecraft.player);
        return Math.max(0, maxTolerance - futureCost);
    }
}