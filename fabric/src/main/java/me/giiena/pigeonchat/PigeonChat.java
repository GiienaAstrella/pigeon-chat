package me.giiena.pigeonchat;

import me.giiena.config.FabricConfig;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.entity.EntityTypes;
import me.giiena.pigeonchat.entity.Pigeon;
import me.giiena.pigeonchat.inventory.MenuProviders;
import me.giiena.pigeonchat.inventory.MenuTypes;
import me.giiena.pigeonchat.inventory.MessengerMenu;
import me.giiena.pigeonchat.item.CreativeTabs;
import me.giiena.pigeonchat.item.Items;
import me.giiena.pigeonchat.network.AssignMessengerPayload;
import me.giiena.pigeonchat.network.SaveWritablePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTabs;

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

        bindKey(BuiltInRegistries.ENTITY_TYPE, EntityTypes::registerTypes);
        bind(BuiltInRegistries.MENU, MenuTypes::register);
        MenuProviders.setMessenger(MessengerMenu::open);
        EntityTypes.registerSpawnPlacements(SpawnPlacements::register);
        BiomeModifications.addSpawn(BiomeSelectors.all(),
                MobCategory.CREATURE,
                EntityTypes.PIGEON,
                Pigeon.SPAWN_WEIGHT,
                Pigeon.MIN_SPAWN_COUNT,
                Pigeon.MAX_SPAWN_COUNT);

        bind(BuiltInRegistries.ITEM, Items::register);
        bind(BuiltInRegistries.CREATIVE_MODE_TAB, CreativeTabs::register);
        EntityTypes.registerAttributes(FabricDefaultAttributeRegistry::register);

        CreativeModeTabEvents.modifyOutputEvent(CreativeTabs.TAB_RESOURCE_KEY).register(entries ->
                Items.TAB_ITEMS.forEach(supplier -> entries.accept(supplier.get())));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries ->
                Items.SPAWN_EGG_TAB_ITEMS.forEach(supplier -> entries.accept(supplier.get())));

        PigeonChatCommon.init();
    }

    private static void networkingInit() {
        PayloadTypeRegistry.serverboundPlay()
                .register(SaveWritablePayload.TYPE, SaveWritablePayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
                SaveWritablePayload.TYPE,
                (payload, ctx) -> payload.handle(ctx.player()));

        PayloadTypeRegistry.serverboundPlay()
                .register(AssignMessengerPayload.TYPE, AssignMessengerPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
                AssignMessengerPayload.TYPE,
                (payload, ctx) -> payload.handle(ctx.player()));
    }

    private <T> void bind(Registry<T> registry, Consumer<BiConsumer<T, Identifier>> source) {
        bindKey(registry, tgt ->
                source.accept((t, id) ->
                        tgt.accept(t, PigeonChatCommon.resourceKey(registry.key(), id))));
    }

    private <T> void bindKey(
            Registry<T> registry,
            Consumer<BiConsumer<T, ResourceKey<T>>> source) {
        source.accept((t, id) -> Registry.register(registry, id, t));
    }
}
