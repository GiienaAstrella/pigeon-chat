package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.Constants;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.data.PackOutput;
import org.jspecify.annotations.NonNull;

public class ModelProvider extends net.minecraft.client.data.models.ModelProvider {
    public ModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockGen,
                                  @NonNull ItemModelGenerators itemGen) {
        PigeonChatModelProvider.generateItemModels(itemGen);
    }

    @Override
    @NonNull
    public String getName() {
        return PigeonChatModelProvider.getName();
    }
}
