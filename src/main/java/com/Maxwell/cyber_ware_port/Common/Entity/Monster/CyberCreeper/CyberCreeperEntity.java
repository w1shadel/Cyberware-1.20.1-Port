package com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberCreeper;

import com.Maxwell.cyber_ware_port.Common.Block.Radio.TemptToBlockGoal;
import com.Maxwell.cyber_ware_port.Common.Entity.ICyberwareMob;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class CyberCreeperEntity extends Creeper implements ICyberwareMob {

    private boolean isCausingCustomExplosion = false;

    public CyberCreeperEntity(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }
    @Override
    public List<Item> getSpecialDrops() {
        return Arrays.asList(
                ModItems.SOLARSKIN.get(),
                ModItems.SUBDERMAL_SPIKES.get(),
                ModItems.SYNTHETIC_SKIN.get(),
                ModItems.TARGETED_IMMUNOSUPPRESSANT.get()
        );
    }
    public void setCausingCustomExplosion(boolean isCausing) {
        this.isCausingCustomExplosion = isCausing;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new TemptToBlockGoal(this, 1.0D, ModBlocks.RADIO_KIT_BLOCK.get(), 64));
    }

    public boolean isCausingCustomExplosion() {
        return this.isCausingCustomExplosion;
    }
}