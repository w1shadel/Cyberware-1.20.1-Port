package com.maxwell.cyber_ware_port.init;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.CyberwareTabState;
import com.maxwell.cyber_ware_port.common.block.robosurgeon.BodyRegionEnum;
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
import com.maxwell.cyber_ware_port.common.item.cyberware.lower_organs.*;
import com.maxwell.cyber_ware_port.common.item.cyberware.lung.CompressedOxygenImplantItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.lung.HyperoxygenationBoostItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.muscle.MyomerMuscleReplacementItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.muscle.WiredReflexesItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.skin.SolarskinItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.skin.SubdermalSpikesItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.skin.SyntheticSkinItem;
import com.maxwell.cyber_ware_port.common.item.cyberware.skin.TargetedImmunosuppressantItem;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModItems {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CyberWare.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("cyber_ware_port");
    private static Item.Properties modProps(Identifier location) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, location));
    }
    public static final DeferredItem<BlockItem> ROBO_SURGEON = ITEMS.registerSimpleBlockItem("robo_surgeon", ModBlocks.ROBO_SURGEON);

    public static final DeferredItem<BlockItem> SURGERY_CHAMBER = ITEMS.registerSimpleBlockItem("surgery_chamber", ModBlocks.SURGERY_CHAMBER);

    public static final DeferredItem<BlockItem> RADIO_TOWER_CORE = ITEMS.registerSimpleBlockItem("radio_tower_core", ModBlocks.RADIO_TOWER_CORE);

    public static final DeferredItem<BlockItem> CYBERWARE_WORKBENCH = ITEMS.registerSimpleBlockItem("cyberware_workbench", ModBlocks.CYBERWARE_WORKBENCH);

    public static final DeferredItem<BlockItem> COMPONENT_BOX_BLOCK = ITEMS.registerSimpleBlockItem("component_box_block", ModBlocks.COMPONENT_BOX);

    public static final DeferredItem<BlockItem> RADIO_KIT_BLOCK = ITEMS.registerSimpleBlockItem("radio_kit", ModBlocks.RADIO_KIT_BLOCK);

    public static final DeferredItem<BlockItem> RADIO_TOWER_COMPONENT = ITEMS.registerSimpleBlockItem("radio_tower_component", ModBlocks.RADIO_TOWER_COMPONENT);

    public static final DeferredItem<BlockItem> CHARGER = ITEMS.registerSimpleBlockItem("charger", ModBlocks.CHARGER);

    public static final DeferredItem<BlockItem> SCANNER = ITEMS.registerSimpleBlockItem("scanner", ModBlocks.SCANNER);


    public static final DeferredHolder<Item, Item> CYBER_ZOMBIE_SPAWN_EGG = ITEMS.register("cyber_zombie_spawn_egg",
            (location) -> new SpawnEggItem(
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, location))
                            .spawnEgg(ModEntities.CYBER_ZOMBIE.get())
            ));

    public static final DeferredHolder<Item, Item> CYBER_SKELETON_SPAWN_EGG = ITEMS.register("cyber_skeleton_spawn_egg",
            (location) -> new SpawnEggItem(
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, location))
                            .spawnEgg(ModEntities.CYBER_SKELETON.get())
            ));

    public static final DeferredHolder<Item, Item> CYBER_WITHER_SKELETON_SPAWN_EGG = ITEMS.register("cyber_wither_skeleton_spawn_egg",
            (location) -> new SpawnEggItem(
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, location))
                            .spawnEgg(ModEntities.CYBER_WITHER_SKELETON.get())
            ));
    public static final DeferredHolder<Item, Item> CYBER_CREEPER_SPAWN_EGG = ITEMS.register("cyber_creeper_spawn_egg",
            (location) -> new SpawnEggItem(
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, location))
                            .spawnEgg(ModEntities.CYBER_CREEPER.get())
            ));
    public static final DeferredHolder<Item, Item> CYBER_WITHER_SPAWN_EGG = ITEMS.register("cyber_wither_spawn_egg",
            (location) -> new SpawnEggItem(
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, location))
                            .spawnEgg(ModEntities.CYBER_WITHER.get())
            ));
    
    public static final DeferredHolder<Item, ComponentBoxItem> COMPONENT_BOX = ITEMS.register("component_box",
            (location) -> new ComponentBoxItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, location))
                    .stacksTo(1)));
    public static final DeferredHolder<Item, KatanaItem> KATANA = ITEMS.register("katana",
            (location) -> new KatanaItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, location))
                    .sword(ToolMaterial.IRON, 4.0F, -2.0F)
                    .stacksTo(1)
            ));

    public static final DeferredHolder<Item, Item> COMPONENT_ACTUATOR = ITEMS.register("component_actuator", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_REACTOR = ITEMS.register("component_reactor", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_TITANIUM = ITEMS.register("component_titanium", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_SSC = ITEMS.register("component_ssc", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_PLATING = ITEMS.register("component_plating", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_FIBEROPTICS = ITEMS.register("component_fiberoptics", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_FULLERENE = ITEMS.register("component_fullerene", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_SYNTHNERVES = ITEMS.register("component_synthnerves", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_STORAGE = ITEMS.register("component_storage", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, Item> COMPONENT_MICROELECTRIC = ITEMS.register("component_microelectric", (location) -> new Item(modProps(location)));
    public static final DeferredHolder<Item, BlueprintItem> BLUEPRINT = ITEMS.register("blueprint", (location) -> new BlueprintItem(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, location))));
    public static final DeferredHolder<Item, HyperoxygenationBoostItem> HYPER_OXYGENATION = ITEMS.register("lungs_upgrades_hyperoxygenation",(location) -> new HyperoxygenationBoostItem(modProps(location)));
    public static final DeferredHolder<Item, LiverFilterItem> LIVER_FILTER = ITEMS.register("lower_organs_upgrades_liver_filter", (location) -> new LiverFilterItem(modProps(location)));
    public static final DeferredHolder<Item, MetabolicGeneratorItem> METABOLIC_GENERATOR = ITEMS.register("lower_organs_upgrades_metabolic", (location) -> new MetabolicGeneratorItem(modProps(location)));
    public static final DeferredHolder<Item, InternalBatteryItem> INTERNAL_BATTERY = ITEMS.register("lower_organs_upgrades_battery", (location) -> new InternalBatteryItem(modProps(location)));
    public static final DeferredHolder<Item, AdrenalinePumpItem> ADRENALINE_PUMP = ITEMS.register("lower_organs_upgrades_adrenaline", (location) -> new AdrenalinePumpItem(modProps(location)));
    public static final DeferredHolder<Item, CreativeBatteryItem> CREATIVE_BATTERY = ITEMS.register("creative_battery", (location) -> new CreativeBatteryItem(modProps(location)));
    public static final DeferredHolder<Item, ExpCapsuleItem> EXP_CAPSULE = ITEMS.register("exp_capsule", (location) -> new ExpCapsuleItem(modProps(location)));
    public static final DeferredHolder<Item, EnderJammerItem> ENDER_JAMMER = ITEMS.register("brain_upgrades_ender_jammer",(location) -> new EnderJammerItem(modProps(location)));
    public static final DeferredHolder<Item, NeuralContextualizerItem> NEURAL_CONTEXTUALIZER = ITEMS.register("brain_upgrades_neural_contextualizer", (location) -> new NeuralContextualizerItem(modProps(location)));
    public static final DeferredHolder<Item, ThreatMatrixItem> THREAT_MATRIX = ITEMS.register("brain_upgrades_matrix", (location) -> new ThreatMatrixItem(modProps(location)));
    public static final DeferredHolder<Item, CranialBroadcasterItem> CRANIAL_BROADCASTER = ITEMS.register("brain_upgrades_radio", (location) -> new CranialBroadcasterItem(modProps(location)));
    public static final DeferredHolder<Item, NeuropozyneItem> NEUROPOZYNE = ITEMS.register("neuropozyne", (location) -> new NeuropozyneItem(modProps(location)));
    public static final DeferredHolder<Item, SolarskinItem> SOLARSKIN = ITEMS.register("skin_upgrades_solar_skin", (location) -> new SolarskinItem(modProps(location)));
    public static final DeferredHolder<Item, SubdermalSpikesItem> SUBDERMAL_SPIKES = ITEMS.register("skin_upgrades_subdermal_spikes", (location) -> new SubdermalSpikesItem(modProps(location)));
    public static final DeferredHolder<Item, SyntheticSkinItem> SYNTHETIC_SKIN = ITEMS.register("skin_upgrades_fake_skin", (location) -> new SyntheticSkinItem(modProps(location)));
    public static final DeferredHolder<Item, TargetedImmunosuppressantItem> TARGETED_IMMUNOSUPPRESSANT = ITEMS.register("skin_upgrades_immuno", (location) -> new TargetedImmunosuppressantItem(modProps(location)));
    public static final DeferredHolder<Item, WiredReflexesItem> WIRED_REFLEXES = ITEMS.register("muscle_upgrades_wired_reflexes", (location) -> new WiredReflexesItem(modProps(location)));
    public static final DeferredHolder<Item, BonelacingItem> BONELACING = ITEMS.register("bone_upgrades_bonelacing", (location) -> new BonelacingItem(modProps(location)));
    public static final DeferredHolder<Item, CitrateEnhancementItem> CITRATE_ENHANCEMENT = ITEMS.register("bone_upgrades_boneflex", (location) -> new CitrateEnhancementItem(modProps(location)));
    public static final DeferredHolder<Item, DenseBatteryItem> DENSE_BATTERY = ITEMS.register("dense_battery", (location) -> new DenseBatteryItem(modProps(location)));
    public static final DeferredHolder<Item, MarrowBatteryItem> MARROW_BATTERY = ITEMS.register("bone_upgrades_bonebattery", (location) -> new MarrowBatteryItem(modProps(location)));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_BRAIN = registerHumanPart("body_part_brain", BodyRegionEnum.BRAIN.getStartSlot(), 1, BodyPartType.BRAIN);
    public static final DeferredHolder<Item, RapidFireFlywheelItem> RAPID_FIRE_FLYWHEEL = ITEMS.register("arm_upgrades_bow", (location) -> new RapidFireFlywheelItem(modProps(location)));
    public static final DeferredHolder<Item, ImplantedSpursItem> IMPLANTED_SPURS = ITEMS.register("foot_upgrades_spurs", (location) -> new ImplantedSpursItem(modProps(location)));
    public static final DeferredHolder<Item, FineManipulatorsItem> FINE_MANIPULATORS = ITEMS.register("hand_upgrades_craft_hands", (location) -> new FineManipulatorsItem(modProps(location)));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_HEART = registerHumanPart("body_part_heart", BodyRegionEnum.HEART.getStartSlot(), 1, BodyPartType.HEART);    public static final DeferredHolder<Item, CorticalStackItem> CORTICAL_STACK = ITEMS.register("brain_upgrades_cortical_stack", (location) -> new CorticalStackItem(modProps(location)));
    public static final DeferredHolder<Item, CardiomechanicPumpItem> CARDIOMECHANIC_PUMP = ITEMS.register("cyberheart", (location) -> new CardiomechanicPumpItem(modProps(location)));
    public static final DeferredHolder<Item, InternalDefibrillatorItem> INTERNAL_DEFIBRILLATOR = ITEMS.register("heart_upgrades_defibrillator", (location) -> new InternalDefibrillatorItem(modProps(location)));
    public static final DeferredHolder<Item, PlateletDispatcherItem> PLATELET_DISPATCHER = ITEMS.register("heart_upgrades_platelets", (location) -> new PlateletDispatcherItem(modProps(location)));
    public static final DeferredHolder<Item, StemCellSynthesizerItem> STEM_CELL_SYNTHESIZER = ITEMS.register("heart_upgrades_medkit", (location) -> new StemCellSynthesizerItem(modProps(location)));
    public static final DeferredHolder<Item, CardiovascularCouplerItem> CARDIOVASCULAR_COUPLER = ITEMS.register("heart_upgrades_coupler", (location) -> new CardiovascularCouplerItem(modProps(location)));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_STOMACH = registerHumanPart("body_part_stomach", BodyRegionEnum.STOMACH.getStartSlot(), 1, BodyPartType.STOMACH);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_SKIN = registerHumanPart("body_part_skin", BodyRegionEnum.SKIN.getStartSlot(), 1, BodyPartType.SKIN);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_MUSCLE = registerHumanPart("body_part_muscles", BodyRegionEnum.MUSCLE.getStartSlot(), 1, BodyPartType.MUSCLE);
    public static final DeferredHolder<Item, MyomerMuscleReplacementItem> MYOMER_MUSCLE = ITEMS.register("muscle_upgrades_muscle_replacements", (location) -> new MyomerMuscleReplacementItem(modProps(location)));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_BONE = registerHumanPart("body_part_bones", BodyRegionEnum.BONES.getStartSlot(), 1, BodyPartType.BONES);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_EYES = registerHumanPart("body_part_eyes", BodyRegionEnum.EYES.getStartSlot(), 1, BodyPartType.EYES);
    public static final DeferredHolder<Item, CybereyesItem> CYBER_EYE = ITEMS.register("cybereyes", (location) -> new CybereyesItem(modProps(location)));
    public static final DeferredHolder<Item, LowLightVisionItem> LOW_LIGHT_VISION = ITEMS.register("cybereye_upgrades_night_vision", (location) -> new LowLightVisionItem(modProps(location)));
    public static final DeferredHolder<Item, ConsciousnessTransmitterItem> CONSCIOUSNESS_TRANSMITTER = ITEMS.register("brain_upgrades_consciousness_transmitter", (location) -> new ConsciousnessTransmitterItem(modProps(location)));
    public static final DeferredHolder<Item, LiquidRefractionCalibratorItem> LIQUID_REFRACTION = ITEMS.register("cybereye_upgrades_underwater_vision", (location) -> new LiquidRefractionCalibratorItem(modProps(location)));
    public static final DeferredHolder<Item, HudjackItem> HUDJACK = ITEMS.register("cybereye_upgrades_hudjack", (location) -> new HudjackItem(modProps(location)));
    public static final DeferredHolder<Item, TargetingOverlayItem> TARGETING_OVERLAY = ITEMS.register("cybereye_upgrades_targeting", (location) -> new TargetingOverlayItem(modProps(location)));
    public static final DeferredHolder<Item, DistanceEnhancerItem> DISTANCE_ENHANCER = ITEMS.register("cybereye_upgrades_zoom", (location) -> new DistanceEnhancerItem(modProps(location)));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LUNGS = registerHumanPart("body_part_lungs", BodyRegionEnum.LUNGS.getStartSlot(), 1, BodyPartType.LUNGS);
    public static final DeferredHolder<Item, CompressedOxygenImplantItem> COMPRESSED_OXYGEN = ITEMS.register("lungs_upgrades_oxygen", (location) -> new CompressedOxygenImplantItem(modProps(location)));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LEFT_ARM = registerHumanPart("body_part_arm_left", BodyRegionEnum.ARMS.getStartSlot(), 1, BodyPartType.ARM_LEFT);
    public static final DeferredHolder<Item, CyberArmItem> CYBER_ARM_RIGHT = ITEMS.register("cyberlimbs_cyberarm_right", (location) -> new CyberArmItem(modProps(location), BodyRegionEnum.ARMS.getStartSlot(), ModItems.HUMAN_RIGHT_ARM, BodyPartType.ARM_RIGHT));
    public static final DeferredHolder<Item, CyberLegItem> CYBER_LEG_RIGHT = ITEMS.register("cyberlimbs_cyberleg_right", (location) -> new CyberLegItem(modProps(location), BodyRegionEnum.LEGS.getStartSlot(), ModItems.HUMAN_RIGHT_LEG, BodyPartType.LEG_RIGHT));
    public static final DeferredHolder<Item, CyberArmItem> CYBER_ARM_LEFT = ITEMS.register("cyberlimbs_cyberarm_left", (location) -> new CyberArmItem(modProps(location),BodyRegionEnum.ARMS.getStartSlot(), ModItems.HUMAN_LEFT_ARM, BodyPartType.ARM_LEFT));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_RIGHT_ARM = registerHumanPart("body_part_arm_right", BodyRegionEnum.ARMS.getStartSlot(), 1, BodyPartType.ARM_RIGHT);
    public static final DeferredHolder<Item, RetractableClawsItem> RETRACTABLE_CLAWS = ITEMS.register("hand_upgrades_claws",(location) -> new RetractableClawsItem(modProps(location)));
    public static final DeferredHolder<Item, ReinforcedFistItem> REINFORCED_FIST = ITEMS.register("hand_upgrades_mining", (location) -> new ReinforcedFistItem(modProps(location)));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LEFT_HAND = registerHumanPart("body_part_hand_left", BodyRegionEnum.HANDS.getStartSlot(), 1, BodyPartType.HAND_LEFT);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_RIGHT_HAND = registerHumanPart("body_part_hand_right", BodyRegionEnum.HANDS.getStartSlot(), 1, BodyPartType.HAND_RIGHT);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LEFT_LEG = registerHumanPart("body_part_leg_left", BodyRegionEnum.LEGS.getStartSlot(), 1, BodyPartType.LEG_LEFT);
    public static final DeferredHolder<Item, CyberLegItem> CYBER_LEG_LEFT = ITEMS.register("cyberlimbs_cyberleg_left", (location) -> new CyberLegItem(modProps(location), BodyRegionEnum.LEGS.getStartSlot(), ModItems.HUMAN_LEFT_LEG, BodyPartType.LEG_LEFT));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_RIGHT_LEG = registerHumanPart("body_part_leg_right", BodyRegionEnum.LEGS.getStartSlot(), 1, BodyPartType.LEG_RIGHT);
   public static final DeferredHolder<Item, AquaticPropulsionSystemItem> AQUATIC_PROPULSION = ITEMS.register("foot_upgrades_aqua",(location) -> new AquaticPropulsionSystemItem(modProps(location)));
    public static final DeferredHolder<Item, LinearActuatorsItem> LINEAR_ACTUATORS = ITEMS.register("leg_upgrades_jump_boost", (location) -> new LinearActuatorsItem(modProps(location)));
    public static final DeferredHolder<Item, FallBracersItem> FALL_BRACERS = ITEMS.register("leg_upgrades_fall_damage", (location) -> new FallBracersItem(modProps(location)));
    public static final DeferredHolder<Item, DeployableWheelsItem> DEPLOYABLE_WHEELS = ITEMS.register("foot_upgrades_wheels", (location) -> new DeployableWheelsItem(modProps(location)));
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_LEFT_FOOT = registerHumanPart("body_part_foot_left", BodyRegionEnum.BOOTS.getStartSlot(), 1, BodyPartType.FOOT_LEFT);
    public static final DeferredHolder<Item, CyberwareItem> HUMAN_RIGHT_FOOT = registerHumanPart("body_part_foot_right", BodyRegionEnum.BOOTS.getStartSlot(), 1, BodyPartType.FOOT_RIGHT);

    public static final DeferredItem<BlockItem> CYBER_WITHER_SKELETON_SKULL_ITEM = ITEMS.register(
            "cyber_wither_skeleton_skull",
            (location) -> new StandingAndWallBlockItem(
                    ModBlocks.CYBER_WITHER_SKELETON_SKULL.get(),
                    ModBlocks.CYBER_WITHER_SKELETON_WALL_SKULL.get(),
                    Direction.DOWN,
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, location)) // これを追加
                            .rarity(Rarity.RARE)
                            .equippableUnswappable(EquipmentSlot.HEAD)
            ));
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
                            } else if (page == 0) {
                                entries.accept(new ItemStack(item));
                            }
                        }
                    }).build());

    private static DeferredHolder<Item, CyberwareItem> registerHumanPart(String name, int slotId, int maxInstall, BodyPartType bodyPartType) {
        return ITEMS.register(name, (location) -> new CyberwareItem.Builder(modProps(location), 0, slotId) // ここで modProps を渡す
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
