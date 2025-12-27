package com.maxwell.cyber_ware_port.client;

import com.maxwell.cyber_ware_port.client.screen.BlueprintChestScreen;
import com.maxwell.cyber_ware_port.client.screen.cwb.CyberwareWorkbenchScreen;
import com.maxwell.cyber_ware_port.client.screen.ComponentBoxScreen;
import com.maxwell.cyber_ware_port.client.screen.roboSurgeon.RobosurgeonScreen;
import com.maxwell.cyber_ware_port.client.screen.scanner.ScannerScreen;
import com.maxwell.cyber_ware_port.client.upgrades.CyberLimbModel;
import com.maxwell.cyber_ware_port.client.upgrades.CyberwarePlayerLayer;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberWareWorkBenchModel;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberwareWorkbenchRenderer;
import com.maxwell.cyber_ware_port.common.block.cyberskull.CyberSkullRenderer;
import com.maxwell.cyber_ware_port.common.block.radio.tower.RadioTowerModel;
import com.maxwell.cyber_ware_port.common.block.radio.tower.RadioTowerRenderer;
import com.maxwell.cyber_ware_port.common.block.scanner.ScannerBlockModel;
import com.maxwell.cyber_ware_port.common.block.scanner.ScannerBlockRenderer;
import com.maxwell.cyber_ware_port.common.block.surgerychamber.SurgeryChamberModel;
import com.maxwell.cyber_ware_port.common.block.surgerychamber.SurgeryChamberRenderer;
import com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper.CyberCreeperModel;
import com.maxwell.cyber_ware_port.common.entity.monster.cybercreeper.CyberCreeperRenderer;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberskeleton.CyberSkeletonModel;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberskeleton.CyberSkeletonRenderer;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwither.CyberWitherBossRenderer;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwither.CyberWitherModel;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwitherskeleton.CyberWitherSkeletonModel;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberwitherskeleton.CyberWitherSkeletonRenderer;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberzombie.CyberZombieModel;
import com.maxwell.cyber_ware_port.common.entity.monster.cyberzombie.CyberZombieRenderer;
import com.maxwell.cyber_ware_port.common.entity.playerpartsmodel.PlayerInternalPartsModel;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.common.item.CyberSkullType;
import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import com.maxwell.cyber_ware_port.init.ModEntities;
import com.maxwell.cyber_ware_port.init.ModItems;
import com.maxwell.cyber_ware_port.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = CyberWare.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    public static final ModelLayerLocation CYBER_SKULL_LAYER =
            new ModelLayerLocation(new ResourceLocation(CyberWare.MODID, "cyber_wither_skeleton_skull"), "main");
    private static final ResourceLocation CYBER_WITHER_SKELETON_TEXTURE =
            new ResourceLocation(CyberWare.MODID, "textures/entity/cyber_wither_skeleton.png");

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.SURGERY_CHAMBER.get(), SurgeryChamberRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CYBERWARE_WORKBENCH.get(), CyberwareWorkbenchRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SCANNER.get(), ScannerBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RADIO_TOWER_CORE.get(), RadioTowerRenderer::new);
        event.registerEntityRenderer(ModEntities.CYBER_ZOMBIE.get(), CyberZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.CYBER_SKELETON.get(), CyberSkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.CYBER_WITHER_SKELETON.get(), CyberWitherSkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.CYBER_CREEPER.get(), CyberCreeperRenderer::new);
        event.registerEntityRenderer(ModEntities.CYBER_WITHER.get(), CyberWitherBossRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CYBER_SKULL.get(), CyberSkullRenderer::new);

    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SurgeryChamberModel.LAYER_LOCATION, SurgeryChamberModel::createBodyLayer);
        event.registerLayerDefinition(PlayerInternalPartsModel.LAYER_LOCATION, PlayerInternalPartsModel::createBodyLayer);
        event.registerLayerDefinition(CyberWareWorkBenchModel.LAYER_LOCATION, CyberWareWorkBenchModel::createBodyLayer);
        event.registerLayerDefinition(ScannerBlockModel.LAYER_LOCATION, ScannerBlockModel::createBodyLayer);
        event.registerLayerDefinition(RadioTowerModel.LAYER_LOCATION, RadioTowerModel::createBodyLayer);
        event.registerLayerDefinition(CyberWitherSkeletonModel.LAYER_LOCATION, CyberWitherSkeletonModel::createBodyLayer);
        event.registerLayerDefinition(CyberSkeletonModel.LAYER_LOCATION, CyberSkeletonModel::createBodyLayer);
        event.registerLayerDefinition(CyberZombieModel.LAYER_LOCATION, CyberZombieModel::createBodyLayer);
        event.registerLayerDefinition(CyberCreeperModel.LAYER_LOCATION, CyberCreeperModel::createBodyLayer);
        event.registerLayerDefinition(CyberWitherModel.LAYER_LOCATION, CyberWitherModel::createBodyLayer);
        event.registerLayerDefinition(CYBER_SKULL_LAYER, SkullModel::createMobHeadLayer);
        event.registerLayerDefinition(CyberLimbModel.LAYER_LOCATION, CyberLimbModel::createBodyLayer);
        event.registerLayerDefinition(CyberCreeperModel.ARMOR_LOCATION, CyberCreeperModel::createArmorLayer);
    }

    @SubscribeEvent
    public static void onCreateSkullModels(EntityRenderersEvent.CreateSkullModels event) {
        SkullModel model = new SkullModel(event.getEntityModelSet().bakeLayer(CYBER_SKULL_LAYER));
        event.registerSkullModel(CyberSkullType.CYBER_WITHER_SKELETON, model);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.ROBO_SURGEON_MENU.get(), RobosurgeonScreen::new);
            MenuScreens.register(ModMenuTypes.CYBERWARE_WORKBENCH_MENU.get(), CyberwareWorkbenchScreen::new);
            MenuScreens.register(ModMenuTypes.SCANNER_MENU.get(), ScannerScreen::new);
            MenuScreens.register(ModMenuTypes.COMPONENT_BOX_MENU.get(), ComponentBoxScreen::new);
            MenuScreens.register(ModMenuTypes.BLUEPRINT_CHEST_MENU.get(), BlueprintChestScreen::new);
            ItemProperties.register(ModItems.BLUEPRINT.get(), new ResourceLocation(CyberWare.MODID, "written"),
                    (stack, level, entity, seed) -> BlueprintItem.getTargetItem(stack) != null ? 1.0F : 0.0F);
            ResourceLocation scavengedProperty = new ResourceLocation(CyberWare.MODID, "is_scavenged");
            for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
                if (entry.get() instanceof CyberwareItem) {
                    ItemProperties.register(entry.get(), scavengedProperty, (stack, level, entity, seed) ->
                            (stack.getItem() instanceof CyberwareItem cw && !cw.isPristine(stack)) ? 1.0F : 0.0F
                    );
                }
            }
            SkullBlockRenderer.SKIN_BY_TYPE.put(CyberSkullType.CYBER_WITHER_SKELETON, CYBER_WITHER_SKELETON_TEXTURE);
        });
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (String skinType : new String[]{"default", "slim"}) {
            PlayerRenderer renderer = event.getSkin(skinType);
            if (renderer != null) {
                renderer.addLayer(new CyberwarePlayerLayer(renderer));
            }
        }
    }

}