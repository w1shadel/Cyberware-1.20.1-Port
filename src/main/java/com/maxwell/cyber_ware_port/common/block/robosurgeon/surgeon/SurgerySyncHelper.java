package com.maxwell.cyber_ware_port.common.block.robosurgeon.surgeon;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class SurgerySyncHelper {
    public static boolean updateGhosts(ItemStacksResourceHandler body, ItemStacksResourceHandler table) {
        boolean changed = false;
        for (int i = 0; i < table.size(); i++) {
            ItemStack b = body.getResource(i).toStack(body.getAmountAsInt(i));
            ItemStack t = table.getResource(i).toStack(table.getAmountAsInt(i));
            if (SurgeryManager.isGhost(t)) {
                if (b.isEmpty()) {
                    table.set(i, ItemResource.EMPTY, 0);
                    changed = true;
                } else {
                    ItemStack ghost = createGhost(b);
                    if (!ItemStack.matches(t, ghost)) {
                        table.set(i, ItemResource.of(ghost), ghost.getCount());
                        changed = true;
                    }
                }
            } else if (t.isEmpty() && !b.isEmpty()) {
                ItemStack ghost = createGhost(b);
                table.set(i, ItemResource.of(ghost), ghost.getCount());
                changed = true;
            } else if (!t.isEmpty() && !b.isEmpty() && ItemStack.matches(t, b)) {
                ItemStack ghost = createGhost(b);
                table.set(i, ItemResource.of(ghost), ghost.getCount());
                changed = true;
            }
        }
        return changed;
    }

    private static ItemStack createGhost(ItemStack stack) {
        ItemStack ghost = stack.copy();
        ghost.set(CyberWare.GHOST_COMPONENT.get(), true);
        return ghost;
    }
}