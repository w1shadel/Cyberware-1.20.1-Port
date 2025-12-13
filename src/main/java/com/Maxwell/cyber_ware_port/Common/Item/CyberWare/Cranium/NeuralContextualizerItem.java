package com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class NeuralContextualizerItem extends CyberwareItem {
    public NeuralContextualizerItem() {
        super(new Builder(5, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)
                .energy(1, 0, 0, StackingRule.STATIC));

    }

    @Override
    public boolean canToggle(ItemStack stack) {
        return true;
    }

    @Override
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, ItemStack stack, LivingEntity wearer) {
        if (!(wearer instanceof Player player) || player.isCreative()) return;
        if (!player.level().isClientSide) {
            net.minecraft.world.level.block.state.BlockState state = player.level().getBlockState(event.getPos());
            ItemStack currentStack = player.getMainHandItem();
            if (currentStack.getDestroySpeed(state) > 1.0f) {
                return;
            }
            int bestSlot = -1;
            float bestSpeed = 1.0f;
            for (int i = 0; i < 9; i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                float speed = invStack.getDestroySpeed(state);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
            if (bestSlot != -1 && bestSlot != player.getInventory().selected) {
                player.getInventory().selected = bestSlot;
                ((ServerPlayer) player).connection.send(new net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket(bestSlot));
            }
        }
    }
}