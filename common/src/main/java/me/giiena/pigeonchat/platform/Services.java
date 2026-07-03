package me.giiena.pigeonchat.platform;

import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.platform.services.IPlatformHelper;
import me.giiena.pigeonchat.platform.services.IRegistryHelper;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    private static IRegistryHelper registryHelper;

    public static IRegistryHelper registry() {
        if (registryHelper == null) registryHelper = load(IRegistryHelper.class);
        return registryHelper;
    }

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}