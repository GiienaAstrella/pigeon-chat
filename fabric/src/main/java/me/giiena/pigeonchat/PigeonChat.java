package me.giiena.pigeonchat;

import me.giiena.config.FabricConfig;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.item.CreativeTabs;
import me.giiena.pigeonchat.item.Items;
import me.giiena.pigeonchat.network.SaveWritablePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
        networkingInit();

        bind(BuiltInRegistries.DATA_COMPONENT_TYPE, PigeonChatComponents::register);

        ItemComponentTooltipProviderRegistry.addAfter(DataComponents.DAMAGE,
                PigeonChatComponents.CONVERTED);
        ItemComponentTooltipProviderRegistry.addAfter(PigeonChatComponents.CONVERTED,
                PigeonChatComponents.SEALED);

        bind(BuiltInRegistries.ITEM, Items::register);
        bind(BuiltInRegistries.CREATIVE_MODE_TAB, CreativeTabs::register);

        CreativeModeTabEvents.modifyOutputEvent(CreativeTabs.TAB_RESOURCE_KEY).register(entries ->
                Items.TAB_ITEMS.forEach(supplier -> entries.accept(supplier.get())));

        PigeonChatCommon.init();
    }

    private static void networkingInit() {
        PayloadTypeRegistry.serverboundPlay()
                .register(SaveWritablePayload.TYPE, SaveWritablePayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
                SaveWritablePayload.TYPE,
                (payload, ctx) -> payload.handle(ctx.player()));
    }

    public <T> void bind(Registry<T> registry, Consumer<BiConsumer<T, Identifier>> source) {
        source.accept((t, id) -> Registry.register(registry, id, t));
    }
}
