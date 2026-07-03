package com.ghifari160.pigeonchat;

import com.ghifari160.pigeonchat.item.CreativeTabs;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PigeonChat implements ModInitializer {

    @Override
    public void onInitialize() {
        bind(BuiltInRegistries.CREATIVE_MODE_TAB, CreativeTabs::register);

        PigeonChatCommon.init();
    }

    public <T> void bind(Registry<T> registry, Consumer<BiConsumer<T, Identifier>> source) {
        source.accept((t, id) -> Registry.register(registry, id, t));
    }
}
