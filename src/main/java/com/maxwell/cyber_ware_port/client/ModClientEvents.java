package com.maxwell.cyber_ware_port.client;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.client.model.PlayerInternalPartsModel;
import com.maxwell.cyber_ware_port.client.model.SkeletonDisplayModel;
import com.maxwell.cyber_ware_port.client.screen.BlueprintChestScreen;
import com.maxwell.cyber_ware_port.client.screen.ComponentBoxScreen;
import com.maxwell.cyber_ware_port.client.screen.cwb.CyberwareWorkbenchScreen;
import com.maxwell.cyber_ware_port.client.screen.robosurgeon.RobosurgeonScreen;
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
import com.maxwell.cyber_ware_port.common.item.BlueprintItem;
import com.maxwell.cyber_ware_port.common.item.CyberSkullType;
import com.maxwell.cyber_ware_port.common.item.base.CyberwareItem;
import com.maxwell.cyber_ware_port.init.*;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = CyberWare.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    public static final ModelLayerLocation CYBER_SKULL_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "cyber_wither_skeleton_skull"), "main");
    private static final ResourceLocation CYBER_WITHER_SKELETON_TEXTURE = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "textures/entity/cyber_wither_skeleton.png");

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
    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerRecipeCategoryFinder(ModRecipes.ENGINEERING_TYPE.get(), recipe -> RecipeBookCategories.CRAFTING_MISC);
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
        event.registerLayerDefinition(SkeletonDisplayModel.LAYER_LOCATION, SkeletonDisplayModel::createBodyLayer);
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
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.ROBO_SURGEON_MENU.get(), RobosurgeonScreen::new);
        event.register(ModMenuTypes.CYBERWARE_WORKBENCH_MENU.get(), CyberwareWorkbenchScreen::new);
        event.register(ModMenuTypes.SCANNER_MENU.get(), ScannerScreen::new);
        event.register(ModMenuTypes.COMPONENT_BOX_MENU.get(), ComponentBoxScreen::new);
        event.register(ModMenuTypes.BLUEPRINT_CHEST_MENU.get(), BlueprintChestScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.BLUEPRINT.get(), ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "written"),
                    (stack, level, entity, seed) -> BlueprintItem.getTargetItem(stack) != null ? 1.0F : 0.0F);
            ResourceLocation scavengedProperty = ResourceLocation.fromNamespaceAndPath(CyberWare.MODID, "is_scavenged");
            for (DeferredHolder<Item, ? extends Item> entry : ModItems.ITEMS.getEntries()) {
                if (entry.get() instanceof CyberwareItem) {
                    ItemProperties.register(entry.get(), scavengedProperty, (stack, level, entity, seed) ->
                            (stack.getItem() instanceof CyberwareItem cw && !cw.isPristine(stack)) ? 1.0F : 0.0F);
                }
            }
            SkullBlockRenderer.SKIN_BY_TYPE.put(CyberSkullType.CYBER_WITHER_SKELETON, CYBER_WITHER_SKELETON_TEXTURE);
        });
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skinModel : PlayerSkin.Model.values()) {
            PlayerRenderer renderer = event.getSkin(skinModel);
            if (renderer != null) {
                renderer.addLayer(new CyberwarePlayerLayer(renderer));
            }
        }
    }
}