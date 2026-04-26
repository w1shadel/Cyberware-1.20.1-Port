package com.maxwell.cyber_ware_port.common.block.cyberskull;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.client.ModClientEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class CyberSkullItemRenderer implements SpecialModelRenderer<CyberSkullItemRenderer.Argument> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_wither_skeleton.png");
    private final SkullModel model;
    private final RenderType renderType;

    public CyberSkullItemRenderer(SkullModel model) {
        this.model = model;
        this.renderType = RenderTypes.entityCutoutZOffset(TEXTURE);
    }

    @Override
    public @Nullable Argument extractArgument(ItemStack stack) {
        return new Argument(0.0F);
    }

    @Override
    public void submit(@Nullable Argument argument, PoseStack poseStack, SubmitNodeCollector collector,
                       int light, int overlay, boolean hasOutline, int outlineColor) {
        float anim = argument != null ? argument.animationProgress() : 0.0F;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        SkullBlockRenderer.submitSkull(
                anim,
                poseStack,
                collector,
                light,
                this.model,
                this.renderType,
                outlineColor,
                null
        );
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
    }

    public record Argument(float animationProgress) {
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Argument> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<Argument> bake(BakingContext context) {
            SkullModel model = new SkullModel(context.entityModelSet().bakeLayer(ModClientEvents.CYBER_SKULL_LAYER));
            return new CyberSkullItemRenderer(model);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}