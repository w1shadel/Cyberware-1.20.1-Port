package com.Maxwell.cyber_ware_port.Common.Item.ComponentBox;import com.Maxwell.cyber_ware_port.Common.Container.ComponentBoxMenu;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;public class ComponentBoxItem extends BlockItem {

    public ComponentBoxItem() {
        super(ModBlocks.COMPONENT_BOX.get(), new Properties().stacksTo(1));

    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();if (player != null && player.isShiftKeyDown()) {
            return super.useOn(context);

        }return this.use(context.getLevel(), player, context.getHand()).getResult();

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {

            if (!pPlayer.isShiftKeyDown()) {
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                        (id, inv, player) -> new ComponentBoxMenu(id, inv, player.getItemInHand(pHand)),
                        Component.translatable("item.cyber_ware_port.component_box")
                ), buf -> {
                    buf.writeBoolean(pHand == InteractionHand.MAIN_HAND);

                });

                return InteractionResultHolder.success(pPlayer.getItemInHand(pHand));

            }
        }return InteractionResultHolder.pass(pPlayer.getItemInHand(pHand));

    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ComponentBoxCapabilityProvider(stack);

    }
}