package com.maxwell.cyber_ware_port.common.block.robosurgeon.surgeon;


import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

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
                } else if (!ItemStack.matches(t, createGhost(b))) {
                    table.setStackInSlot(i, createGhost(b));
                    changed = true;
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
        ghost.getOrCreateTag().putBoolean("cyberware_ghost", true);
        return ghost;
    }
}