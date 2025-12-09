package com.Maxwell.cyber_ware_port.Datagen;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CyberWare.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // ★★★ ここが最も重要 ★★★
        // あなたのフェンスブロックを、バニラの "fences" タグに追加します。
        // これにより、他のフェンスがあなたのブロックを仲間として認識するようになります。
        this.tag(BlockTags.FENCES)
                .add(ModBlocks.RADIO_TOWER_COMPONENT.get());

        // ツルハシで破壊可能にするためのタグ (すでにあるなら不要)
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.RADIO_TOWER_COMPONENT.get());

        // 適切なツールレベルを要求するタグ (鉄のツルハシ以上など)
        // 例えば鉄ツールレベルなら...
        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.RADIO_TOWER_COMPONENT.get());
    }
}