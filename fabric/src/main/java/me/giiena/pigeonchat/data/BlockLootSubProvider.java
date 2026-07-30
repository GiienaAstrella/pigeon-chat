package me.giiena.pigeonchat.data;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class BlockLootSubProvider extends FabricBlockLootSubProvider
        implements IBlockLootSubProvider {
    protected BlockLootSubProvider(FabricPackOutput packOutput,
                                   CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {
        PigeonChatBlockLootProvider.generate(this);
    }
}
