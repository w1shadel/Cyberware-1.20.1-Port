package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.CyberwareTabState;
import com.maxwell.cyber_ware_port.common.block.cyberskull.CyberSkullItemRenderer;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.RobosurgeonBlockEntity;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.common.item.ExpCapsuleItem;
import com.maxwell.cyber_ware_port.common.item.KatanaItem;
import com.maxwell.cyber_ware_port.common.item.NeuropozyneItem;
import com.maxwell.cyber_ware_port.common.item.base.BodyPartType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.common.item.componentbox.ComponentBoxItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.arm.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.bone.BonelacingItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.bone.CitrateEnhancementItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.bone.MarrowBatteryItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.cranium.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.eye.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.heart.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.leg.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.lung.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.muscle.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.skin.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.lower_organs.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class ModItems {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CyberWare.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, CyberWare.MODID);
    public static final DeferredHolder<Item, Item> CYBER_WITHER_SKELETON_SKULL_ITEM = ITEMS.register(
            "cyber_wither_skeleton_skull",
            () -> new StandingAndWallBlockItem(
                    ModBlocks.CYBER_WITHER_SKELETON_SKULL.get(),
                    ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get(),
                    new Item.Properties().rarity(Rarity.RARE),
                    Direction.DOWN) {
                @Override
                public void initializeClient(Consumer<IClientItemExtensions> consumer) {
                    consumer.accept(new IClientItemExtensions() {
                        private CyberSkullItemRenderer renderer;

                        @Override
                        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                            if (this.renderer == null) {
                                this.renderer = new CyberSkullItemRenderer(
                                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                                        Minecraft.getInstance().getEntityModels());
                            }
                            return this.renderer;
                        }
                    });
                }
            });
    public static final DeferredHolder<Item, Item> CYBER_ZOMBIE_SPAWN_EGG = ITEMS.register("cyber_zombie_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CYBER_ZOMBIE, 0x445555, 0x00AAAA, new Item.Properties()));
    public static final DeferredHolder<Item, Item> CYBER_SKELETON_SPAWN_EGG = ITEMS.register("cyber_skeleton_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CYBER_SKELETON, 0x888888, 0x4444AA, new Item.Properties()));
    public static final DeferredHolder<Item, Item> CYBER_WITHER_SKELETON_SPAWN_EGG = ITEMS.register("cyber_wither_skeleton_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CYBER_WITHER_SKELETON, 0x111111, 0xFF2222, new Item.Properties()));
    public static final DeferredHolder<Item, Item> CYBER_CREEPER_SPAWN_EGG = ITEMS.register("cyber_creeper_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CYBER_CREEPER, 0x0DA70B, 0xFF0000, new Item.Properties()));
    public static final DeferredHolder<Item, Item> CYBER_WITHER_SPAWN_EGG = ITEMS.register("cyber_wither_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CYBER_WITHER, 0x222222, 0x55FFFF, new Item.Properties()));
    public static final DeferredHolder<Item, ComponentBoxItem> COMPONENT_BOX = ITEMS.register("component_box", ComponentBoxItem::new);
    public static final DeferredHolder<Item, KatanaItem> KATANA = ITEMS.register("katana", KatanaItem::new);
    public static final DeferredHolder<Item, Item> COMPONENT_ACTUATOR = ITEMS.register("component_actuator", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_REACTOR = ITEMS.register("component_reactor", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_TITANIUM = ITEMS.register("component_titanium", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_SSC = ITEMS.register("component_ssc", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_PLATING = ITEMS.register("component_plating", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_FIBEROPTICS = ITEMS.register("component_fiberoptics", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_FULLERENE = ITEMS.register("component_fullerene", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_SYNTHNERVES = ITEMS.register("component_synthnerves", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_STORAGE = ITEMS.register("component_storage", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> COMPONENT_MICROELECTRIC = ITEMS.register("component_microelectric", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, BlueprintItem> BLUEPRINT = ITEMS.register("blueprint", () -> new BlueprintItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, HyperoxygenationBoostItem> HYPER_OXYGENATION = ITEMS.register("lungs_upgrades_hyperoxygenation", HyperoxygenationBoostItem::new);
    public static final DeferredHolder<Item, LiverFilterItem> LIVER_FILTER = ITEMS.register("lower_organs_upgrades_liver_filter", LiverFilterItem::new);
    public static final DeferredHolder<Item, MetabolicGeneratorItem> METABOLIC_GENERATOR = ITEMS.register("lower_organs_upgrades_metabolic", MetabolicGeneratorItem::new);
    public static final DeferredHolder<Item, InternalBatteryItem> INTERNAL_BATTERY = ITEMS.register("lower_organs_upgrades_battery", InternalBatteryItem::new);
    public static final DeferredHolder<Item, AdrenalinePumpItem> ADRENALINE_PUMP = ITEMS.register("lower_organs_upgrades_adrenaline", AdrenalinePumpItem::new);
    public static final DeferredHolder<Item, CreativeBatteryItem> CREATIVE_BATTERY = ITEMS.register("creative_battery", CreativeBatteryItem::new);
    public static final DeferredHolder<Item, ExpCapsuleItem> EXP_CAPSULE = ITEMS.register("exp_capsule", () -> new ExpCapsuleItem(new Item.Properties()));
    public static final DeferredHolder<Item, EnderJammerItem> ENDER_JAMMER = ITEMS.register("brain_upgrades_ender_jammer", EnderJammerItem::new);
    public static final DeferredHolder<Item, NeuralContextualizerItem> NEURAL_CONTEXTUALIZER = ITEMS.register("brain_upgrades_neural_contextualizer", NeuralContextualizerItem::new);
    public static final DeferredHolder<Item, ThreatMatrixItem> THREAT_MATRIX = ITEMS.register("brain_upgrades_matrix", ThreatMatrixItem::new);
    public static final DeferredHolder<Item, CranialBroadcasterItem> CRANIAL_BROADCASTER = ITEMS.register("brain_upgrades_radio", CranialBroadcasterItem::new);
    public static final DeferredHolder<Item, NeuropozyneItem> NEUROPOZYNE = ITEMS.register("neuropozyne", NeuropozyneItem::new);
    public static final DeferredHolder<Item, SolarskinItem> SOLARSKIN = ITEMS.register("skin_upgrades_solar_skin", SolarskinItem::new);
    public static final DeferredHolder<Item, SubdermalSpikesItem> SUBDERMAL_SPIKES = ITEMS.register("skin_upgrades_subdermal_spikes", SubdermalSpikesItem::new);
    public static final DeferredHolder<Item, SyntheticSkinItem> SYNTHETIC_SKIN = ITEMS.register("skin_upgrades_fake_skin", SyntheticSkinItem::new);
    public static final DeferredHolder<Item, TargetedImmunosuppressantItem> TARGETED_IMMUNOSUPPRESSANT = ITEMS.register("skin_upgrades_immuno", TargetedImmunosuppressantItem::new);
    public static final DeferredHolder<Item, WiredReflexesItem> WIRED_REFLEXES = ITEMS.register("muscle_upgrades_wired_reflexes", WiredReflexesItem::new);
    public static final DeferredHolder<Item, BonelacingItem> BONELACING = ITEMS.register("bone_upgrades_bonelacing", BonelacingItem::new);
    public static final DeferredHolder<Item, CorticalStackItem> CORTICAL_STACK = ITEMS.register("brain_upgrades_cortical_stack", CorticalStackItem::new);
    public static final DeferredHolder<Item, CitrateEnhancementItem> CITRATE_ENHANCEMENT = ITEMS.register("bone_upgrades_boneflex", CitrateEnhancementItem::new);
    public static final DeferredHolder<Item, DenseBatteryItem> DENSE_BATTERY = ITEMS.register("dense_battery", DenseBatteryItem::new);
    public static final DeferredHolder<Item, MarrowBatteryItem> MARROW_BATTERY = ITEMS.register("bone_upgrades_bonebattery", MarrowBatteryItem::new);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_BRAIN = registerHumanPart("body_part_brain", RobosurgeonBlockEntity.SLOT_BRAIN, 1, BodyPartType.BRAIN);
    public static final DeferredHolder<Item, RapidFireFlywheelItem> RAPID_FIRE_FLYWHEEL = ITEMS.register("arm_upgrades_bow", RapidFireFlywheelItem::new);
    public static final DeferredHolder<Item, ConsciousnessTransmitterItem> CONSCIOUSNESS_TRANSMITTER = ITEMS.register("brain_upgrades_consciousness_transmitter", ConsciousnessTransmitterItem::new);
    public static final DeferredHolder<Item, ImplantedSpursItem> IMPLANTED_SPURS = ITEMS.register("foot_upgrades_spurs", ImplantedSpursItem::new);
    public static final DeferredHolder<Item, FineManipulatorsItem> FINE_MANIPULATORS = ITEMS.register("hand_upgrades_craft_hands", FineManipulatorsItem::new);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_HEART = registerHumanPart("body_part_heart", RobosurgeonBlockEntity.SLOT_HEART, 1, BodyPartType.HEART);
    public static final DeferredHolder<Item, CardiomechanicPumpItem> CARDIOMECHANIC_PUMP = ITEMS.register("cyberheart", CardiomechanicPumpItem::new);
    public static final DeferredHolder<Item, InternalDefibrillatorItem> INTERNAL_DEFIBRILLATOR = ITEMS.register("heart_upgrades_defibrillator", InternalDefibrillatorItem::new);
    public static final DeferredHolder<Item, PlateletDispatcherItem> PLATELET_DISPATCHER = ITEMS.register("heart_upgrades_platelets", PlateletDispatcherItem::new);
    public static final DeferredHolder<Item, StemCellSynthesizerItem> STEM_CELL_SYNTHESIZER = ITEMS.register("heart_upgrades_medkit", StemCellSynthesizerItem::new);
    public static final DeferredHolder<Item, CardiovascularCouplerItem> CARDIOVASCULAR_COUPLER = ITEMS.register("heart_upgrades_coupler", CardiovascularCouplerItem::new);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_STOMACH = registerHumanPart("body_part_stomach", RobosurgeonBlockEntity.SLOT_STOMACH, 1, BodyPartType.STOMACH);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_SKIN = registerHumanPart("body_part_skin", RobosurgeonBlockEntity.SLOT_SKIN, 1, BodyPartType.SKIN);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_MUSCLE = registerHumanPart("body_part_muscles", RobosurgeonBlockEntity.SLOT_MUSCLE, 1, BodyPartType.MUSCLE);
    public static final DeferredHolder<Item, MyomerMuscleReplacementItem> MYOMER_MUSCLE = ITEMS.register("muscle_upgrades_muscle_replacements", MyomerMuscleReplacementItem::new);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_BONE = registerHumanPart("body_part_bones", RobosurgeonBlockEntity.SLOT_BONES, 1, BodyPartType.BONES);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_EYES = registerHumanPart("body_part_eyes", RobosurgeonBlockEntity.SLOT_EYES, 1, BodyPartType.EYES);
    public static final DeferredHolder<Item, CybereyesItem> CYBER_EYE = ITEMS.register("cybereyes", CybereyesItem::new);
    public static final DeferredHolder<Item, LowLightVisionItem> LOW_LIGHT_VISION = ITEMS.register("cybereye_upgrades_night_vision", LowLightVisionItem::new);
    public static final DeferredHolder<Item, LiquidRefractionCalibratorItem> LIQUID_REFRACTION = ITEMS.register("cybereye_upgrades_underwater_vision", LiquidRefractionCalibratorItem::new);
    public static final DeferredHolder<Item, HudjackItem> HUDJACK = ITEMS.register("cybereye_upgrades_hudjack", HudjackItem::new);
    public static final DeferredHolder<Item, TargetingOverlayItem> TARGETING_OVERLAY = ITEMS.register("cybereye_upgrades_targeting", TargetingOverlayItem::new);
    public static final DeferredHolder<Item, DistanceEnhancerItem> DISTANCE_ENHANCER = ITEMS.register("cybereye_upgrades_zoom", DistanceEnhancerItem::new);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LUNGS = registerHumanPart("body_part_lungs", RobosurgeonBlockEntity.SLOT_LUNGS, 1, BodyPartType.LUNGS);
    public static final DeferredHolder<Item, CompressedOxygenImplantItem> COMPRESSED_OXYGEN = ITEMS.register("lungs_upgrades_oxygen", CompressedOxygenImplantItem::new);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LEFT_ARM = registerHumanPart("body_part_arm_left", RobosurgeonBlockEntity.SLOT_ARMS, 1, BodyPartType.ARM_LEFT);
    public static final DeferredHolder<Item, CyberArmItem> CYBER_ARM_LEFT = ITEMS.register("cyberlimbs_cyberarm_left", () -> new CyberArmItem(RobosurgeonBlockEntity.SLOT_ARMS, ModItems.HUMAN_LEFT_ARM, BodyPartType.ARM_LEFT));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_RIGHT_ARM = registerHumanPart("body_part_arm_right", RobosurgeonBlockEntity.SLOT_ARMS, 1, BodyPartType.ARM_RIGHT);
    public static final DeferredHolder<Item, CyberArmItem> CYBER_ARM_RIGHT = ITEMS.register("cyberlimbs_cyberarm_right", () -> new CyberArmItem(RobosurgeonBlockEntity.SLOT_ARMS, ModItems.HUMAN_RIGHT_ARM, BodyPartType.ARM_RIGHT));
    public static final DeferredHolder<Item, RetractableClawsItem> RETRACTABLE_CLAWS = ITEMS.register("hand_upgrades_claws", RetractableClawsItem::new);
    public static final DeferredHolder<Item, ReinforcedFistItem> REINFORCED_FIST = ITEMS.register("hand_upgrades_mining", ReinforcedFistItem::new);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LEFT_HAND = registerHumanPart("body_part_hand_left", RobosurgeonBlockEntity.SLOT_HANDS, 1, BodyPartType.HAND_LEFT);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_RIGHT_HAND = registerHumanPart("body_part_hand_right", RobosurgeonBlockEntity.SLOT_HANDS, 1, BodyPartType.HAND_RIGHT);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LEFT_LEG = registerHumanPart("body_part_leg_left", RobosurgeonBlockEntity.SLOT_LEGS, 1, BodyPartType.LEG_LEFT);
    public static final DeferredHolder<Item, CyberLegItem> CYBER_LEG_LEFT = ITEMS.register("cyberlimbs_cyberleg_left", () -> new CyberLegItem(RobosurgeonBlockEntity.SLOT_LEGS, ModItems.HUMAN_LEFT_LEG, BodyPartType.LEG_LEFT));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_RIGHT_LEG = registerHumanPart("body_part_leg_right", RobosurgeonBlockEntity.SLOT_LEGS, 1, BodyPartType.LEG_RIGHT);
    public static final DeferredHolder<Item, CyberLegItem> CYBER_LEG_RIGHT = ITEMS.register("cyberlimbs_cyberleg_right", () -> new CyberLegItem(RobosurgeonBlockEntity.SLOT_LEGS, ModItems.HUMAN_RIGHT_LEG, BodyPartType.LEG_RIGHT));
    public static final DeferredHolder<Item, AquaticPropulsionSystemItem> AQUATIC_PROPULSION = ITEMS.register("foot_upgrades_aqua", AquaticPropulsionSystemItem::new);
    public static final DeferredHolder<Item, LinearActuatorsItem> LINEAR_ACTUATORS = ITEMS.register("leg_upgrades_jump_boost", LinearActuatorsItem::new);
    public static final DeferredHolder<Item, FallBracersItem> FALL_BRACERS = ITEMS.register("leg_upgrades_fall_damage", FallBracersItem::new);
    public static final DeferredHolder<Item, DeployableWheelsItem> DEPLOYABLE_WHEELS = ITEMS.register("foot_upgrades_wheels", DeployableWheelsItem::new);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LEFT_FOOT = registerHumanPart("body_part_foot_left", RobosurgeonBlockEntity.SLOT_BOOTS, 1, BodyPartType.FOOT_LEFT);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_RIGHT_FOOT = registerHumanPart("body_part_foot_right", RobosurgeonBlockEntity.SLOT_BOOTS, 1, BodyPartType.FOOT_RIGHT);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CW_TABS = TABS.register("cyber_wear_port",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cyber_ware_port.items"))
                    .icon(() -> new ItemStack(ModBlocks.SURGERY_CHAMBER.get()))
                    .displayItems((enabledFeatures, entries) -> {
                        int page = CyberwareTabState.currentPage;
                        for (DeferredHolder<Item, ? extends Item> holder : ITEMS.getEntries()) {
                            Item item = holder.get();
                            if (item instanceof CyberwareItem cw) {
                                if (page == 0) {
                                    entries.accept(new ItemStack(item));
                                } else if (page == 1) {
                                    ItemStack scavenged = new ItemStack(item);
                                    cw.setPristine(scavenged, false);
                                    entries.accept(scavenged);
                                }
                            } else {
                                if (page == 0) {
                                    entries.accept(new ItemStack(item));
                                }
                            }
                        }
                    }).build());

    private static DeferredHolder<Item, CyberwareItem> registerHumanPart(String name, int slotId, int maxInstall, BodyPartType bodyPartType) {
        return ITEMS.register(name, () -> new CyberwareItem.Builder(0, slotId)
                .maxInstall(maxInstall)
                .bodyPart(bodyPartType)
                .quality(0)
                .build());
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        TABS.register(eventBus);
    }
}
