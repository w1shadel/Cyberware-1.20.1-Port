package com.Maxwell.cyber_ware_port.Client;

import com.Maxwell.cyber_ware_port.Client.Screen.BlueprintChestScreen;
import com.Maxwell.cyber_ware_port.Client.Screen.CWB.CyberwareWorkbenchScreen;
import com.Maxwell.cyber_ware_port.Client.Screen.ComponentBoxScreen;
import com.Maxwell.cyber_ware_port.Client.Screen.RoboSurgeon.RobosurgeonScreen;
import com.Maxwell.cyber_ware_port.Client.Screen.Scanner.ScannerScreen;
import com.Maxwell.cyber_ware_port.Client.Upgrades.CyberLimbModel;
import com.Maxwell.cyber_ware_port.Client.Upgrades.CyberwarePlayerLayer;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberWareWorkBenchModel;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberwareWorkbenchRenderer;
import com.Maxwell.cyber_ware_port.Common.Block.CyberSkull.CyberSkullRenderer;
import com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower.RadioTowerModel;
import com.Maxwell.cyber_ware_port.Common.Block.Radio.Tower.RadioTowerRenderer;
import com.Maxwell.cyber_ware_port.Common.Block.Scanner.ScannerBlockModel;
import com.Maxwell.cyber_ware_port.Common.Block.Scanner.ScannerBlockRenderer;
import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberModel;
import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberRenderer;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberCreeper.CyberCreeperModel;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberCreeper.CyberCreeperRenderer;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberSkeleton.CyberSkeletonModel;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberSkeleton.CyberSkeletonRenderer;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWither.CyberWitherBossRenderer;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWither.CyberWitherModel;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWitherSkeleton.CyberWitherSkeletonModel;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberWitherSkeleton.CyberWitherSkeletonRenderer;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberZombie.CyberZombieModel;
import com.Maxwell.cyber_ware_port.Common.Entity.Monster.CyberZombie.CyberZombieRenderer;
import com.Maxwell.cyber_ware_port.Common.Entity.PlayerPartsModel.PlayerInternalPartsModel;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;
import com.Maxwell.cyber_ware_port.Init.ModEntities;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import com.Maxwell.cyber_ware_port.Init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
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
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.ROBO_SURGEON_MENU.get(), RobosurgeonScreen::new);
            MenuScreens.register(ModMenuTypes.CYBERWARE_WORKBENCH_MENU.get(), CyberwareWorkbenchScreen::new);
            MenuScreens.register(ModMenuTypes.SCANNER_MENU.get(), ScannerScreen::new);
            MenuScreens.register(ModMenuTypes.COMPONENT_BOX_MENU.get(), ComponentBoxScreen::new);
            MenuScreens.register(ModMenuTypes.BLUEPRINT_CHEST_MENU.get(), BlueprintChestScreen::new);
            ItemProperties.register(ModItems.BLUEPRINT.get(),
                    new ResourceLocation(CyberWare.MODID, "written"),
                    (stack, level, entity, seed) -> {
                        return BlueprintItem.getTargetItem(stack) != null ? 1.0F : 0.0F;

                    });
            ResourceLocation scavengedProperty = new ResourceLocation(CyberWare.MODID, "is_scavenged");
            for (RegistryObject<Item> entry : ModItems.ITEMS.getEntries()) {
                if (entry.get() instanceof CyberwareItem) {
                    Item item = entry.get();
                    ItemProperties.register(item, scavengedProperty, (stack, level, entity, seed) -> {
                        if (stack.getItem() instanceof CyberwareItem cw) {
                            return cw.isPristine(stack) ? 0.0F : 1.0F;

                        }
                        return 0.0F;

                    });

                }
            }
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