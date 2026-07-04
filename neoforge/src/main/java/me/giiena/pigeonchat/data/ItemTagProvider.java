package me.giiena.pigeonchat.data;

import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.item.ItemIDs;
import me.giiena.pigeonchat.tag.ItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.references.ItemIds;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider {
    public ItemTagProvider(PackOutput output,
                           CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        this.tag(ItemTags.WRITABLES)
                .addTag(ItemTags.WRITABLE_LETTERS)
                .addTag(ItemTags.WRITABLE_NAME_TAGS);
        this.tag(ItemTags.WRITABLE_LETTERS)
                .add(PigeonChatCommon.resourceKey(Registries.ITEM, ItemIDs.LETTER))
                .add(ItemIds.PAPER);
        this.tag(ItemTags.WRITABLE_NAME_TAGS)
                .add(ItemIds.NAME_TAG);

        this.tag(ItemTags.DELIVERABLES)
                .addTag(ItemTags.PIGEON_DELIVERABLES);
        this.tag(ItemTags.PIGEON_DELIVERABLES)
                .addTag(ItemTags.WRITABLE_LETTERS)
                .add(ItemIds.MAP)
                .add(ItemIds.FILLED_MAP);

        this.tag(ItemTags.NIB_MATERIALS)
                .addTag(Tags.Items.NUGGETS_IRON)
                .addTag(Tags.Items.NUGGETS_GOLD);
        this.tag(ItemTags.QUILL_MATERIALS)
                .addTag(Tags.Items.FEATHERS);
    }
}
