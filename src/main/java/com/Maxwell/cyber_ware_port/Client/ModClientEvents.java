package com.Maxwell.cyber_ware_port.Client;

import com.Maxwell.cyber_ware_port.Client.Screen.CWB.CyberwareWorkbenchScreen;
import com.Maxwell.cyber_ware_port.Client.Screen.RoboSurgeon.RobosurgeonScreen;
import com.Maxwell.cyber_ware_port.Client.Screen.Scanner.ScannerScreen;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberWareWorkBenchModel;
import com.Maxwell.cyber_ware_port.Common.Block.CWB.CyberwareWorkbenchRenderer;
import com.Maxwell.cyber_ware_port.Common.Block.Scanner.ScannerBlockModel;
import com.Maxwell.cyber_ware_port.Common.Block.Scanner.ScannerBlockRenderer;
import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberModel;
import com.Maxwell.cyber_ware_port.Common.Block.SurgeryChamber.SurgeryChamberRenderer;
import com.Maxwell.cyber_ware_port.Common.Entity.PlayerPartsModel.PlayerInternalPartsModel;
import com.Maxwell.cyber_ware_port.Common.Item.Base.CyberwareItem;
import com.Maxwell.cyber_ware_port.Common.Item.BlueprintItem;
import com.Maxwell.cyber_ware_port.CyberWare;
import com.Maxwell.cyber_ware_port.Init.ModBlockEntities;
import com.Maxwell.cyber_ware_port.Init.ModItems;
import com.Maxwell.cyber_ware_port.Init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
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
    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.SURGERY_CHAMBER.get(), SurgeryChamberRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CYBERWARE_WORKBENCH.get(), CyberwareWorkbenchRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SCANNER.get(), ScannerBlockRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SurgeryChamberModel.LAYER_LOCATION, SurgeryChamberModel::createBodyLayer);
        event.registerLayerDefinition(PlayerInternalPartsModel.LAYER_LOCATION, PlayerInternalPartsModel::createBodyLayer);
        event.registerLayerDefinition(CyberWareWorkBenchModel.LAYER_LOCATION, CyberWareWorkBenchModel::createBodyLayer);
        event.registerLayerDefinition(ScannerBlockModel.LAYER_LOCATION, ScannerBlockModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.ROBO_SURGEON_MENU.get(), RobosurgeonScreen::new);
            MenuScreens.register(ModMenuTypes.CYBERWARE_WORKBENCH_MENU.get(), CyberwareWorkbenchScreen::new);
            MenuScreens.register(ModMenuTypes.SCANNER_MENU.get(), ScannerScreen::new);
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

}