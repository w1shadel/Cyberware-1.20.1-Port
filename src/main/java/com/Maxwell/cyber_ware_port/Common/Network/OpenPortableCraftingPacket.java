package com.Maxwell.cyber_ware_port.Common.Network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OpenPortableCraftingPacket {
    public OpenPortableCraftingPacket() {
    }

    public static OpenPortableCraftingPacket fromBytes(FriendlyByteBuf buf) {
        return new OpenPortableCraftingPacket();

    }

    public static void handle(OpenPortableCraftingPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ContainerLevelAccess portableAccess = new ContainerLevelAccess() {
                    @Override
                    public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> function) {
                        return Optional.empty();

                    }

                    @Override
                    public void execute(BiConsumer<Level, BlockPos> consumer) {
                        consumer.accept(player.level(), player.blockPosition());

                    }
                };
                player.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new CraftingMenu(id, inv, portableAccess),
                        Component.translatable("container.crafting")
                ));

            }
        });
        ctx.get().setPacketHandled(true);

    }

    public void toBytes(FriendlyByteBuf buf) {
    }
}