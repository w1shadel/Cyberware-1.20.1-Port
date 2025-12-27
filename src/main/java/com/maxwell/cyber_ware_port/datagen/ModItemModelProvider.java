package com.maxwell.cyber_ware_port.datagen;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("removal")
public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CyberWare.MODID, existingFileHelper);

    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.BLUEPRINT);
        simpleItem(ModItems.EXP_CAPSULE);
        simpleItem(ModItems.CREATIVE_BATTERY);
        simpleItem(ModItems.COMPONENT_ACTUATOR);
        simpleItem(ModItems.COMPONENT_REACTOR);
        simpleItem(ModItems.COMPONENT_TITANIUM);
        simpleItem(ModItems.COMPONENT_SSC);
        simpleItem(ModItems.COMPONENT_PLATING);
        simpleItem(ModItems.COMPONENT_FIBEROPTICS);
        simpleItem(ModItems.COMPONENT_FULLERENE);
        simpleItem(ModItems.COMPONENT_SYNTHNERVES);
        simpleItem(ModItems.COMPONENT_STORAGE);
        simpleItem(ModItems.COMPONENT_MICROELECTRIC);
        simpleItem(ModItems.NEUROPOZYNE);
        withExistingParent("component_box",
                new ResourceLocation(CyberWare.MODID, "block/component_box"));
        katanaItem(ModItems.KATANA);
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
        cyberwareItem(ModItems.CYBER_EYE);
        cyberwareItem(ModItems.LOW_LIGHT_VISION);
        cyberwareItem(ModItems.LIQUID_REFRACTION);
        cyberwareItem(ModItems.HUDJACK);
        cyberwareItem(ModItems.TARGETING_OVERLAY);
        cyberwareItem(ModItems.DISTANCE_ENHANCER);
        cyberwareItem(ModItems.COMPRESSED_OXYGEN);
        cyberwareItem(ModItems.HYPER_OXYGENATION);
        cyberwareItem(ModItems.RAPID_FIRE_FLYWHEEL);
        cyberwareItem(ModItems.IMPLANTED_SPURS);
        cyberwareItem(ModItems.FINE_MANIPULATORS);
        cyberwareItem(ModItems.LIVER_FILTER);
        cyberwareItem(ModItems.METABOLIC_GENERATOR);
        cyberwareItem(ModItems.INTERNAL_BATTERY);
        cyberwareItem(ModItems.ADRENALINE_PUMP);
        cyberwareItem(ModItems.DENSE_BATTERY);
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

    private void simpleItem(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(CyberWare.MODID, "item/" + item.getId().getPath()));

    }

    private void cyberwareItem(RegistryObject<Item> item) {
        String path = item.getId().getPath();
        ResourceLocation standardTexture = new ResourceLocation(CyberWare.MODID, "item/" + path);
        ResourceLocation scavengedTexture = new ResourceLocation(CyberWare.MODID, "item/" + path + "_scavenged");
        ItemModelBuilder scavengedModel = withExistingParent(path + "_scavenged", new ResourceLocation("item/generated"))
                .texture("layer0", scavengedTexture);
        withExistingParent(path, new ResourceLocation("item/generated"))
                .texture("layer0", standardTexture)
                .override()
                .predicate(new ResourceLocation(CyberWare.MODID, "is_scavenged"), 1.0f)
                .model(scavengedModel)
                .end();

    }

    private void katanaItem(RegistryObject<Item> item) {
        withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld"))
                .texture("layer0", new ResourceLocation(CyberWare.MODID, "item/" + item.getId().getPath())).transforms()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(0, 90, 55)
                .translation(0, 4.0f, 0.5f)
                .scale(0.85f)
                .end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                .rotation(0, -90, 55)
                .translation(0, 4.0f, 0.5f)
                .scale(0.85f)
                .end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0, -90, 25)
                .translation(1.13f, 3.2f, 1.13f)
                .scale(0.68f)
                .end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                .rotation(0, 90, 25)
                .translation(1.13f, 3.2f, 1.13f)
                .scale(0.68f)
                .end().end();

    }
}