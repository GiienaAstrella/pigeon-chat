package com.ghifari160.pigeonchat.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public final class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider.Runner {
    public RecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected net.minecraft.data.recipes.@NonNull RecipeProvider createRecipeProvider(
            HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput recipeOutput) {
        return new PigeonChatRecipeProvider(provider, recipeOutput);
    }

    @Override
    @NonNull
    public String getName() {
        return PigeonChatRecipeProvider.name();
    }
}
