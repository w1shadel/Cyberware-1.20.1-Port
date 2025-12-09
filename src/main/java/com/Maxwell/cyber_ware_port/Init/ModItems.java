package com.Maxwell.cyber_ware_port.Init;

import com.Maxwell.cyber_ware_port.Common.Block.CyberSkull.CyberSkullItemRenderer;
import com.Maxwell.cyber_ware_port.Common.Block.Robosurgeon.RobosurgeonBlockEntity;
import com.Maxwell.cyber_ware_port.Common.CyberwareTabState;
import com.Maxwell.cyber_ware_port.Common.Item.Base.BodyPartType;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.Common.Item.ComponentBox.ComponentBoxItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm.FineManipulatorsItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Arm.RapidFireFlywheelItem;
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
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Leg.*;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lower_Organs.*;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lung.CompressedOxygenImplantItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Lung.HyperoxygenationBoostItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Muscle.MyomerMuscleReplacementItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Muscle.WiredReflexesItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin.SolarskinItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin.SubdermalSpikesItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin.SyntheticSkinItem;
import com.Maxwell.cyber_ware_port.Common.Item.CyberWare.Skin.TargetedImmunosuppressantItem;
import com.Maxwell.cyber_ware_port.Common.Item.ExpCapsuleItem;
import com.Maxwell.cyber_ware_port.Common.Item.KatanaItem;
import com.Maxwell.cyber_ware_port.CyberWare;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class ModItems {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CyberWare.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CyberWare.MODID);
    public static final RegistryObject<Item> CYBER_WITHER_SKELETON_SKULL_ITEM = ITEMS.register("cyber_wither_skeleton_skull",
            () -> new StandingAndWallBlockItem(
                    ModBlocks.CYBER_WITHER_SKELETON_SKULL.get(),      
                    ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get(), 
                    new Item.Properties().rarity(Rarity.RARE),        
                    Direction.DOWN                                    
            ) {

                @Override
                public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                    consumer.accept(new IClientItemExtensions() {
                        private CyberSkullItemRenderer renderer;

                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            if (this.renderer == null) {

                                this.renderer = new CyberSkullItemRenderer(
                                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                                        Minecraft.getInstance().getEntityModels()
                                );
                            }
                            return this.renderer;
                        }
                    });
                }
            });
    public static final RegistryObject<Item> COMPONENT_BOX = ITEMS.register("component_box", ComponentBoxItem::new);
    public static final RegistryObject<Item> KATANA = ITEMS.register("katana", KatanaItem::new);
    public static final RegistryObject<Item> COMPONENT_ACTUATOR = ITEMS.register("component_actuator",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_REACTOR = ITEMS.register("component_reactor",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_TITANIUM = ITEMS.register("component_titanium",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_SSC = ITEMS.register("component_ssc",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_PLATING = ITEMS.register("component_plating",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_FIBEROPTICS = ITEMS.register("component_fiberoptics",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_FULLERENE = ITEMS.register("component_fullerene",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_SYNTHNERVES = ITEMS.register("component_synthnerves",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_STORAGE = ITEMS.register("component_storage",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT_MICROELECTRIC = ITEMS.register("component_microelectric",
            () -> new Item(new Item.Properties()));
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
    public static final RegistryObject<Item> DENSE_BATTERY = ITEMS.register("dense_battery", DenseBatteryItem::new);

    public static final RegistryObject<Item> MARROW_BATTERY = ITEMS.register("bone_upgrades_bonebattery", MarrowBatteryItem::new);public static final RegistryObject<Item> CYBER_ARM_LEFT = ITEMS.register("cyberlimbs_cyberarm_left", () -> new CyberArmItem(RobosurgeonBlockEntity.SLOT_ARMS,ModItems.HUMAN_LEFT_ARM));
    public static final RegistryObject<Item> CYBER_ARM_RIGHT = ITEMS.register("cyberlimbs_cyberarm_right", () -> new CyberArmItem(RobosurgeonBlockEntity.SLOT_ARMS, ModItems.HUMAN_RIGHT_ARM));

    public static final RegistryObject<Item> CYBER_LEG_LEFT = ITEMS.register("cyberlimbs_cyberleg_left", () -> new CyberLegItem(RobosurgeonBlockEntity.SLOT_LEGS,ModItems.HUMAN_LEFT_LEG));
    public static final RegistryObject<Item> CYBER_LEG_RIGHT = ITEMS.register("cyberlimbs_cyberleg_right", () -> new CyberLegItem(RobosurgeonBlockEntity.SLOT_LEGS,ModItems.HUMAN_RIGHT_LEG));

    public static final RegistryObject<Item> RETRACTABLE_CLAWS = ITEMS.register("hand_upgrades_claws", RetractableClawsItem::new);
    public static final RegistryObject<Item> REINFORCED_FIST = ITEMS.register("hand_upgrades_mining", ReinforcedFistItem::new);public static final RegistryObject<Item> LINEAR_ACTUATORS = ITEMS.register("leg_upgrades_jump_boost", LinearActuatorsItem::new);
    public static final RegistryObject<Item> FALL_BRACERS = ITEMS.register("leg_upgrades_fall_damage", FallBracersItem::new);

    public static final RegistryObject<Item> AQUATIC_PROPULSION = ITEMS.register("foot_upgrades_aqua", AquaticPropulsionSystemItem::new);
    public static final RegistryObject<Item> DEPLOYABLE_WHEELS = ITEMS.register("foot_upgrades_wheels", DeployableWheelsItem::new);public static final RegistryObject<Item> HUMAN_BRAIN   = registerHumanPart("body_part_brain", RobosurgeonBlockEntity.SLOT_BRAIN, 1, BodyPartType.BRAIN);
    public static final RegistryObject<Item> RAPID_FIRE_FLYWHEEL = ITEMS.register("arm_upgrades_bow",
            RapidFireFlywheelItem::new);

    public static final RegistryObject<Item> IMPLANTED_SPURS = ITEMS.register("foot_upgrades_spurs",
            ImplantedSpursItem::new);

    public static final RegistryObject<Item> FINE_MANIPULATORS = ITEMS.register("hand_upgrades_craft_hands",
            FineManipulatorsItem::new);
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

                int page = CyberwareTabState.currentPage;

                for (RegistryObject<Item> entry : ITEMS.getEntries()) {
                    Item item = entry.get();

                    if (item instanceof CyberwareItem cw) {
                        if (page == 0) {
                            entries.accept(new ItemStack(item));
                        }
                        else if (page == 1) {
                            ItemStack scavenged = new ItemStack(item);
                            cw.setPristine(scavenged, false);
                            entries.accept(scavenged);
                        }
                    }
                    else {

                        if (page == 0) {
                            entries.accept(new ItemStack(item));
                        }
                    }
                }
            }).build());
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