package com.ghifari160.pigeonchat;

import com.ghifari160.config.FabricConfig;
import com.ghifari160.pigeonchat.component.PigeonChatComponents;
import com.ghifari160.pigeonchat.item.CreativeTabs;
import com.ghifari160.pigeonchat.item.Items;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PigeonChat implements ModInitializer {

    @Override
    public void onInitialize() {
        FabricConfig.init();
        PigeonChatConfig.init();

        bind(BuiltInRegistries.DATA_COMPONENT_TYPE, PigeonChatComponents::register);
        ItemComponentTooltipProviderRegistry.addAfter(DataComponents.DAMAGE,
                PigeonChatComponents.CONVERTED);
        bind(BuiltInRegistries.ITEM, Items::register);
        bind(BuiltInRegistries.CREATIVE_MODE_TAB, CreativeTabs::register);

        CreativeModeTabEvents.modifyOutputEvent(CreativeTabs.TAB_RESOURCE_KEY).register(entries ->
                Items.TAB_ITEMS.forEach(supplier -> entries.accept(supplier.get())));

        PigeonChatCommon.init();
    }

    public <T> void bind(Registry<T> registry, Consumer<BiConsumer<T, Identifier>> source) {
        source.accept((t, id) -> Registry.register(registry, id, t));
    }
}
