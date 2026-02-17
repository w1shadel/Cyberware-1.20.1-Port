package com.maxwell.cyber_ware_port.api.event;

import com.maxwell.cyber_ware_port.common.block.charger.ChargerBlockEntity;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberwareWorkbenchBlockEntity;
import com.maxwell.cyber_ware_port.common.block.scanner.ScannerBlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class CyberwareEvents {
    @Cancelable
    public static class Recharge extends Event {
        private final LivingEntity entity;
        private final ChargerBlockEntity tile;
        private final boolean isDrainOperation;

        public Recharge(LivingEntity entity, ChargerBlockEntity tile, boolean isDrain) {
            this.entity = entity;
            this.tile = tile;
            this.isDrainOperation = isDrain;
        }

        public LivingEntity getEntity() {
            return entity;
        }

        public ChargerBlockEntity getTile() {
            return tile;
        }

        public boolean isDrainOperation() {
            return isDrainOperation;
        }
    }

    public static class Salvage extends Event {
        private final CyberwareWorkbenchBlockEntity tile;
        private final ItemStack inputStack;

        protected Salvage(CyberwareWorkbenchBlockEntity tile, ItemStack inputStack) {
            this.tile = tile;
            this.inputStack = inputStack;
        }

        public CyberwareWorkbenchBlockEntity getTile() {
            return tile;
        }

        public ItemStack getInputStack() {
            return inputStack;
        }

        @Cancelable
        public static class Pre extends Salvage {
            private float blueprintChance;

            public Pre(CyberwareWorkbenchBlockEntity tile, ItemStack inputStack, float blueprintChance) {
                super(tile, inputStack);
                this.blueprintChance = blueprintChance;
            }

            public float getBlueprintChance() {
                return blueprintChance;
            }

            public void setBlueprintChance(float chance) {
                this.blueprintChance = chance;
            }
        }

        public static class Post extends Salvage {
            private final List<ItemStack> outputs;

            public Post(CyberwareWorkbenchBlockEntity tile, ItemStack inputStack, List<ItemStack> outputs) {
                super(tile, inputStack);
                this.outputs = outputs;
            }

            public List<ItemStack> getOutputs() {
                return outputs;
            }
        }
    }

    public static class Scan extends Event {
        private final ScannerBlockEntity tile;
        private final ItemStack inputStack;

        protected Scan(ScannerBlockEntity tile, ItemStack inputStack) {
            this.tile = tile;
            this.inputStack = inputStack;
        }

        @Cancelable
        public static class Complete extends Scan {
            private float chance;
            private boolean consumeItem;

            public Complete(ScannerBlockEntity tile, ItemStack inputStack, float defaultChance) {
                super(tile, inputStack);
                this.chance = defaultChance;
                this.consumeItem = true;
            }

            public float getChance() {
                return chance;
            }

            public void setChance(float chance) {
                this.chance = chance;
            }

            public boolean shouldConsumeItem() {
                return consumeItem;
            }

            public void setConsumeItem(boolean consume) {
                this.consumeItem = consume;
            }
        }
    }
}