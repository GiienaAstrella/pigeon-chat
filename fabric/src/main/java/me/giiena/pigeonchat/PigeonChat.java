package me.giiena.pigeonchat;

import me.giiena.pigeonchat.block.BlockEntities;
import me.giiena.pigeonchat.block.Blocks;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.entity.EntityTypes;
import me.giiena.pigeonchat.entity.Pigeon;
import me.giiena.pigeonchat.inventory.MenuProviders;
import me.giiena.pigeonchat.inventory.MenuTypes;
import me.giiena.pigeonchat.inventory.MessengerAnimalMenu;
import me.giiena.pigeonchat.inventory.MessengerCageMenu;
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PigeonChat implements ModInitializer {
    @Override
    public void onInitialize() {
        PigeonChatConfig.init();
        networkingInit();

        bind(BuiltInRegistries.DATA_COMPONENT_TYPE, PigeonChatComponents::register);
        ItemComponentTooltipProviderRegistry.addAfter(DataComponents.DAMAGE,
                PigeonChatComponents.CONVERTED);
        ItemComponentTooltipProviderRegistry.addAfter(PigeonChatComponents.CONVERTED,
                PigeonChatComponents.SEALED);
        ItemComponentTooltipProviderRegistry.addBefore(PigeonChatComponents.CONVERTED,
                PigeonChatComponents.CAGED_MESSENGER);

        bindKey(BuiltInRegistries.ENTITY_TYPE, EntityTypes::registerTypes);
        bind(BuiltInRegistries.MENU, MenuTypes::register);
        MenuProviders.setMessenger(MessengerAnimalMenu::open);
        MenuProviders.setCage(MessengerCageMenu::open);
        EntityTypes.registerSpawnPlacements(SpawnPlacements::register);
        BiomeModifications.addSpawn(BiomeSelectors.all(),
                MobCategory.CREATURE,
                EntityTypes.PIGEON,
                Pigeon.SPAWN_WEIGHT,
                Pigeon.MIN_SPAWN_COUNT,
                Pigeon.MAX_SPAWN_COUNT);

        bindKey(BuiltInRegistries.BLOCK_ENTITY_TYPE, BlockEntities::register);
        bindKey(BuiltInRegistries.BLOCK, Blocks::registerBlocks);
        bind(BuiltInRegistries.ITEM, Items::register);
        bindKey(BuiltInRegistries.ITEM, Blocks::registerItems);
        bind(BuiltInRegistries.CREATIVE_MODE_TAB, CreativeTabs::register);

        EntityTypes.registerAttributes(FabricDefaultAttributeRegistry::register);

        CreativeTabs.TAB_ITEMS.forEach((tab, items) ->
                CreativeModeTabEvents.modifyOutputEvent(tab).register(entries ->
                        items.forEach(supplier -> entries.accept(supplier.get()))));

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
