package me.giiena.config;

import me.giiena.config.impl.ConfigManager;
import me.giiena.config.impl.networking.ConfigPayload;
import me.giiena.config.impl.networking.ConfigReloadPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class NeoForgeConfig {
    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(NeoForgeConfig::registerPayloads);
        NeoForge.EVENT_BUS.register(ConfigSyncEvents.class);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                ConfigPayload.TYPE,
                ConfigPayload.STREAM_CODEC,
                NeoForgeConfig::handleConfigPayload);
        registrar.playToServer(
                ConfigReloadPayload.TYPE,
                ConfigReloadPayload.STREAM_CODEC,
                NeoForgeConfig::handleConfigReloadPayload);
    }

    private static void handleConfigPayload(ConfigPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ConfigManager.onSyncReceived(payload.modID(), payload.contents()));
    }

    private static void handleConfigReloadPayload(
            ConfigReloadPayload payload,
            IPayloadContext ctx) {
        ctx.enqueueWork(() -> ConfigManager.onServerReload(payload.modID()));
    }
}
