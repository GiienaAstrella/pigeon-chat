package me.giiena.pigeonchat.data;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import org.jspecify.annotations.NonNull;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators gen) {}

    @Override
    public void generateItemModels(@NonNull ItemModelGenerators gen) {
        PigeonChatModelProvider.generateItemModels(gen);
    }

    @Override
    @NonNull
    public String getName() {
        return PigeonChatModelProvider.getName();
    }
}
