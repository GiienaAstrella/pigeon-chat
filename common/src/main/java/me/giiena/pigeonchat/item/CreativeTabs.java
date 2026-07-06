package me.giiena.pigeonchat.item;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CreativeTabs {
    public static final ResourceKey<CreativeModeTab> TAB = create("pigeonchat");

    public static final Map<ResourceKey<CreativeModeTab>, List<Supplier<ItemStack>>> TAB_ITEMS =
            new LinkedHashMap<>();

    public static void register(BiConsumer<CreativeModeTab, Identifier> consumer) {
        consumer.accept(builder()
                .icon(() -> new ItemStack(Items.QUILL))
                .title(Component.translatable(TAB.identifier().toLanguageKey("itemGroup")))
                .build(), TAB.identifier());
    }

    public static void addItem(ResourceKey<CreativeModeTab> tab, ItemLike item) {
        addItem(tab, () -> new ItemStack(item));
    }

    public static void addItem(ResourceKey<CreativeModeTab> tab, Supplier<ItemStack> supplier) {
        List<Supplier<ItemStack>> contents = TAB_ITEMS.computeIfAbsent(tab,
                _ -> new LinkedList<>());
        contents.add(supplier);
    }

    @SuppressWarnings("SameParameterValue")
    private static ResourceKey<CreativeModeTab> create(String id) {
        return PigeonChatCommon.resourceKey(Registries.CREATIVE_MODE_TAB, id);
    }

    private static CreativeModeTab.Builder builder() {
        return Services.registry().tabBuilder();
    }
}
