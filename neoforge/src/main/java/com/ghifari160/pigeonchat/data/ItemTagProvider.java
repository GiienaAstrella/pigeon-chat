package com.ghifari160.pigeonchat.data;

import com.ghifari160.pigeonchat.Constants;
import com.ghifari160.pigeonchat.tag.ItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.ItemIds;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        this.tag(ItemTags.WRITABLES)
                .add(ItemIds.PAPER)
                .add(ItemIds.BOOK);
        this.tag(ItemTags.NIB_MATERIALS)
                .addTag(Tags.Items.NUGGETS_IRON)
                .addTag(Tags.Items.NUGGETS_GOLD);
        this.tag(ItemTags.QUILL_MATERIALS)
                .addTag(Tags.Items.FEATHERS);
    }
}
