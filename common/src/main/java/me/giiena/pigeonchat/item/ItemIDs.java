package me.giiena.pigeonchat.item;

import me.giiena.pigeonchat.PigeonChatCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ItemIDs {
    public static final ResourceKey<Item> PEN = create("pen");
    public static final ResourceKey<Item> QUILL = create("quill");
    public static final ResourceKey<Item> INK_BOTTLE = create("ink_bottle");
    public static final ResourceKey<Item> LETTER = create("letter");
    public static final ResourceKey<Item> PIGEON_SPAWN_EGG = create("pigeon_spawn_egg");
    public static final ResourceKey<Item> PIGEON = create("pigeon");
    public static final ResourceKey<Item> COOKED_PIGEON = create("cooked_pigeon");

    private static ResourceKey<Item> create(final String name) {
        return PigeonChatCommon.resourceKey(Registries.ITEM, name);
    }
}
