package com.maxwell.cyber_ware_port.common.block.robosurgeon.surgeon;

import com.maxwell.cyber_ware_port.CyberWare;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

public class SurgerySyncHelper {
    public static boolean updateGhosts(ItemStackHandler body, IItemHandlerModifiable table) {
        boolean changed = false;
        for (int i = 0; i < table.getSlots(); i++) {
            ItemStack b = body.getStackInSlot(i);
            ItemStack t = table.getStackInSlot(i);
            if (SurgeryManager.isGhost(t)) {
                if (b.isEmpty()) {
                    table.setStackInSlot(i, ItemStack.EMPTY);
                    changed = true;
                } else {
                    ItemStack ghost = createGhost(b);
                    if (!ItemStack.matches(t, ghost)) {
                        table.setStackInSlot(i, ghost);
                        changed = true;
                    }
                }
            } else if (t.isEmpty() && !b.isEmpty()) {
                table.setStackInSlot(i, createGhost(b));
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