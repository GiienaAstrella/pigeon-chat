package com.ghifari160.pigeonchat;

import com.ghifari160.pigeonchat.item.CreativeTabs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod(Constants.MOD_ID)
public class PigeonChat {

    public static IEventBus EVENT_BUS;

    public PigeonChat(IEventBus modEventBus) {
        EVENT_BUS = modEventBus;

        bind(Registries.CREATIVE_MODE_TAB, CreativeTabs::register);

        PigeonChatCommon.init();
    }

    public <T> void bind(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, Identifier>> source) {
        EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
            if (registry.equals(event.getRegistryKey())) {
                source.accept((t, id) -> event.register(registry, id, () -> t));
            }
        });
    }
}