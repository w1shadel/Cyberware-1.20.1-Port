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

            if (!t.isEmpty() && !t.getOrDefault(CyberWare.GHOST_COMPONENT.get(), false)) {
                continue;
            }

            if (b.isEmpty()) {

                if (!t.isEmpty()) {
                    table.set(i, ItemResource.EMPTY, 0);
                    changed = true;
                }
            } else {

                ItemStack ghost = createGhost(b);


                boolean currentIsRemoving = t.getOrDefault(CyberWare.REMOVAL_COMPONENT.get(), false);


                ghost.set(CyberWare.REMOVAL_COMPONENT.get(), currentIsRemoving);

                if (!ItemStack.matches(t, ghost)) {

                    ItemStack freshGhost = createGhost(b);
                    table.set(i, ItemResource.of(freshGhost), freshGhost.getCount());
                    changed = true;
                }
            }
        }
        return changed;
    }

    private static ItemStack createGhost(ItemStack stack) {
        ItemStack ghost = stack.copy();
        ghost.set(CyberWare.GHOST_COMPONENT.get(), true);

        ghost.set(CyberWare.REMOVAL_COMPONENT.get(), false);
        return ghost;
    }
}