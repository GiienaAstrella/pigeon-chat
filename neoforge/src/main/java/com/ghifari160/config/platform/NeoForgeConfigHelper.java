package com.ghifari160.config.platform;

import com.ghifari160.config.platform.services.IConfigHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgeConfigHelper implements IConfigHelper {
    @Override
    public Path getDefaultConfigDir() {
        return FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath());
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isDedicatedServer() {
        return FMLLoader.getCurrent().getDist() == Dist.DEDICATED_SERVER;
    }
}
