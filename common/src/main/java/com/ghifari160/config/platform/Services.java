package com.ghifari160.config.platform;

import com.ghifari160.config.platform.services.IConfigHelper;

import java.util.ServiceLoader;

public class Services {
    public static final IConfigHelper CONFIG = load(IConfigHelper.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}
