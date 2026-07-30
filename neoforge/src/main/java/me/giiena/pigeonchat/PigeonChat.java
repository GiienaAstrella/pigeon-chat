package me.giiena.pigeonchat;

import me.giiena.pigeonchat.block.BlockEntities;
import me.giiena.pigeonchat.block.Blocks;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.entity.EntityTypes;
import me.giiena.pigeonchat.inventory.MenuProviders;
import me.giiena.pigeonchat.inventory.MenuTypes;
import me.giiena.pigeonchat.inventory.MessengerAnimalMenu;
import me.giiena.pigeonchat.inventory.MessengerCageMenu;
import me.giiena.pigeonchat.item.CreativeTabs;
import me.giiena.pigeonchat.item.Items;
import me.giiena.pigeonchat.network.AssignMessengerPayload;
import me.giiena.pigeonchat.network.SaveWritablePayload;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mod(Constants.MOD_ID)
@EventBusSubscriber
public class PigeonChat {
    public static IEventBus EVENT_BUS;

    public PigeonChat(IEventBus modEventBus) {
        EVENT_BUS = modEventBus;
        PigeonChatConfig.init();

        bind(Registries.DATA_COMPONENT_TYPE, PigeonChatComponents::registerAll);

        bindKey(Registries.ENTITY_TYPE, EntityTypes::registerTypes);
        bind(Registries.MENU, MenuTypes::registerAll);
        MenuProviders.setMessenger(MessengerAnimalMenu::open);
        MenuProviders.setCage(MessengerCageMenu::open);

        bindKey(Registries.BLOCK_ENTITY_TYPE, BlockEntities::register);
        bindKey(Registries.BLOCK, Blocks::registerAll);
        bindKey(Registries.ITEM, Items::registerAll);
        bindKey(Registries.CREATIVE_MODE_TAB, CreativeTabs::registerAll);

        EVENT_BUS.addListener((Consumer<BuildCreativeModeTabContentsEvent>) event ->
                CreativeTabs.TAB_ITEMS.forEach((tab, items) -> {
            if (event.getTabKey() == tab) {
                items.forEach(supplier -> event.accept(supplier.get().create()));
            }
        }));

        PigeonChatCommon.init();
    }

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                SaveWritablePayload.TYPE,
                SaveWritablePayload.STREAM_CODEC,
                (payload, ctx) -> payload.handle(ctx.player()));
        registrar.playToServer(
                AssignMessengerPayload.TYPE,
                AssignMessengerPayload.STREAM_CODEC,
                (payload, ctx) -> payload.handle(ctx.player()));
    }

    @SubscribeEvent
    private static void createDefaultAttributes(EntityAttributeCreationEvent event) {
        EntityTypes.registerAttributes((type, builder) ->
                event.put(type, builder.build()));
    }

    @SubscribeEvent
    private static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        EntityTypes.registerSpawnPlacements(new EntityTypes.SpawnPlacementsRegistrar() {
            @Override
            public <T extends Mob> void register(EntityType<T> type,
                                                 SpawnPlacementType placementType,
                                                 Heightmap.Types heightMap,
                                                 SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
                event.register(type,
                        placementType,
                        heightMap,
                        spawnPredicate,
                        RegisterSpawnPlacementsEvent.Operation.REPLACE);
            }
        });
    }

    private <T> void bind(
            ResourceKey<Registry<T>> registry,
            Consumer<BiConsumer<Identifier, T>> source) {
        EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
            if (registry.equals(event.getRegistryKey())) {
                source.accept((id, t) -> event.register(registry, id, () -> t));
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    private <T> void bindKey(
            ResourceKey<Registry<T>> registry,
            Consumer<BiConsumer<ResourceKey<T>, T>> source) {
        EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
            if (registry.equals(event.getRegistryKey())) {
                source.accept((id, t) -> event.register(registry, id.identifier(), () -> t));
            }
        });
    }
}