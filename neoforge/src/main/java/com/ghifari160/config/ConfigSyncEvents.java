package com.ghifari160.config;

import com.ghifari160.config.impl.ConfigManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class ConfigSyncEvents {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ConfigManager.onPlayerLogin(player);
        }
    }

    @SubscribeEvent
    public static void onServerReload(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            ConfigManager.onServerReload();
        }
    }
}
