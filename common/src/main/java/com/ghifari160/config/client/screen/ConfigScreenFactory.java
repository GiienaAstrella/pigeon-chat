package com.ghifari160.config.client.screen;

import com.ghifari160.config.api.Config;
import com.ghifari160.config.platform.Services;
import net.minecraft.client.gui.screens.Screen;

import java.util.EnumMap;

public class ConfigScreenFactory {
    public static Screen createScreen(
            String modName,
            Screen previous,
            final EnumMap<Config.Type, Config> configs) {
        if (configs.size() == 1 && !Services.PLATFORM.isDevelopmentEnvironment()) {
            return new ConfigEditScreen(modName, previous, configs.values().iterator().next());
        } else {
            return new ConfigListScreen(modName, previous, configs);
        }
    }
}
