package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.block.BlockItemIDs;
import me.giiena.pigeonchat.tag.BlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public BlockTagProvider(FabricPackOutput output,
                            CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        this.builder(BlockTags.MINEABLE_PICKAXE)
                .add(BlockItemIDs.BIRD_CAGE);
    }
}
