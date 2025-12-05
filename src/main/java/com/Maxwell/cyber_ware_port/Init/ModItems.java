package com.Maxwell.cyber_ware_port.Init;

import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm.ReinforcedFistItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm.RetractableClawsItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Bone.BonelacingItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Bone.CitrateEnhancementItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Bone.MarrowBatteryItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Cranium.*;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.CyberArmItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.CyberLegItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Eye.*;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Heart.*;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg.AquaticPropulsionSystemItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg.DeployableWheelsItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg.FallBracersItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg.LinearActuatorsItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs.*;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lung.*;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Muscle.MyomerMuscleReplacementItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Muscle.WiredReflexesItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin.SolarskinItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin.SubdermalSpikesItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin.SyntheticSkinItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin.TargetedImmunosuppressantItem;
import com.Maxwell.cyber_ware_port.Common.Item.ExpCapsuleItem;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CyberWare.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CyberWare.MODID);

    public static final RegistryObject<Item> BLUEPRINT = ITEMS.register("blueprint",
            () -> new BlueprintItem(new Item.Properties().stacksTo(1)));public static final RegistryObject<Item> CYBER_EYE = ITEMS.register("cybereyes", CybereyesItem::new);

    public static final RegistryObject<Item> LOW_LIGHT_VISION = ITEMS.register("cybereye_upgrades_night_vision", LowLightVisionItem::new);

    public static final RegistryObject<Item> LIQUID_REFRACTION = ITEMS.register("cybereye_upgrades_underwater_vision", LiquidRefractionCalibratorItem::new);

    public static final RegistryObject<Item> HUDJACK = ITEMS.register("cybereye_upgrades_hudjack", HudjackItem::new);

    public static final RegistryObject<Item> TARGETING_OVERLAY = ITEMS.register("cybereye_upgrades_targeting", TargetingOverlayItem::new);

    public static final RegistryObject<Item> DISTANCE_ENHANCER = ITEMS.register("cybereye_upgrades_zoom", DistanceEnhancerItem::new);public static final RegistryObject<Item> COMPRESSED_OXYGEN = ITEMS.register("lungs_upgrades_oxygen", CompressedOxygenImplantItem::new);

    public static final RegistryObject<Item> HYPER_OXYGENATION = ITEMS.register("lungs_upgrades_hyperoxygenation", HyperoxygenationBoostItem::new);public static final RegistryObject<Item> LIVER_FILTER = ITEMS.register("lower_organs_upgrades_liver_filter", LiverFilterItem::new);

    public static final RegistryObject<Item> METABOLIC_GENERATOR = ITEMS.register("lower_organs_upgrades_metabolic", MetabolicGeneratorItem::new);

    public static final RegistryObject<Item> INTERNAL_BATTERY = ITEMS.register("lower_organs_upgrades_battery", InternalBatteryItem::new);

    public static final RegistryObject<Item> ADRENALINE_PUMP = ITEMS.register("lower_organs_upgrades_adrenaline", AdrenalinePumpItem::new);

    public static final RegistryObject<Item> CREATIVE_BATTERY = ITEMS.register("creative_battery", CreativeBatteryItem::new);public static final RegistryObject<Item> EXP_CAPSULE = ITEMS.register("exp_capsule", () -> new ExpCapsuleItem(new Item.Properties()));public static final RegistryObject<Item> CORTICAL_STACK = ITEMS.register("brain_upgrades_cortical_stack", CorticalStackItem::new);

    public static final RegistryObject<Item> ENDER_JAMMER = ITEMS.register("brain_upgrades_ender_jammer", EnderJammerItem::new);

    public static final RegistryObject<Item> CONSCIOUSNESS_TRANSMITTER = ITEMS.register("brain_upgrades_consciousness_transmitter", ConsciousnessTransmitterItem::new);

    public static final RegistryObject<Item> NEURAL_CONTEXTUALIZER = ITEMS.register("brain_upgrades_neural_contextualizer", NeuralContextualizerItem::new);

    public static final RegistryObject<Item> THREAT_MATRIX = ITEMS.register("brain_upgrades_matrix", ThreatMatrixItem::new);

    public static final RegistryObject<Item> CRANIAL_BROADCASTER = ITEMS.register("brain_upgrades_radio", CranialBroadcasterItem::new);public static final RegistryObject<Item> CARDIOMECHANIC_PUMP = ITEMS.register("cyberheart", CardiomechanicPumpItem::new);

    public static final RegistryObject<Item> INTERNAL_DEFIBRILLATOR = ITEMS.register("heart_upgrades_defibrillator", InternalDefibrillatorItem::new);

    public static final RegistryObject<Item> PLATELET_DISPATCHER = ITEMS.register("heart_upgrades_platelets", PlateletDispatcherItem::new);

    public static final RegistryObject<Item> STEM_CELL_SYNTHESIZER = ITEMS.register("heart_upgrades_medkit", StemCellSynthesizerItem::new);

    public static final RegistryObject<Item> CARDIOVASCULAR_COUPLER = ITEMS.register("heart_upgrades_coupler", CardiovascularCouplerItem::new);public static final RegistryObject<Item> SOLARSKIN = ITEMS.register("skin_upgrades_solar_skin", SolarskinItem::new);

    public static final RegistryObject<Item> SUBDERMAL_SPIKES = ITEMS.register("skin_upgrades_subdermal_spikes", SubdermalSpikesItem::new);

    public static final RegistryObject<Item> SYNTHETIC_SKIN = ITEMS.register("skin_upgrades_fake_skin", SyntheticSkinItem::new);

    public static final RegistryObject<Item> TARGETED_IMMUNOSUPPRESSANT = ITEMS.register("skin_upgrades_immuno", TargetedImmunosuppressantItem::new);public static final RegistryObject<Item> WIRED_REFLEXES = ITEMS.register("muscle_upgrades_wired_reflexes", WiredReflexesItem::new);

    public static final RegistryObject<Item> MYOMER_MUSCLE = ITEMS.register("muscle_upgrades_muscle_replacements", MyomerMuscleReplacementItem::new);public static final RegistryObject<Item> BONELACING = ITEMS.register("bone_upgrades_bonelacing", BonelacingItem::new);

    public static final RegistryObject<Item> CITRATE_ENHANCEMENT = ITEMS.register("bone_upgrades_boneflex", CitrateEnhancementItem::new);

    public static final RegistryObject<Item> MARROW_BATTERY = ITEMS.register("bone_upgrades_bonebattery", MarrowBatteryItem::new);public static final RegistryObject<Item> CYBER_ARM_LEFT = ITEMS.register("cyberlimbs_cyberarm_left", () -> new CyberArmItem(RobosurgeonBlockEntity.SLOT_ARMS));
    public static final RegistryObject<Item> CYBER_ARM_RIGHT = ITEMS.register("cyberlimbs_cyberarm_right", () -> new CyberArmItem(RobosurgeonBlockEntity.SLOT_ARMS));

    public static final RegistryObject<Item> CYBER_LEG_LEFT = ITEMS.register("cyberlimbs_cyberleg_left", () -> new CyberLegItem(RobosurgeonBlockEntity.SLOT_LEGS));
    public static final RegistryObject<Item> CYBER_LEG_RIGHT = ITEMS.register("cyberlimbs_cyberleg_right", () -> new CyberLegItem(RobosurgeonBlockEntity.SLOT_LEGS));

    public static final RegistryObject<Item> RETRACTABLE_CLAWS = ITEMS.register("hand_upgrades_claws", RetractableClawsItem::new);
    public static final RegistryObject<Item> REINFORCED_FIST = ITEMS.register("hand_upgrades_mining", ReinforcedFistItem::new);public static final RegistryObject<Item> LINEAR_ACTUATORS = ITEMS.register("leg_upgrades_jump_boost", LinearActuatorsItem::new);
    public static final RegistryObject<Item> FALL_BRACERS = ITEMS.register("leg_upgrades_fall_damage", FallBracersItem::new);

    public static final RegistryObject<Item> AQUATIC_PROPULSION = ITEMS.register("foot_upgrades_aqua", AquaticPropulsionSystemItem::new);
    public static final RegistryObject<Item> DEPLOYABLE_WHEELS = ITEMS.register("foot_upgrades_wheels", DeployableWheelsItem::new);public static final RegistryObject<Item> HUMAN_BRAIN   = registerHumanPart("body_part_brain", RobosurgeonBlockEntity.SLOT_BRAIN, 1, BodyPartType.BRAIN);

    public static final RegistryObject<Item> HUMAN_HEART   = registerHumanPart("body_part_heart", RobosurgeonBlockEntity.SLOT_HEART, 1, BodyPartType.HEART);

    public static final RegistryObject<Item> HUMAN_STOMACH = registerHumanPart("body_part_stomach", RobosurgeonBlockEntity.SLOT_STOMACH, 1, BodyPartType.STOMACH);

    public static final RegistryObject<Item> HUMAN_SKIN    = registerHumanPart("body_part_skin", RobosurgeonBlockEntity.SLOT_SKIN, 1, BodyPartType.SKIN);

    public static final RegistryObject<Item> HUMAN_MUSCLE  = registerHumanPart("body_part_muscles", RobosurgeonBlockEntity.SLOT_MUSCLE, 1, BodyPartType.MUSCLE);

    public static final RegistryObject<Item> HUMAN_BONE    = registerHumanPart("body_part_bones", RobosurgeonBlockEntity.SLOT_BONES, 1, BodyPartType.BONES);

    public static final RegistryObject<Item> HUMAN_EYES  = registerHumanPart("body_part_eyes", RobosurgeonBlockEntity.SLOT_EYES, 1, BodyPartType.EYES);

    public static final RegistryObject<Item> HUMAN_LUNGS = registerHumanPart("body_part_lungs", RobosurgeonBlockEntity.SLOT_LUNGS, 1, BodyPartType.LUNGS);

    public static final RegistryObject<Item> HUMAN_LEFT_ARM   = registerHumanPart("body_part_arm_left", RobosurgeonBlockEntity.SLOT_ARMS, 1, BodyPartType.ARM);

    public static final RegistryObject<Item> HUMAN_RIGHT_ARM   = registerHumanPart("body_part_arm_right", RobosurgeonBlockEntity.SLOT_ARMS, 1, BodyPartType.ARM);

    public static final RegistryObject<Item> HUMAN_LEFT_HAND  = registerHumanPart("body_part_hand_left", RobosurgeonBlockEntity.SLOT_HANDS, 1, BodyPartType.HAND);

    public static final RegistryObject<Item> HUMAN_RIGHT_HAND  = registerHumanPart("body_part_hand_right", RobosurgeonBlockEntity.SLOT_HANDS, 1, BodyPartType.HAND);

    public static final RegistryObject<Item> HUMAN_LEFT_LEG   = registerHumanPart("body_part_leg_left", RobosurgeonBlockEntity.SLOT_LEGS, 1, BodyPartType.LEG);

    public static final RegistryObject<Item> HUMAN_RIGHT_LEG   = registerHumanPart("body_part_leg_right", RobosurgeonBlockEntity.SLOT_LEGS, 1, BodyPartType.LEG);

    public static final RegistryObject<Item> HUMAN_LEFT_FOOT  = registerHumanPart("body_part_foot_left", RobosurgeonBlockEntity.SLOT_BOOTS, 1, BodyPartType.FOOT);

    public static final RegistryObject<Item> HUMAN_RIGHT_FOOT  = registerHumanPart("body_part_foot_right", RobosurgeonBlockEntity.SLOT_BOOTS, 1, BodyPartType.FOOT);
    public static final RegistryObject<CreativeModeTab> CW_TABS = TABS.register("cyber_wear_port", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cyber_ware_port.items"))
            .icon(() -> new ItemStack(ModBlocks.SURGERY_CHAMBER.get()))
            .displayItems((enabledFeatures, entries) -> {

                entries.accept(ModBlocks.ROBO_SURGEON.get());
                entries.accept(ModBlocks.SURGERY_CHAMBER.get());
                entries.accept(ModBlocks.CYBERWARE_WORKBENCH.get());
                entries.accept(ModBlocks.SCANNER.get());
                entries.accept(BLUEPRINT.get()); entries.accept(CYBER_EYE.get());
                entries.accept(LOW_LIGHT_VISION.get());
                entries.accept(LIQUID_REFRACTION.get());
                entries.accept(HUDJACK.get());
                entries.accept(TARGETING_OVERLAY.get());
                entries.accept(DISTANCE_ENHANCER.get());

                entries.accept(CORTICAL_STACK.get());
                entries.accept(ENDER_JAMMER.get());
                entries.accept(CONSCIOUSNESS_TRANSMITTER.get());
                entries.accept(NEURAL_CONTEXTUALIZER.get());
                entries.accept(THREAT_MATRIX.get());
                entries.accept(CRANIAL_BROADCASTER.get());

                entries.accept(CARDIOMECHANIC_PUMP.get());
                entries.accept(INTERNAL_DEFIBRILLATOR.get());
                entries.accept(PLATELET_DISPATCHER.get());
                entries.accept(STEM_CELL_SYNTHESIZER.get());
                entries.accept(CARDIOVASCULAR_COUPLER.get());

                entries.accept(COMPRESSED_OXYGEN.get());
                entries.accept(HYPER_OXYGENATION.get());

                entries.accept(LIVER_FILTER.get());
                entries.accept(METABOLIC_GENERATOR.get());
                entries.accept(INTERNAL_BATTERY.get());
                entries.accept(ADRENALINE_PUMP.get());
                entries.accept(CREATIVE_BATTERY.get());

                entries.accept(SOLARSKIN.get());
                entries.accept(SUBDERMAL_SPIKES.get());
                entries.accept(SYNTHETIC_SKIN.get());
                entries.accept(TARGETED_IMMUNOSUPPRESSANT.get());

                entries.accept(WIRED_REFLEXES.get());
                entries.accept(MYOMER_MUSCLE.get());

                entries.accept(BONELACING.get());
                entries.accept(CITRATE_ENHANCEMENT.get());
                entries.accept(MARROW_BATTERY.get());

                entries.accept(CYBER_ARM_LEFT.get());
                entries.accept(CYBER_ARM_RIGHT.get());
                entries.accept(CYBER_LEG_LEFT.get());
                entries.accept(CYBER_LEG_RIGHT.get());

                entries.accept(RETRACTABLE_CLAWS.get());
                entries.accept(REINFORCED_FIST.get());
                entries.accept(LINEAR_ACTUATORS.get());
                entries.accept(FALL_BRACERS.get());
                entries.accept(AQUATIC_PROPULSION.get());
                entries.accept(DEPLOYABLE_WHEELS.get());

                entries.accept(EXP_CAPSULE.get());

                entries.accept(HUMAN_BRAIN.get());
                entries.accept(HUMAN_EYES.get());
                entries.accept(HUMAN_HEART.get());
                entries.accept(HUMAN_LUNGS.get());
                entries.accept(HUMAN_STOMACH.get());
                entries.accept(HUMAN_SKIN.get());
                entries.accept(HUMAN_MUSCLE.get());
                entries.accept(HUMAN_BONE.get());
                entries.accept(HUMAN_LEFT_ARM.get());
                entries.accept(HUMAN_RIGHT_ARM.get());
                entries.accept(HUMAN_LEFT_HAND.get());
                entries.accept(HUMAN_RIGHT_HAND.get());
                entries.accept(HUMAN_LEFT_LEG.get());
                entries.accept(HUMAN_RIGHT_LEG.get());
                entries.accept(HUMAN_LEFT_FOOT.get());
                entries.accept(HUMAN_RIGHT_FOOT.get());
            }).build());

    private static RegistryObject<Item> registerPart(String name, int slotId, int essenceCost, int maxInstall) {
        return ITEMS.register(name, () -> new CyberwareItem.Builder(essenceCost, slotId)
                .maxInstall(maxInstall)
                .build());
    }

    private static RegistryObject<Item> registerHumanPart(String name, int slotId, int maxInstall, BodyPartType bodyPartType) {
        return ITEMS.register(name, () -> new CyberwareItem.Builder(0, slotId)
                .maxInstall(maxInstall)
                .bodyPart(bodyPartType) 
                .build());
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        TABS.register(eventBus);
    }
}