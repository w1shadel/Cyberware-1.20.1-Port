package com.maxwell.cyber_ware_port.common.item;

import com.maxwell.cyber_ware_port.common.container.ComponentBoxMenu;
import com.maxwell.cyber_ware_port.init.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ComponentBoxItem extends BlockItem {
    public ComponentBoxItem(Properties p) {
        super(ModBlocks.COMPONENT_BOX.get(), p);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.isShiftKeyDown()) {
            return this.use(context.getLevel(), player, context.getHand());
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        if (!pLevel.isClientSide() && pPlayer instanceof ServerPlayer serverPlayer) {
            if (!pPlayer.isShiftKeyDown()) {
                serverPlayer.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new ComponentBoxMenu(id, inv, p.getItemInHand(pHand)),
                        Component.translatable("item.cyber_ware_port.component_box")
                ), buf -> {
                    buf.writeBoolean(false);
                    buf.writeBoolean(pHand == InteractionHand.MAIN_HAND);
                });
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}