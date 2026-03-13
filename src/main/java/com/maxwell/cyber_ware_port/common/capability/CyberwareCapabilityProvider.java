package com.maxwell.cyber_ware_port.common.capability;

import com.maxwell.cyber_ware_port.CyberWare;
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
    }
}