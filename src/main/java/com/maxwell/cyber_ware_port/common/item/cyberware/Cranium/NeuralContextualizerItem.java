package com.maxwell.cyber_ware_port.common.item.cyberware.cranium;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class NeuralContextualizerItem extends CyberwareItem {
    public NeuralContextualizerItem(Properties p) {
        super(new Builder(p,5, RobosurgeonBlockEntity.SLOT_BRAIN)
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

        if (player instanceof ServerPlayer serverPlayer) {
            BlockState state = player.level().getBlockState(event.getPos());
            ItemStack currentStack = player.getMainHandItem();

            if (currentStack.getDestroySpeed(state) > 1.1f) {
                return;
            }

            int bestSlot = -1;
            float bestSpeed = 1.1f;

            for (int i = 0; i < 9; i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                float speed = invStack.getDestroySpeed(state);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }

            if (bestSlot != -1 && bestSlot != player.getInventory().getSelectedSlot()) {
                player.getInventory().setSelectedSlot(bestSlot);

            }
        }
    }
}