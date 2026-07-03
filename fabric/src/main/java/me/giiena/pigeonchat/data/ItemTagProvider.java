package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.item.ItemIDs;
import me.giiena.pigeonchat.tag.ItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
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
                .forceAddTag(ItemTags.WRITABLE_LETTERS)
                .forceAddTag(ItemTags.WRITABLE_NAME_TAGS);
        builder(ItemTags.WRITABLE_LETTERS)
                .add(PigeonChatCommon.resourceKey(Registries.ITEM, ItemIDs.LETTER))
                .add(ItemIds.PAPER);
        builder(ItemTags.WRITABLE_NAME_TAGS)
                .add(ItemIds.NAME_TAG);

        builder(ItemTags.NIB_MATERIALS)
                .forceAddTag(ConventionalItemTags.IRON_NUGGETS)
                .forceAddTag(ConventionalItemTags.GOLD_NUGGETS);
        builder(ItemTags.QUILL_MATERIALS)
                .forceAddTag(ConventionalItemTags.FEATHERS);
    }
}
