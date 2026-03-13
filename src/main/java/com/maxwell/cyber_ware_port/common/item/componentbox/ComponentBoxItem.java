package com.maxwell.cyber_ware_port.common.item.componentbox;

import com.maxwell.cyber_ware_port.common.container.ComponentBoxMenu;
import com.maxwell.cyber_ware_port.init.ModBlocks;
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
import org.jetbrains.annotations.NotNull;

public class ComponentBoxItem extends BlockItem {
    public ComponentBoxItem() {
        super(ModBlocks.COMPONENT_BOX.get(), new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.isShiftKeyDown()) {
            return this.use(context.getLevel(), player, context.getHand()).getResult();
        }
        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer) {
            if (!pPlayer.isShiftKeyDown()) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new ComponentBoxMenu(id, inv, p.getItemInHand(pHand)),
                        Component.translatable("item.cyber_ware_port.component_box")
                ), buf -> buf.writeBoolean(pHand == InteractionHand.MAIN_HAND));
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }
}