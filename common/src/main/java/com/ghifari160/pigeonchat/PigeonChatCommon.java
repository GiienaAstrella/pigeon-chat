package com.ghifari160.pigeonchat;

import com.ghifari160.pigeonchat.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PigeonChatCommon {
    public static void init() {
        Constants.LOG.info("{} in {}", Constants.MOD_NAME, Services.PLATFORM.getPlatformName());
        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            Constants.LOG.info("We are in a development environment!");
        }
    }

    /**
     * Returns an {@code Identifier} in the mod namespace.
     */
    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    /**
     * Returns a translation key in the mod namespace.
     */
    public static String langKey(String path, String prefix) {
        return langKey(path, prefix, null);
    }

    /**
     * Returns a translation key in the mod namespace.
     */
    public static String langKey(String path, String prefix, @Nullable String suffix) {
        List<String> parts = new ArrayList<>();
        parts.add(prefix);
        parts.add(Constants.MOD_ID);
        parts.add(path);
        if (suffix != null && !suffix.isEmpty()) {
            parts.addLast(suffix);
        }
        return String.join(".", parts);
    }

    /**
     * Returns a resource key for {@code registry} in the mod namespace.
     */
    @SuppressWarnings("unused")
    public static <T> ResourceKey<T> resourceKey(
            ResourceKey<? extends Registry<T>> registry,
            final String path) {
        return resourceKey(registry, identifier(path));
    }

    /**
     * Returns a resource key for {@code registry}.
     */
    public static <T> ResourceKey<T> resourceKey(
            ResourceKey<? extends Registry<T>> registry,
            final Identifier identifier) {
        return ResourceKey.create(registry, identifier);
    }
}