package com.maxwell.cyber_ware_port.common.item.cyberware.cranium;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class CorticalStackItem extends CyberwareItem {
    public CorticalStackItem() {
        super(new Builder(10, RobosurgeonBlockEntity.SLOT_BRAIN)
                .maxInstall(1)
                .incompatible(ModItems.CONSCIOUSNESS_TRANSMITTER));
    }

    @Override
    public void onLivingDeath(LivingDeathEvent event, ItemStack stack, LivingEntity wearer) {
        if (wearer instanceof Player player && !player.level().isClientSide) {
            int totalXp = getTotalXp(player);
            if (totalXp > 0) {
                ItemStack capsule = new ItemStack(ModItems.EXP_CAPSULE.get());
                CompoundTag tag = new CompoundTag();
                tag.putInt("xp", totalXp);
                capsule.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
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