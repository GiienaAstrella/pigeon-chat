package com.ghifari160.config.platform;

import com.ghifari160.config.platform.services.IConfigHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;

public class FabricConfigHelper implements IConfigHelper {
    public static Path gameDir = new File(".").toPath();

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
}
