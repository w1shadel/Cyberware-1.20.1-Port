package com.maxwell.cyber_ware_port.common.block.robosurgeon.surgeon;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.api.json.CyberwareAPI;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SurgeryManager {
    public static void execute(ServerPlayer player, ItemStacksResourceHandler table, ItemStacksResourceHandler body) {
        List<ItemStack> ejectList = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            ItemStack tableStack = table.getResource(i).toStack(table.getAmountAsInt(i));
            if (isGhost(tableStack)) {
                continue;
            }
            ItemStack oldPart = body.getResource(i).toStack(body.getAmountAsInt(i));
            if (!oldPart.isEmpty()) {
                ejectList.add(oldPart.copy());
            }
            ItemResource resource = table.getResource(i);
            int amount = table.getAmountAsInt(i);
            if (!resource.isEmpty() && amount > 0) {
                try (Transaction tx = Transaction.openRoot()) {
                    int extractedCount = table.extract(i, resource, amount, tx);
                    tx.commit();
                    ItemStack insertedDeducted = resource.toStack(extractedCount);
                    body.set(i, ItemResource.of(insertedDeducted), insertedDeducted.getCount());
                }
            } else {
                body.set(i, ItemResource.EMPTY, 0);
            }
        }
        resolveConflicts(body, ejectList);
        for (ItemStack stack : ejectList) {
            if (stack.isEmpty()) continue;
            if (!player.getInventory().add(stack)) {
                ItemEntity itemEntity = player.drop(stack, false);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                    itemEntity.setUnlimitedLifetime();
                }
            }
        }
    }

    private static void resolveConflicts(ItemStacksResourceHandler body, List<ItemStack> ejectList) {
        for (int i = 0; i < body.size(); i++) {
            ItemStack s1 = body.getResource(i).toStack(body.getAmountAsInt(i));
            ICyberware cw1 = CyberwareAPI.getCyberware(s1);
            if (cw1 == null) continue;
            for (int j = i + 1; j < body.size(); j++) {
                ItemStack s2 = body.getResource(j).toStack(body.getAmountAsInt(j));
                ICyberware cw2 = CyberwareAPI.getCyberware(s2);
                if (cw2 == null) continue;
                boolean conflict = false;
                if (cw1.getBodyPartType(s1) != BodyPartType.NONE && cw1.getBodyPartType(s1) == cw2.getBodyPartType(s2)) {
                    conflict = true;
                }
                if (!conflict && (cw1.isIncompatible(s1, s2) || cw2.isIncompatible(s2, s1))) {
                    conflict = true;
                }
                if (conflict) {
                    int q1 = cw1.getQuality(s1);
                    int q2 = cw2.getQuality(s2);
                    int loserIndex = (q1 > q2) ? j : i;
                    ItemStack loserStack = body.getResource(loserIndex).toStack(body.getAmountAsInt(loserIndex));
                    ejectList.add(loserStack.copy());
                    body.set(loserIndex, ItemResource.EMPTY, 0);
                    if (loserIndex == i) break;
                }
            }
        }
    }

    public static boolean isGhost(ItemStack s) {
        return !s.isEmpty() && s.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false);
    }
}