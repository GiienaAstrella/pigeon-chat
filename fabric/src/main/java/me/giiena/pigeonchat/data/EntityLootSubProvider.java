package me.giiena.pigeonchat.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class EntityLootSubProvider extends FabricEntityLootSubProvider {
    protected EntityLootSubProvider(FabricPackOutput output,
                                    CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate() {
        PigeonChatEntityLootProvider.generate(this::add);
    }
}
