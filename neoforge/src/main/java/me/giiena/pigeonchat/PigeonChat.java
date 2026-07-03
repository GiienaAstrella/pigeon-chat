package me.giiena.pigeonchat;

import me.giiena.config.ConfigNeoForgeClient;
import me.giiena.config.NeoForgeConfig;
import me.giiena.pigeonchat.client.color.item.InkContainer;
import me.giiena.pigeonchat.client.screen.ItemScreen;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.data.ItemTagProvider;
import me.giiena.pigeonchat.data.ModelProvider;
import me.giiena.pigeonchat.data.RecipeProvider;
import me.giiena.pigeonchat.item.CreativeTabs;
import me.giiena.pigeonchat.item.Items;
import me.giiena.pigeonchat.network.SaveWritablePayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod(Constants.MOD_ID)
public class PigeonChat {
    public static IEventBus EVENT_BUS;

    public PigeonChat(IEventBus modEventBus) {
        EVENT_BUS = modEventBus;
        EVENT_BUS.register(PigeonChat.class);
        NeoForge.EVENT_BUS.register(PigeonChatGameEvents.class);
        NeoForgeConfig.init(modEventBus);
        PigeonChatConfig.init();

        bind(Registries.DATA_COMPONENT_TYPE, PigeonChatComponents::register);
        bind(Registries.ITEM, Items::register);
        bind(Registries.CREATIVE_MODE_TAB, CreativeTabs::register);

        EVENT_BUS.addListener((Consumer<BuildCreativeModeTabContentsEvent>) event -> {
            if (event.getTabKey() == CreativeTabs.TAB_RESOURCE_KEY) {
                Items.TAB_ITEMS.forEach(supplier -> event.accept(supplier.get()));
            }
        });

        PigeonChatCommon.init();
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        PigeonChatCommonClient.init();

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        generator.addProvider(true, new ModelProvider(output));
        generator.addProvider(true, new ItemTagProvider(output, registries));
        generator.addProvider(true, new RecipeProvider(output, registries));
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(PigeonChatCommon.identifier("ink_container"), InkContainer.MAP_CODEC);
    }

    @SubscribeEvent
    private static void clientSetup(FMLClientSetupEvent event) {
        ConfigNeoForgeClient.setup();
        PigeonChatCommonClient.init();
        ItemScreen.init();
    }

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                SaveWritablePayload.TYPE,
                SaveWritablePayload.STREAM_CODEC,
                (payload, ctx) -> payload.handle(ctx.player()));
    }

    public <T> void bind(
            ResourceKey<Registry<T>> registry,
            Consumer<BiConsumer<T, Identifier>> source) {
        EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
            if (registry.equals(event.getRegistryKey())) {
                source.accept((t, id) -> event.register(registry, id, () -> t));
            }
        });
    }
}