package com.ghifari160.pigeonchat.data;

import com.ghifari160.pigeonchat.tag.ItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.ItemIds;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        builder(ItemTags.WRITABLES)
                .add(ItemIds.PAPER)
                .add(ItemIds.BOOK);
        builder(ItemTags.NIB_MATERIALS)
                .forceAddTag(ConventionalItemTags.IRON_NUGGETS)
                .forceAddTag(ConventionalItemTags.GOLD_NUGGETS);
        builder(ItemTags.QUILL_MATERIALS)
                .forceAddTag(ConventionalItemTags.FEATHERS);
    }
}
