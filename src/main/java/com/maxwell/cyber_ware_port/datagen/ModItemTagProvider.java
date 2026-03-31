package com.maxwell.cyber_ware_port.datagen;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.base.ICyberware;
import com.maxwell.cyber_ware_port.common.util.ModTags;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, CyberWare.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        ModItems.ITEMS.getEntries().forEach(holder -> {
            Item item = holder.get();
            if (item instanceof ICyberware cw) {

                this.tag(ModTags.Items.CYBERWARE).add(item);

                int slot = cw.getSlot(ItemStack.EMPTY);
                int slotsPerPart = RobosurgeonBlockEntity.SLOTS_PER_PART;

                if (slot >= RobosurgeonBlockEntity.SLOT_EYES && slot < RobosurgeonBlockEntity.SLOT_EYES + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_EYES).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_BRAIN && slot < RobosurgeonBlockEntity.SLOT_BRAIN + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_BRAIN).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_HEART && slot < RobosurgeonBlockEntity.SLOT_HEART + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_HEART).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_LUNGS && slot < RobosurgeonBlockEntity.SLOT_LUNGS + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_LUNGS).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_STOMACH && slot < RobosurgeonBlockEntity.SLOT_STOMACH + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_STOMACH).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_SKIN && slot < RobosurgeonBlockEntity.SLOT_SKIN + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_SKIN).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_MUSCLE && slot < RobosurgeonBlockEntity.SLOT_MUSCLE + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_MUSCLE).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_BONES && slot < RobosurgeonBlockEntity.SLOT_BONES + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_BONES).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_ARMS && slot < RobosurgeonBlockEntity.SLOT_ARMS + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_ARMS).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_HANDS && slot < RobosurgeonBlockEntity.SLOT_HANDS + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_HANDS).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_LEGS && slot < RobosurgeonBlockEntity.SLOT_LEGS + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_LEGS).add(item);
                } else if (slot >= RobosurgeonBlockEntity.SLOT_BOOTS && slot < RobosurgeonBlockEntity.SLOT_BOOTS + slotsPerPart) {
                    this.tag(ModTags.Items.CYBERWARE_BOOTS).add(item);
                }
            }
        });
    }
}
