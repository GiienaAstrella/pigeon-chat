package me.giiena.pigeonchat.client.renderer.entity;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EntityModelLayers {
    public static final ModelLayerLocation PIGEON = createMain("pigeon");

    public static void registerModelLayers(
            BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        consumer.accept(PIGEON, PigeonModel::createBodyLayer);
    }

    @SuppressWarnings("SameParameterValue")
    private static ModelLayerLocation createMain(String id) {
        return new ModelLayerLocation(PigeonChatCommon.identifier(id), "main");
    }
}
