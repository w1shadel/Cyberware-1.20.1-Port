package com.Maxwell.cyber_ware_port.Datagen;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CyberWare.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // ==========================================
        // 1. 通常のアイテム (中古版が存在しないもの)
        // ==========================================
        simpleItem(ModItems.BLUEPRINT);
        simpleItem(ModItems.EXP_CAPSULE);
        simpleItem(ModItems.CREATIVE_BATTERY);

        // 人間のパーツ
        simpleItem(ModItems.HUMAN_BRAIN);
        simpleItem(ModItems.HUMAN_HEART);
        simpleItem(ModItems.HUMAN_STOMACH);
        simpleItem(ModItems.HUMAN_SKIN);
        simpleItem(ModItems.HUMAN_MUSCLE);
        simpleItem(ModItems.HUMAN_BONE);
        simpleItem(ModItems.HUMAN_EYES);
        simpleItem(ModItems.HUMAN_LUNGS);
        simpleItem(ModItems.HUMAN_LEFT_ARM);
        simpleItem(ModItems.HUMAN_RIGHT_ARM);
        simpleItem(ModItems.HUMAN_LEFT_HAND);
        simpleItem(ModItems.HUMAN_RIGHT_HAND);
        simpleItem(ModItems.HUMAN_LEFT_LEG);
        simpleItem(ModItems.HUMAN_RIGHT_LEG);
        simpleItem(ModItems.HUMAN_LEFT_FOOT);
        simpleItem(ModItems.HUMAN_RIGHT_FOOT);

        // ==========================================
        // 2. サイバーウェア (中古版画像があるもの)
        //    これらは自動で2つのJSONが生成されます
        // ==========================================
        cyberwareItem(ModItems.CYBER_EYE);
        cyberwareItem(ModItems.LOW_LIGHT_VISION);
        cyberwareItem(ModItems.LIQUID_REFRACTION);
        cyberwareItem(ModItems.HUDJACK);
        cyberwareItem(ModItems.TARGETING_OVERLAY);
        cyberwareItem(ModItems.DISTANCE_ENHANCER);

        cyberwareItem(ModItems.COMPRESSED_OXYGEN);
        cyberwareItem(ModItems.HYPER_OXYGENATION);

        cyberwareItem(ModItems.LIVER_FILTER);
        cyberwareItem(ModItems.METABOLIC_GENERATOR);
        cyberwareItem(ModItems.INTERNAL_BATTERY);
        cyberwareItem(ModItems.ADRENALINE_PUMP);

        cyberwareItem(ModItems.CORTICAL_STACK);
        cyberwareItem(ModItems.ENDER_JAMMER);
        cyberwareItem(ModItems.CONSCIOUSNESS_TRANSMITTER);
        cyberwareItem(ModItems.NEURAL_CONTEXTUALIZER);
        cyberwareItem(ModItems.THREAT_MATRIX);
        cyberwareItem(ModItems.CRANIAL_BROADCASTER);

        cyberwareItem(ModItems.CARDIOMECHANIC_PUMP);
        cyberwareItem(ModItems.INTERNAL_DEFIBRILLATOR);
        cyberwareItem(ModItems.PLATELET_DISPATCHER);
        cyberwareItem(ModItems.STEM_CELL_SYNTHESIZER);
        cyberwareItem(ModItems.CARDIOVASCULAR_COUPLER);

        cyberwareItem(ModItems.SOLARSKIN);
        cyberwareItem(ModItems.SUBDERMAL_SPIKES);
        cyberwareItem(ModItems.SYNTHETIC_SKIN);
        cyberwareItem(ModItems.TARGETED_IMMUNOSUPPRESSANT);

        cyberwareItem(ModItems.WIRED_REFLEXES);
        cyberwareItem(ModItems.MYOMER_MUSCLE);

        cyberwareItem(ModItems.BONELACING);
        cyberwareItem(ModItems.CITRATE_ENHANCEMENT);
        cyberwareItem(ModItems.MARROW_BATTERY);

        cyberwareItem(ModItems.CYBER_ARM_LEFT);
        cyberwareItem(ModItems.CYBER_ARM_RIGHT);
        cyberwareItem(ModItems.RETRACTABLE_CLAWS);
        cyberwareItem(ModItems.REINFORCED_FIST);

        cyberwareItem(ModItems.CYBER_LEG_LEFT);
        cyberwareItem(ModItems.CYBER_LEG_RIGHT);
        cyberwareItem(ModItems.LINEAR_ACTUATORS);
        cyberwareItem(ModItems.FALL_BRACERS);
        cyberwareItem(ModItems.AQUATIC_PROPULSION);
        cyberwareItem(ModItems.DEPLOYABLE_WHEELS);
    }

    /**
     * 通常のアイテムモデルを生成します
     */
    private void simpleItem(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(CyberWare.MODID, "item/" + item.getId().getPath()));
    }

    /**
     * サイバーウェア用のモデルを生成します。
     * 1. 中古版のモデル (_scavenged) を生成
     * 2. 通常版のモデルを生成し、is_scavenged=1 の時に中古版へ切り替えるロジックを埋め込む
     */
    private void cyberwareItem(RegistryObject<Item> item) {
        String path = item.getId().getPath();
        ResourceLocation standardTexture = new ResourceLocation(CyberWare.MODID, "item/" + path);
        ResourceLocation scavengedTexture = new ResourceLocation(CyberWare.MODID, "item/" + path + "_scavenged");

        // 1. 中古版モデルの生成 (生成されたビルダーを変数に保持)
        ItemModelBuilder scavengedModel = withExistingParent(path + "_scavenged", new ResourceLocation("item/generated"))
                .texture("layer0", scavengedTexture);

        // 2. 通常版モデルの生成 (Overrideロジック付き)
        withExistingParent(path, new ResourceLocation("item/generated"))
                .texture("layer0", standardTexture)
                .override()
                // プロパティ "is_scavenged" が 1.0 の場合
                .predicate(new ResourceLocation(CyberWare.MODID, "is_scavenged"), 1.0f)
                // 上で作った scavengedModel を使用する
                .model(scavengedModel)
                .end();
    }
}