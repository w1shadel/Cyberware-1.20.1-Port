package com.maxwell.cyber_ware_port.common.capability;

import com.maxwell.cyber_ware_port.CyberWare;
import com.maxwell.cyber_ware_port.common.block.cwb.CyberwareWorkbenchBlockEntity;
import com.maxwell.cyber_ware_port.init.ModBlockEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@EventBusSubscriber(modid = CyberWare.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CyberwareCapabilityProvider {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CyberWare.MODID);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CyberwareUserData>> CYBERWARE_DATA =
            ATTACHMENT_TYPES.register("cyberware_data", () -> AttachmentType.serializable(CyberwareUserData::new)
                    .copyOnDeath()
                    .build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(
                Capabilities.EnergyStorage.ENTITY,
                net.minecraft.world.entity.EntityType.PLAYER,
                (player, side) -> player.getData(CYBERWARE_DATA.get())
        );
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SCANNER.get(), (be, side) -> be.getExposedHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.ROBO_SURGEON.get(), (be, side) -> be.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.COMPONENT_BOX.get(), (be, side) -> be.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.BLUEPRINT_CHEST.get(), (be, side) -> be.getItemHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.CYBERWARE_WORKBENCH.get(), CyberwareWorkbenchBlockEntity::getItemHandler);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.CHARGER.get(), (be, side) -> be.getEnergyStorage());
    }
}