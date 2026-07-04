package me.giiena.pigeonchat.client.renderer.entity;

import me.giiena.pigeonchat.entity.EntityTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class EntityRenderers {
    public static void register(EntityRendererRegistrar registrar) {
        registrar.register(EntityTypes.PIGEON, PigeonRenderer::new);
    }

    /**
     * Common interface abstraction for multi-loader entity renderer registrations.
     */
    @FunctionalInterface
    public interface EntityRendererRegistrar {
        <T extends Entity> void register(
                EntityType<? extends T> type,
                EntityRendererProvider<T> renderer);
    }
}
