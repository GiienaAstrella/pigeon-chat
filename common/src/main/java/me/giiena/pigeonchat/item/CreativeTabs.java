package me.giiena.pigeonchat.item;

import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public class CreativeTabs {
    static final Identifier TAB_ID = PigeonChatCommon.identifier("pigeonchat");
    public static final ResourceKey<CreativeModeTab> TAB_RESOURCE_KEY =
            PigeonChatCommon.resourceKey(Registries.CREATIVE_MODE_TAB, TAB_ID);
    public static CreativeModeTab TAB;

    public static void register(BiConsumer<CreativeModeTab, Identifier> consumer) {
        TAB = Services.registry().tabBuilder()
                .icon(() -> new ItemStack(Items.QUILL))
                .title(Component.translatable(TAB_ID.toLanguageKey("itemGroup")))
                .build();

        consumer.accept(TAB, TAB_ID);
    }
}
