package me.giiena.pigeonchat.item;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CreativeTabs {
    private static final Map<ResourceKey<CreativeModeTab>, CreativeModeTab> TABS = new HashMap<>();
    public static final Map<ResourceKey<CreativeModeTab>, List<Supplier<ItemStackTemplate>>> TAB_ITEMS =
            new HashMap<>();

    public static final ResourceKey<CreativeModeTab> TAB = register("pigeonchat", builder()
            .icon(() -> new ItemStack(Items.QUILL))
            .title(Component.translatable(PigeonChatCommon.langKey("pigeonchat", "itemGroup")))
            .build());

    public static void addItem(ResourceKey<CreativeModeTab> tab, ItemLike item) {
        addItem(tab, () -> new ItemStackTemplate(item.asItem()));
    }

    public static void addItem(ResourceKey<CreativeModeTab> tab, Supplier<ItemStackTemplate> tmpl) {
        List<Supplier<ItemStackTemplate>> contents = TAB_ITEMS.computeIfAbsent(tab,
                _ -> new LinkedList<>());
        contents.add(tmpl);
    }

    public static void registerAll(BiConsumer<ResourceKey<CreativeModeTab>, CreativeModeTab> registry) {
        TABS.forEach(registry);
    }

    @SuppressWarnings("SameParameterValue")
    private static ResourceKey<CreativeModeTab> register(final String id, CreativeModeTab tab) {
        ResourceKey<CreativeModeTab> key = PigeonChatCommon.resourceKey(Registries.CREATIVE_MODE_TAB, id);
        TABS.put(key, tab);
        return key;
    }

    private static CreativeModeTab.Builder builder() {
        return Services.registry().tabBuilder();
    }
}
