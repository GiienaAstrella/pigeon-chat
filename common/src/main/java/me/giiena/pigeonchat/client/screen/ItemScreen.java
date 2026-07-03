package me.giiena.pigeonchat.client.screen;

import me.giiena.pigeonchat.tag.ItemTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemScreen {
    private static final Map<Item, Factory> ITEM_REGISTRY = new IdentityHashMap<>();
    private static final Map<TagKey<Item>, Factory> TAG_REGISTRY = new LinkedHashMap<>();

    public static void init() {
        register(ItemTags.WRITABLE_LETTERS, WritableEditScreen::new);
        register(ItemTags.WRITABLE_NAME_TAGS, NameTagEditScreen::new);
    }

    public static void register(Item item, Factory factory) {
        ITEM_REGISTRY.put(item, factory);
    }

    public static void register(TagKey<Item> itemTag, Factory factory) {
        TAG_REGISTRY.put(itemTag, factory);
    }

    public static boolean open(Player owner, ItemStack stack, InteractionHand hand) {
        Factory factory = resolve(stack);
        if (factory == null) return false;
        Minecraft.getInstance().gui.setScreen(factory.apply(owner, stack, hand));
        return true;
    }

    private static Factory resolve(ItemStack stack) {
        Factory factory = ITEM_REGISTRY.get(stack.getItem());
        if (factory != null) return factory;
        for (Map.Entry<TagKey<Item>, Factory> entry : TAG_REGISTRY.entrySet()) {
            if (stack.is(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    public interface Factory {
        Screen apply(Player owner, ItemStack stack, InteractionHand hand);
    }
}
