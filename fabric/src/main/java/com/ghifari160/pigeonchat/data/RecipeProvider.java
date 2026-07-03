package com.ghifari160.pigeonchat.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected net.minecraft.data.recipes.@NonNull RecipeProvider createRecipeProvider(
            HolderLookup.@NonNull Provider registries, @NonNull RecipeOutput output) {
        return new PigeonChatRecipeProvider(registries, output);
    }

    @Override
    @NonNull
    public String getName() {
        return PigeonChatRecipeProvider.name();
    }
}
