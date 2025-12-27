package com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper;

import com.maxwell.cyber_ware_port.common.entity.ICyberwareMob;
import com.maxwell.cyber_ware_port.init.ModItems;
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

    public boolean isCausingCustomExplosion() {
        return this.isCausingCustomExplosion;

    }

    public void setCausingCustomExplosion(boolean isCausing) {
        this.isCausingCustomExplosion = isCausing;

    }
}