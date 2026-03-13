package com.maxwell.cyber_ware_port.common.network;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenPortableCraftingPacket() implements CustomPacketPayload {
    public static final Type<OpenPortableCraftingPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "open_portable_crafting"));

    public static final StreamCodec<FriendlyByteBuf, OpenPortableCraftingPacket> STREAM_CODEC = StreamCodec.unit(new OpenPortableCraftingPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                player.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new CraftingMenu(id, inv, ContainerLevelAccess.create(player.level(), player.blockPosition())),
                        Component.translatable("container.crafting")
                ));
            }
        });
    }
}