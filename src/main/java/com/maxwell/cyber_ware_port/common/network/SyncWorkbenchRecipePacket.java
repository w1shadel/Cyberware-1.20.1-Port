package com.maxwell.cyber_ware_port.common.network;
import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.container.CyberwareWorkbenchMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record SyncWorkbenchRecipePacket(
        List<SizedIngredientDisplay> ingredients,
        float deconstructChance
) implements CustomPacketPayload {

    public static final Type<SyncWorkbenchRecipePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CyberWare.MODID, "sync_workbench_recipe"));

    public static record SizedIngredientDisplay(ItemStack item, int count) {
        public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredientDisplay> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, SizedIngredientDisplay::item,
                ByteBufCodecs.VAR_INT, SizedIngredientDisplay::count,
                SizedIngredientDisplay::new
        );
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWorkbenchRecipePacket> STREAM_CODEC = StreamCodec.composite(
            SizedIngredientDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), SyncWorkbenchRecipePacket::ingredients,
            ByteBufCodecs.FLOAT, SyncWorkbenchRecipePacket::deconstructChance,
            SyncWorkbenchRecipePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}