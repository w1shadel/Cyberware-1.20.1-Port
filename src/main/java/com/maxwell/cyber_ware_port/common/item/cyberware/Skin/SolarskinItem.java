package com.maxwell.cyber_ware_port.common.item.cyberware.skin;

import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.capability.CyberwareCapabilityProvider;
import com.maxwell.cyber_ware_port.common.capability.CyberwareUserData;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SolarskinItem extends CyberwareItem {
    private static final int GENERATION_AMOUNT = 2;

    public SolarskinItem(Properties p) {
        super(new Builder(p,4, RobosurgeonBlockEntity.SLOT_SKIN)
                .maxInstall(4)
                .energy(0, GENERATION_AMOUNT, 0, StackingRule.LINEAR)
        );
    }

    @Override
    public void onSystemTick(LivingEntity wearer, ItemStack stack) {
        Level level = wearer.level();
        long time = level.getGameTime() % 24000;
        boolean isDaytime = time < 12500 || time > 23500;
        if (level.dimensionType().hasSkyLight() && isDaytime && !level.isRaining() && level.canSeeSky(wearer.blockPosition())) {
            int unitGeneration = this.getEnergyGeneration(stack);
            int totalGeneration = unitGeneration * stack.getCount();
            if (totalGeneration > 0) {
                CyberwareUserData data = wearer.getData(CyberwareCapabilityProvider.CYBERWARE_DATA.get());
                data.receiveEnergy(totalGeneration, false);
            }
        }
    }
}