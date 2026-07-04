package me.giiena.pigeonchat.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@SuppressWarnings("unused")
public interface IPlatformHelper {
    /**
     * Gets the name of the current platform
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given {@code modId} is loaded.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Sends {@code payload} to the server.
     */
    <T extends CustomPacketPayload> void sendPacketToServer(T payload);
}