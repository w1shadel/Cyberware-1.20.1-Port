package com.maxwell.cyber_ware_port.common.item.cyberware.Cranium;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class CorticalStackItem extends CyberwareItem {
    public CorticalStackItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_BRAIN).maxInstall(1).incompatible(ModItems.CONSCIOUSNESS_TRANSMITTER));
    }

    @Override
    public void onLivingDeath(LivingDeathEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer instanceof Player player) {
            int totalXp = getTotalXp(player);
            if (totalXp > 0) {
                ItemStack capsule = new ItemStack(ModItems.EXP_CAPSULE.get());
                capsule.getOrCreateTag().putInt("xp", totalXp);
                player.drop(capsule, true);
                player.totalExperience = 0;
                player.experienceLevel = 0;
                player.experienceProgress = 0;
            }
        }
    }

    private int getTotalXp(Player player) {
        return (int) (player.experienceLevel * 7 + player.experienceProgress * player.getXpNeededForNextLevel());
    }
}