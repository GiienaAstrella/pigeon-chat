package com.ghifari160.pigeonchat.tag;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTags {
    public static final TagKey<Item> WRITABLES = create("writables");
    public static final TagKey<Item> WRITABLE_LETTERS = create("writables/letter");
    public static final TagKey<Item> WRITABLE_NAME_TAGS = create("writables/name_tag");

    public static final TagKey<Item> NIB_MATERIALS = create("nib_materials");
    public static final TagKey<Item> QUILL_MATERIALS = create("quill_materials");

    public static final TagKey<Item> STRINGS = create("c", "strings");

    public static TagKey<Item> create(final String id) {
        return TagKey.create(Registries.ITEM, PigeonChatCommon.identifier(id));
    }

    public static TagKey<Item> create(final String namespace, final String id) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, id));
    }
}
