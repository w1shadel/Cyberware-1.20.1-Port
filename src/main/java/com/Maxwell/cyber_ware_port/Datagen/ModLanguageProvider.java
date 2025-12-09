package com.Maxwell.cyber_ware_port.Datagen;

import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlocks;
import com.Maxwell.cyber_ware_port.Init.ModEntities;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {
    private final String locale;

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, CyberWare.MODID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        if (locale.equals("en_us")) {

            add(ModItems.CYBER_EYE.get(), "Cyber Eyes");
            add(ModItems.BLUEPRINT.get(), "Blueprint");
            add(ModItems.EXP_CAPSULE.get(), "Experience Capsule");

            add(ModItems.CORTICAL_STACK.get(), "Cortical Stack");
            add(ModItems.ENDER_JAMMER.get(), "Ender Jammer");
            add(ModItems.CONSCIOUSNESS_TRANSMITTER.get(), "Consciousness Transmitter");
            add(ModItems.NEURAL_CONTEXTUALIZER.get(), "Neural Contextualizer");
            add(ModItems.THREAT_MATRIX.get(), "Threat Matrix Calculator");
            add(ModItems.CRANIAL_BROADCASTER.get(), "Cranial Broadcaster");

            add(ModItems.CARDIOMECHANIC_PUMP.get(), "Cardiomechanic Pump");
            add(ModItems.INTERNAL_DEFIBRILLATOR.get(), "Internal Defibrillator");
            add(ModItems.PLATELET_DISPATCHER.get(), "Platelet Dispatcher");
            add(ModItems.STEM_CELL_SYNTHESIZER.get(), "Stem Cell Synthesizer");
            add(ModItems.CARDIOVASCULAR_COUPLER.get(), "Cardiovascular Coupler");

            add(ModItems.COMPRESSED_OXYGEN.get(), "Compressed Oxygen Implant");
            add(ModItems.HYPER_OXYGENATION.get(), "Hyperoxygenation Boost");
            add(ModItems.LIVER_FILTER.get(), "Liver Filter");
            add(ModItems.METABOLIC_GENERATOR.get(), "Metabolic Generator");
            add(ModItems.INTERNAL_BATTERY.get(), "Internal Battery");
            add(ModItems.ADRENALINE_PUMP.get(), "Adrenaline Pump");
            add(ModItems.CREATIVE_BATTERY.get(), "Creative Capacitor");

            add("itemGroup.cyber_ware_port.items", "Cyberware");

            add("block.cyber_ware_port.robo_surgeon", "Robo Surgeon");
            add("block.cyber_ware_port.surgery_chamber", "Surgery Chamber");
            add("block.cyber_ware_port.cyberware_workbench", "Cyberware Workbench");
            add("block.cyber_ware_port.scanner", "Scanner");

            add("gui.cyber_ware_port.need_paper", "Need Paper.");
            add("gui.cyber_ware_port.assemble", "Assemble");
            add("gui.cyber_ware_port.deconstruct", "Deconstruct");
            add("gui.cyber_ware_port.blueprint_chance", "%s%% chance to get com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWitherSkelton.a blueprint");
            add("gui.cyber_ware_port.scanner.chance", "[%s%% Chance]");
            add("gui.cyber_ware_port.installed_cyberware.title", "Post-Surgery Cyberware Status");
            add("gui.cyber_ware_port.button.view_installed", "Index");
            add("gui.cyber_ware_port.menu", "Cyberware Menu");

            add("key.cyber_ware_port.menu", "Open Cyberware Menu");
            add("key.categories.cyber_ware_port", "Cyberware");

            add("cyberware_slot.cyber_ware_port.eyes", "Eyes");
            add("cyberware_slot.cyber_ware_port.brain", "Cranium");
            add("cyberware_slot.cyber_ware_port.heart", "Heart");
            add("cyberware_slot.cyber_ware_port.lungs", "Lungs");
            add("cyberware_slot.cyber_ware_port.stomach", "Abdominal Organs");
            add("cyberware_slot.cyber_ware_port.arms", "Arms");
            add("cyberware_slot.cyber_ware_port.hands", "Hands");
            add("cyberware_slot.cyber_ware_port.legs", "Legs");
            add("cyberware_slot.cyber_ware_port.boots", "Feet");
            add("cyberware_slot.cyber_ware_port.skin", "Skin");
            add("cyberware_slot.cyber_ware_port.muscle", "Muscles");
            add("cyberware_slot.cyber_ware_port.bones", "Bones");
            add("cyberware_slot.cyber_ware_port.unknown", "Unknown");

            add("cyberware.tooltip.shiftPrompt", "Hold %s for more info");
            add("cyberware.tooltip.essence", "Essence Cost: %s");
            add("cyberware.tooltip.capacity", "Power Capacity: %s FE");
            add("cyberware.tooltip.powerConsumption", "Power Usage: %s FE/t");
            add("cyberware.tooltip.powerProduction", "Power Gen: %s FE/t");
            add("cyberware.tooltip.maxInstall", "Max Install: %s");
            add("cyberware.tooltip.requires", "Requires:");
            add("cyberware.tooltip.incompatible", "Incompatible with:");
            add("cyberware.quality.manufactured", "Manufactured");
            add("cyberware.quality.scavenged", "Salvaged");
            add("cyberware.tooltip.status", "Status: %s");

            add("cyberware.tooltip.exp_capsule", "Contains preserved experience.");

            add("cyberware.tooltip.cybereyes", "Immunity to blindness.\nFoundation for eye upgrades.");
            add("cyberware.tooltip.cybereye_upgrades_night_vision", "Enhanced low-light vision.\n(Toggleable)");
            add("cyberware.tooltip.cybereye_upgrades_underwater_vision", "Clear underwater vision.\n(Toggleable)");
            add("cyberware.tooltip.cybereye_upgrades_hudjack", "Displays internal power info on HUD.");
            add("cyberware.tooltip.cybereye_upgrades_targeting", "Highlights nearby creatures.\n(Toggleable)");
            add("cyberware.tooltip.cybereye_upgrades_zoom", "Zoom in on distant objects.\n(Toggleable)");

            add("cyberware.tooltip.lungs_upgrades_oxygen", "Additional oxygen supply.");
            add("cyberware.tooltip.lungs_upgrades_hyperoxygenation", "Increases sprinting speed.");

            add("cyberware.tooltip.lower_organs_upgrades_liver_filter", "Neutralizes negative potion effects.");
            add("cyberware.tooltip.lower_organs_upgrades_metabolic", "Converts food into power.");
            add("cyberware.tooltip.lower_organs_upgrades_battery", "Stores power.");
            add("cyberware.tooltip.lower_organs_upgrades_adrenaline", "Boosts stats at low health.");
            add("cyberware.tooltip.creative_battery", "Limitless potential.");

            add("cyberware.tooltip.brain_upgrades_cortical_stack", "Saves XP in com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWitherSkelton.a capsule upon death.");
            add("cyberware.tooltip.brain_upgrades_ender_jammer", "Blocks ender teleportation nearby.");
            add("cyberware.tooltip.brain_upgrades_consciousness_transmitter", "Retains some XP after death.");
            add("cyberware.tooltip.brain_upgrades_neural_contextualizer", "Automatically switches to the right tool.");
            add("cyberware.tooltip.brain_upgrades_matrix", "Chance to dodge attacks when unarmored.");
            add("cyberware.tooltip.brain_upgrades_radio", "Slightly increases Cyberzombie spawns.");

            add("cyberware.tooltip.cyberheart", "Immunity to Weakness.");
            add("cyberware.tooltip.heart_upgrades_defibrillator", "Prevents death once (Requires Power).");
            add("cyberware.tooltip.heart_upgrades_platelets", "Heals when close to full health.");
            add("cyberware.tooltip.heart_upgrades_medkit", "Regenerates health over time.");
            add("cyberware.tooltip.heart_upgrades_coupler", "Generates power from heartbeat.");

            add(ModItems.SOLARSKIN.get(), "Solarskin");
            add(ModItems.SUBDERMAL_SPIKES.get(), "Subdermal Spikes");
            add(ModItems.SYNTHETIC_SKIN.get(), "Synthetic Skin");
            add(ModItems.TARGETED_IMMUNOSUPPRESSANT.get(), "Targeted Immunosuppressant");

            add(ModItems.WIRED_REFLEXES.get(), "Wired Reflexes");
            add(ModItems.MYOMER_MUSCLE.get(), "Myomer Muscle Replacement");

            add(ModItems.BONELACING.get(), "Bonelacing");
            add(ModItems.CITRATE_ENHANCEMENT.get(), "Citrate Enhancement");
            add(ModItems.MARROW_BATTERY.get(), "Marrow Battery");

            add("cyberware.tooltip.ghost.remove", "Click to remove");
            add("cyberware.tooltip.ghost.add", "Click to install %s");

            add("cyberware.tooltip.skin_upgrades_solar_skin", "Generates power in sunlight.");
            add("cyberware.tooltip.skin_upgrades_subdermal_spikes", "Damages attackers when unarmored.");
            add("cyberware.tooltip.skin_upgrades_synthetic_skin", "Cosmetic: Hides cyberlimbs.");
            add("cyberware.tooltip.skin_upgrades_immuno", "Increases tolerance but reduces immunity.");

            add("cyberware.tooltip.muscle_upgrades_wired_reflexes", "Increases attack speed.\nAuto-faces attacker.");
            add("cyberware.tooltip.muscle_upgrades_muscle_replacements", "Boosts movement speed and strength.");

            add("cyberware.tooltip.bone_upgrades_bonelacing", "Increases maximum health.");
            add("cyberware.tooltip.bone_upgrades_boneflex", "Reduces fall damage.");
            add("cyberware.tooltip.bone_upgrades_bonebattery", "Stores power in your bones.");

        } else if (locale.equals("ja_jp")) {

            add("itemGroup.cyber_ware_port.items", "サイバーウェア");
            add(ModItems.KATANA.get(), "カタナ");
            add(ModItems.CYBER_WITHER_SKELETON_SKULL_ITEM.get(),"サイバーウィザースケルトンの頭");
            add(ModEntities.CYBER_CREEPER.get(),"サイバークリーパー");
            add(ModEntities.CYBER_WITHER.get(),"サイバーウィザー");
            add(ModEntities.CYBER_WITHER_SKELETON.get(),"サイバーウィザースケルトン");
            add(ModEntities.CYBER_SKELETON.get(),"サイバースケルトン");
            add(ModEntities.CYBER_ZOMBIE.get(),"サイバーゾンビ");
            add(ModBlocks.ROBO_SURGEON.get(), "ロボ外科医");
            add(ModBlocks.RADIO_KIT_BLOCK.get(), "ラジオキット");
            add(ModBlocks.RADIO_TOWER_CORE.get(), "ラジオビーコン");
            add(ModBlocks.RADIO_TOWER_COMPONENT.get(), "ラジオタワーの構成部品");
            add(ModBlocks.SURGERY_CHAMBER.get(), "手術室");
            add("message.cyber_ware_port.no_hud_installed","サイバーアイをインストールしてください。");
            add(ModBlocks.CYBERWARE_WORKBENCH.get(), "サイバーウェア工作台");
            add(ModBlocks.COMPONENT_BOX.get(), "部品ボックス");
            add("item.cyber_ware_port.component_box", "部品ボックス");
            add(ModBlocks.BLUEPRINT_CHEST.get(), "設計図保管棚");
            add(ModBlocks.SCANNER.get(), "スキャナー");
            add(ModItems.BLUEPRINT.get(), "空白の設計図");
            add(ModItems.EXP_CAPSULE.get(), "経験値カプセル");
            add(ModItems.CREATIVE_BATTERY.get(), "クリエイティブ蓄電器");
            add(ModItems.COMPONENT_ACTUATOR.get(), "駆動装置");
            add(ModItems.COMPONENT_REACTOR.get(), "バイオリアクター");
            add(ModItems.COMPONENT_TITANIUM.get(), "チタンメッシュ");
            add(ModItems.COMPONENT_SSC.get(), "ソリッドステート回路");
            add(ModItems.COMPONENT_PLATING.get(), "クロムメッキ");
            add(ModItems.COMPONENT_FIBEROPTICS.get(), "光ファイバー");
            add(ModItems.COMPONENT_FULLERENE.get(), "フラーレン微細構造体");
            add(ModItems.COMPONENT_SYNTHNERVES.get(), "合成神経");
            add(ModItems.COMPONENT_STORAGE.get(), "ストレージセル");
            add(ModItems.COMPONENT_MICROELECTRIC.get(), "微小電気セル");

            add(ModItems.HUMAN_BRAIN.get(), "人間の脳");
            add(ModItems.HUMAN_EYES.get(), "人間の目");
            add(ModItems.HUMAN_HEART.get(), "人間の心臓");
            add(ModItems.HUMAN_LUNGS.get(), "人間の肺");
            add(ModItems.HUMAN_STOMACH.get(), "人間の胃");
            add(ModItems.HUMAN_SKIN.get(), "人間の皮膚");
            add(ModItems.HUMAN_MUSCLE.get(), "人間の筋肉");
            add(ModItems.HUMAN_BONE.get(), "人間の骨格");
            add(ModItems.HUMAN_LEFT_ARM.get(), "人間の左腕");
            add(ModItems.HUMAN_RIGHT_ARM.get(), "人間の右腕");
            add(ModItems.HUMAN_LEFT_HAND.get(), "人間の左手");
            add(ModItems.HUMAN_RIGHT_HAND.get(), "人間の右手");
            add(ModItems.HUMAN_LEFT_LEG.get(), "人間の左脚部");
            add(ModItems.HUMAN_RIGHT_LEG.get(), "人間の右脚部");
            add(ModItems.HUMAN_LEFT_FOOT.get(), "人間の左足首");
            add(ModItems.HUMAN_RIGHT_FOOT.get(), "人間の右足首");add(ModItems.CYBER_EYE.get(), "サイバーアイ");
            add(ModItems.LOW_LIGHT_VISION.get(), "強化ローライトビジョン");
            add(ModItems.LIQUID_REFRACTION.get(), "液体屈折光矯正装置");
            add(ModItems.HUDJACK.get(), "HUDジャック");
            add(ModItems.TARGETING_OVERLAY.get(), "標的オーバーレイ");
            add(ModItems.DISTANCE_ENHANCER.get(), "望遠強化装置");

            add(ModItems.CORTICAL_STACK.get(), "脳皮質スタック装置");
            add(ModItems.ENDER_JAMMER.get(), "エンダージャマー");
            add(ModItems.CONSCIOUSNESS_TRANSMITTER.get(), "意識送信機");
            add(ModItems.NEURAL_CONTEXTUALIZER.get(), "ニューラル状況解釈装置");
            add(ModItems.THREAT_MATRIX.get(), "脅威マトリックス計算機");
            add(ModItems.CRANIAL_BROADCASTER.get(), "頭蓋放送局");

            add(ModItems.CARDIOMECHANIC_PUMP.get(), "心臓機能ポンプ");
            add(ModItems.INTERNAL_DEFIBRILLATOR.get(), "体内除細動器");
            add(ModItems.PLATELET_DISPATCHER.get(), "血小板急送装置");
            add(ModItems.STEM_CELL_SYNTHESIZER.get(), "幹細胞合成装置");
            add(ModItems.CARDIOVASCULAR_COUPLER.get(), "心血管カプラー");

            add(ModItems.COMPRESSED_OXYGEN.get(), "圧縮酸素インプラント");
            add(ModItems.HYPER_OXYGENATION.get(), "過酸素化ブースト");

            add(ModItems.LIVER_FILTER.get(), "肝臓フィルター");
            add(ModItems.METABOLIC_GENERATOR.get(), "メタボリック発電機");
            add(ModItems.INTERNAL_BATTERY.get(), "内臓バッテリー");
            add(ModItems.ADRENALINE_PUMP.get(), "アドレナリンポンプ");

            add(ModItems.SOLARSKIN.get(), "ソーラースキン");
            add(ModItems.SUBDERMAL_SPIKES.get(), "皮下スパイク");
            add(ModItems.SYNTHETIC_SKIN.get(), "合成皮膚");
            add(ModItems.TARGETED_IMMUNOSUPPRESSANT.get(), "標的免疫抑制装置");

            add(ModItems.WIRED_REFLEXES.get(), "強化反射神経");
            add(ModItems.MYOMER_MUSCLE.get(), "ミオマー筋肉代替物");

            add(ModItems.BONELACING.get(), "骨格接合具");
            add(ModItems.CITRATE_ENHANCEMENT.get(), "クエン酸塩強化");
            add(ModItems.MARROW_BATTERY.get(), "骨髄バッテリー");

            add(ModItems.CYBER_ARM_LEFT.get(), "左サイバーアーム");
            add(ModItems.CYBER_ARM_RIGHT.get(), "右サイバーアーム");
            add(ModItems.CYBER_LEG_LEFT.get(), "左サイバーレッグ");
            add(ModItems.CYBER_LEG_RIGHT.get(), "右サイバーレッグ");add(ModItems.FINE_MANIPULATORS.get(), "高精細マニピュレーター");
            add(ModItems.RETRACTABLE_CLAWS.get(), "引き込み式クロー");
            add(ModItems.REINFORCED_FIST.get(), "強化フィスト");
            add(ModItems.DENSE_BATTERY.get(),"超濃縮蓄電器");
            add(ModItems.RAPID_FIRE_FLYWHEEL.get(), "早射ちフライホイール");

            add(ModItems.IMPLANTED_SPURS.get(), "埋め込み式拍車");
            add(ModItems.AQUATIC_PROPULSION.get(), "水中推進システム");
            add(ModItems.DEPLOYABLE_WHEELS.get(), "展開式ホイール");
            add(ModItems.LINEAR_ACTUATORS.get(), "垂直駆動装置");
            add(ModItems.FALL_BRACERS.get(), "落下補助装置");

            add("gui.cyber_ware_port.scanner.chance", "[%s%% の確率]");
            add("gui.cyber_ware_port.need_paper", "紙が必要です。");
            add("gui.cyber_ware_port.assemble", "組み立て");
            add("gui.cyber_ware_port.deconstruct", "分解");
            add("gui.cyber_ware_port.blueprint_chance", "%s%%の確率で設計図を獲得");
            add("gui.cyber_ware_port.installed_cyberware.title", "サイバーウェア手術後の状態");
            add("gui.cyber_ware_port.button.view_installed", "索引");
            add("gui.cyber_ware_port.menu", "サイバーウェアメニュー");
            add("cyberware.tooltip.status", "状態: %s");
            add("cyberware.gui.active.enable", "有効化");
            add("cyberware.gui.active.disable", "無効化");
            add("cyberware.gui.active.zoom", "ズーム");
            add("cyberware.tooltip.ghost.remove", "クリックで削除");
            add("cyberware.tooltip.ghost.add", "クリックして%sを追加");

            add("key.cyber_ware_port.menu", "サイバーウェアメニューを開く");
            add("key.categories.cyber_ware_port", "サイバーウェア");

            add("cyberware_slot.cyber_ware_port.eyes", "眼球スロット");
            add("cyberware_slot.cyber_ware_port.brain", "頭蓋スロット");
            add("cyberware_slot.cyber_ware_port.heart", "心臓スロット");
            add("cyberware_slot.cyber_ware_port.lungs", "肺スロット");
            add("cyberware_slot.cyber_ware_port.stomach", "下腹部の臓器スロット");
            add("cyberware_slot.cyber_ware_port.arms", "腕部スロット");
            add("cyberware_slot.cyber_ware_port.hands", "手スロット");
            add("cyberware_slot.cyber_ware_port.legs", "脚部スロット");
            add("cyberware_slot.cyber_ware_port.boots", "足スロット");
            add("cyberware_slot.cyber_ware_port.skin", "皮膚スロット");
            add("cyberware_slot.cyber_ware_port.muscle", "筋肉スロット");
            add("cyberware_slot.cyber_ware_port.bones", "骨格スロット");
            add("cyberware_slot.cyber_ware_port.unknown", "不明");

            add("cyberware.tooltip.shiftPrompt", "スニークを押すと詳細");
            add("cyberware.tooltip.requires", "必須");
            add("cyberware.tooltip.incompatible", "競合パーツ:");
            add("cyberware.quality.manufactured", "新品");
            add("cyberware.quality.scavenged", "中古");
            add("cyberware.tooltip.essence", "耐性消費：%s");
            add("cyberware.tooltip.capacity", "%sパワーの蓄電容量");
            add("cyberware.tooltip.powerConsumption", "毎秒%sパワーを消費します");
            add("cyberware.tooltip.powerProduction", "毎秒%sパワーを発電します");
            add("cyberware.tooltip.maxInstall", "最大%sつまでインストールできます");
            add("cyberware.tooltip.slot", "%s");
            add("cyberware.tooltip.exp_capsule", "記録された経験値が貯められています");

            add("cyberware.tooltip.cyberlimbs_cyberarm_left", "機械化された腕。\n生身の腕よりも頑丈で、様々なアップグレードをインストールするためのプラットフォームとなる。");
            add("cyberware.tooltip.cyberlimbs_cyberarm_right", "機械化された腕。\n生身の腕よりも頑丈で、様々なアップグレードをインストールするためのプラットフォームとなる。");
            add("cyberware.tooltip.cyberlimbs_cyberleg_left", "機械化された脚部。\n生身の脚部よりも頑丈で、様々なアップグレードをインストールするためのプラットフォームとなる。");
            add("cyberware.tooltip.cyberlimbs_cyberleg_right", "機械化された脚部。\n生身の脚部よりも頑丈で、様々なアップグレードをインストールするためのプラットフォームとなる。");
            add("cyberware.tooltip.cybereyes", "盲目への耐性があります\nサイバーアイのアップグレードが可能になります");
            add("cyberware.tooltip.cybereye_upgrades_night_vision", "暗闇の中でも見えます");
            add("cyberware.tooltip.cybereye_upgrades_underwater_vision", "水中でも見えます");
            add("cyberware.tooltip.cybereye_upgrades_hudjack", "体内の電力情報を表示します\nHUDをオーバーレイするアップグレードが可能になります");
            add("cyberware.tooltip.cybereye_upgrades_targeting", "近くの生き物をハイライトします");
            add("cyberware.tooltip.cybereye_upgrades_zoom", "遠くの物体をズームできます");

            add("cyberware.tooltip.brain_upgrades_cortical_stack", "死亡時に経験値をカプセルに保存します");
            add("cyberware.tooltip.brain_upgrades_ender_jammer", "近くのエンダーテレポートをブロックします");
            add("cyberware.tooltip.brain_upgrades_consciousness_transmitter", "死亡時にXPをいくつか保管します");
            add("cyberware.tooltip.brain_upgrades_neural_contextualizer", "自動的に適正ツールに切り替えます");
            add("cyberware.tooltip.brain_upgrades_matrix", "軽装または防具が無い時高い確率で攻撃を避けます");
            add("cyberware.tooltip.brain_upgrades_radio", "近くのサイバーゾンビのスポーン率をわずかに増加させます");

            add("cyberware.tooltip.cyberheart", "弱体化への耐性があります");
            add("cyberware.tooltip.heart_upgrades_defibrillator", "一度だけ特定の死亡を防ぎます(要電力)");
            add("cyberware.tooltip.heart_upgrades_platelets", "体力が満タンに近い時回復します");
            add("cyberware.tooltip.heart_upgrades_medkit", "長めの待ち時間の後に直前のダメージを回復します");
            add("cyberware.tooltip.heart_upgrades_coupler", "人体の電気パルスを電力化します");

            add("cyberware.tooltip.lungs_upgrades_oxygen", "追加の酸素ゲージ、回復はゆっくりです");
            add("cyberware.tooltip.lungs_upgrades_hyperoxygenation", "スプリント中の追加スピード");

            add("cyberware.tooltip.lower_organs_upgrades_liver_filter", "飲食物からの悪い効果が無くなります");
            add("cyberware.tooltip.lower_organs_upgrades_metabolic", "カロリーを消費して発電します");
            add("cyberware.tooltip.lower_organs_upgrades_battery", "エネルギーを貯蔵します");
            add("cyberware.tooltip.lower_organs_upgrades_adrenaline", "体力が低いときに力とスピードが上昇します");
            add("cyberware.tooltip.creative_battery", "無限のポテンシャル");
            add("cyberware.tooltip.dense_battery", "大容量のバッテリー");
            add("cyberware.tooltip.skin_upgrades_solar_skin", "太陽の光を浴びると発電します");
            add("cyberware.tooltip.skin_upgrades_subdermal_spikes", "軽装または防具が無い時、攻撃した相手にダメージを与えます");
            add("cyberware.tooltip.skin_upgrades_fake_skin", "サイバーアーム・レッグを隠します");
            add("cyberware.tooltip.skin_upgrades_immuno", "免疫システムの反応を抑制します\n病気や毒への耐性が減少します");

            add("cyberware.tooltip.muscle_upgrades_wired_reflexes", "R1：攻撃速度が速くなります\nR2：攻撃を受けると武器を構えます\nR3：自動的に攻撃者の方を見ます");
            add("cyberware.tooltip.muscle_upgrades_muscle_replacements", "移動速度が速くなり、力が上昇します");

            add("cyberware.tooltip.bone_upgrades_bonelacing", "体力が上昇します");
            add("cyberware.tooltip.bone_upgrades_boneflex", "落下ダメージを弾力で吸収します");
            add("cyberware.tooltip.bone_upgrades_bonebattery", "エネルギーを貯蔵します");

            add("cyberware.tooltip.hand_upgrades_craft_hands", "作業台無しで3x3のクラフトができます");
            add("cyberware.tooltip.hand_upgrades_claws", "武器を持たない時の攻撃がより強くなります");
            add("cyberware.tooltip.hand_upgrades_mining", "素手で石レベルの採掘ができます");

            add("cyberware.tooltip.arm_upgrades_bow", "より速く弓を発射します");

            add("cyberware.tooltip.leg_upgrades_jump_boost", "より高くジャンプできます\nシフト＋ジャンプで自分を打ち上げます\n2つのサイバーレッグでさらにパワフルに");
            add("cyberware.tooltip.leg_upgrades_fall_damage", "小さな衝撃を吸収します");

            add("cyberware.tooltip.foot_upgrades_spurs", "自分をスピードアップ");
            add("cyberware.tooltip.foot_upgrades_aqua", "より速く泳ぎます、2つのサーバーレッグでさらにパワフルに");
            add("cyberware.tooltip.foot_upgrades_wheels", "１ブロックの段ならまたいで歩きます(自動ジャンプをオフにしてください)");

            add("death.attack.cyberware.brainless", "もしも%1$sが脳無しじゃなかったら・・・");
            add("death.attack.cyberware.heartless", "%1$sは心無いが故に死にました。");
            add("death.attack.cyberware.surgery", "%1$sはオペから生還しませんでした。");
            add("death.attack.cyberware.cyberware_missing_bone", "%1$sは「骨」のない軟弱者でした。");
            add("death.attack.cyberware.nomuscles", "%1$sはバラバラになった。");
            add("death.attack.cyberware.noessence", "%1$sの体が拒絶しました。");
            add("death.attack.cyberware.lowessence", "%1$sの体が拒絶しました。");
            add("cyberware.risk.missing_requirement", "エラー：必須パーツが不足しています");
            add("cyberware.risk.missing_brain", "警告：脳が欠損しています、思考ができません。");
            add("cyberware.risk.missing_bones_death", "警告：骨格が欠損しています、体形を維持できません。");

            add("cyberware.risk.missing_heart", "警告：心臓が欠損しています、血液が輸送できません。");
            add("cyberware.risk.zero_tolerance", "警告：耐性値が枯渇しています、ショック死します。");
            add("cyberware.risk.missing_lungs", "危険：肺が欠損しています、呼吸できません。");
            add("cyberware.risk.missing_skin", "危険：皮膚が欠損しています、受けるダメージが上昇します。");
            add("cyberware.risk.missing_arm_right", "注意：右腕がありません。");
            add("cyberware.risk.missing_arm_left", "注意：左腕がありません。");
            add("cyberware.risk.missing_legs_both", "注意：両足がありません、正常な移動は不可能です。");
            add("cyberware.risk.missing_legs_single", "注意：片足がありません、移動速度が低下します。");
            add("cyberware.risk.missing_stomach", "注意：胃が欠損しています、食事ができません。");
            add("cyberware.risk.missing_muscle", "注意：筋肉が欠損しています、攻撃力が低下します。");
            add("cyberware.risk.missing_bones", "注意：骨格が欠損しています、受けるダメージが上昇します。");
            add("cyberware.risk.missing_eyes", "注意：眼球が欠損しています、失明します。");
            add("cyberware.risk.low_tolerance", "注意：耐性値が低下しています。抑制剤が必須。");

            add("cyberware.gui.scanner_saying.0", "曲線の積分");
            add("cyberware.gui.scanner_saying.1", "クジラの移動をグラフ化");
            add("cyberware.gui.scanner_saying.2", "熱核戦争のシミュレーション");
            add("cyberware.gui.scanner_saying.3", "Windowsをアップデート中");
            add("cyberware.gui.scanner_saying.4", "宝くじ番号の予測中");
            add("cyberware.gui.scanner_saying.5", "データツリーの根絶を行います");
            add("cyberware.gui.scanner_saying.6", "Askツールバーをインストールします");
            add("cyberware.gui.scanner_saying.7", "プロパガンダを抑制します。");
            add("cyberware.gui.scanner_saying.8", "他のMODを再調整");
            add("cyberware.gui.scanner_saying.9", "Redditバッファを更新しています");
            add("cyberware.gui.scanner_saying.10", "4d6のロール");
            add("cyberware.gui.scanner_saying.11", "見たことの繰り返し");
            add("cyberware.gui.scanner_saying.12", "バイナリからオクタルへの変換");
            add("cyberware.gui.scanner_saying.13", "ラマを検索中");
            add("cyberware.gui.scanner_saying.14", "フォンシェーダーを撤回");
            add("cyberware.gui.scanner_saying.15", "クイックプレイの再実装");
            add("cyberware.gui.scanner_saying.16", "マイクロトランザクションの削除");
            add("cyberware.gui.scanner_saying.17", "風水シェーダーを適用しています");
            add("cyberware.gui.scanner_saying.18", "頭が悪そうなコメントを生成中");
            add("cyberware.gui.scanner_saying.19", "パーティクルソースの収集");
            add("cyberware.gui.scanner_saying.20", "遺伝子ソースの接合");
            add("cyberware.gui.scanner_saying.21", "Herobrineに黒目を描く");
            add("cyberware.gui.scanner_saying.22", "カスタマイズされたBIOSの点滅");
            add("cyberware.gui.scanner_saying.23", "お勧めのルーツ");
            add("cyberware.gui.scanner_saying.24", "EULA規定の強制");
            add("cyberware.gui.scanner_saying.25", "ネジの方向を計算");
            add("cyberware.gui.scanner_saying.26", "あなたは好きですか？");
            add("cyberware.gui.scanner_saying.27", "ファームウェアの抽出");
            add("cyberware.gui.scanner_saying.28", "ライセンスキーのクラッキング");
            add("cyberware.gui.scanner_saying.29", "シミュレーションの構築");
            add("cyberware.gui.scanner_saying.30", "電子移動量の監視");
            add("cyberware.gui.scanner_saying.31", "ルートを再計算しています");
            add("cyberware.gui.scanner_saying.32", "特異点の分割");
            add("cyberware.gui.scanner_saying.33", "内部時計の圧縮");
            add("cyberware.gui.scanner_saying.34", "水分計のリセット");
            add("cyberware.gui.scanner_saying.35", "ブランド詳細の分析");
            add("cyberware.gui.scanner_saying.36", "Yulife計算を実行");
            add("cyberware.gui.scanner_saying.37", "ノイズレイヤーを抽出する");
            add("cyberware.gui.scanner_saying.38", "微分方程式を解いています");
            add("cyberware.gui.scanner_saying.39", "空間シミュレーションの開始");
            add("cyberware.gui.scanner_saying.40", "かゆ…うま");
            add("cyberware.gui.scanner_saying.41", "賽の河原の石積みをカウント中");
            add("cyberware.gui.scanner_saying.42", "私メリーさん、今あなたの");
            add("cyberware.gui.scanner_saying.43", "つるかめ算を解いています");
            add("cyberware.gui.scanner_saying.44", "チャイルドモードの実装");
            add("cyberware.gui.scanner_saying.45", "排出率の表示を偽装します");
            add("cyberware.gui.scanner_saying.46", "捕らぬ狸の皮を数えています");
            add("cyberware.gui.scanner_saying.47", "あなたは赤い部屋好きですか？");
            add("cyberware.gui.scanner_saying.48", "問題をイジングモデルに変換");
            add("cyberware.gui.scanner_saying.49", "コギトエルゴスム");
            add("cyberware.gui.scanner_saying.50", "ビットコインをマイニング中");
            add("cyberware.gui.scanner_saying.51", "AlphaGoと対局中");
            add("cyberware.gui.scanner_saying.52", "トポロジー最適化を行います");
            add("cyberware.gui.scanner_saying.53", "ジェネレーティブデザインを出力");
            add("cyberware.gui.scanner_saying.54", "捕鯨船が青いクジラを撃退");
            add("cyberware.gui.scanner_saying.55", "SNSから個人情報を収集");
            add("cyberware.gui.scanner_saying.56", "ビッグデータを解析中");
            add("cyberware.gui.scanner_saying.57", "深層学習の電力が不足しています");
            add("cyberware.gui.scanner_saying.58", "ほとんどの広告主に適していません");
            add("cyberware.gui.scanner_saying.59", "ゼロトラストに検知されました");
            add("cyberware.gui.scanner_saying.60", "人工知能によるフェイクか判別中");
            add("cyberware.gui.scanner_saying.61", "ゴリラと人間の違いを学習中");
            add("cyberware.gui.scanner_saying.62", "信用スコアが不足しています");
            add("cyberware.gui.scanner_saying.63", "シンギュラリティは近い");
            add("cyberware.gui.scanner_saying.64", "フェイクニュースを生成中");
            add("cyberware.gui.scanner_saying.65", "MCNに加入申請");
            add("cyberware.gui.scanner_saying.66", "スカイネットに捕捉されています");
            add("cyberware.gui.scanner_saying.67", "このパスワードは流出しています");
            add("cyberware.gui.scanner_saying.68", "Akinatorが私を知っているか検証");
            add("cyberware.gui.scanner_saying.69", "AIがチーターを検知しました");
            add("cyberware.gui.scanner_saying.70", "平行世界を使い並列処理しています");
            add("cyberware.gui.scanner_saying.71", "トランザクションのガス代が不足");
            add("cyberware.gui.scanner_saying.72", "51%攻撃を受けています");
            add("cyberware.gui.scanner_saying.73", "10万ビットコインをGOXしました");
        }
    }
}