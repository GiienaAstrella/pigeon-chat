package com.ghifari160.config.platform;

import com.ghifari160.config.ConfigState;
import com.ghifari160.config.impl.networking.ConfigPayload;
import com.ghifari160.config.impl.networking.ConfigReloadPayload;
import com.ghifari160.config.platform.services.IPlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
    public static Path gameDir = new File(".").toPath();

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public Path getDefaultConfigDir() {
        return gameDir.resolve("defaultconfigs");
    }

    @Override
    public Path getConfigDir() {
        return gameDir.resolve("config");
    }

    @Override
    public boolean isDedicatedServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void sendSyncPacket(ServerPlayer player, String modID, byte[] contents) {
        ServerPlayNetworking.send(player, new ConfigPayload(modID, contents));
    }

    @Override
    public void broadcastSyncPacket(String modID, byte[] contents) {
        if (ConfigState.server != null) {
            ConfigPayload payload = new ConfigPayload(modID, contents);
            for (ServerPlayer player : ConfigState.server.getPlayerList().getPlayers()) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    @Override
    public void sendReloadPacket(String modID) {
        ClientPlayNetworking.send(new ConfigReloadPayload(modID));
    }
}
