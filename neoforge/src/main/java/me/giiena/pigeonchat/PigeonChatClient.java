package me.giiena.pigeonchat;

import me.giiena.pigeonchat.client.color.item.InkContainer;
import me.giiena.pigeonchat.client.renderer.entity.EntityModelLayers;
import me.giiena.pigeonchat.client.renderer.entity.EntityRenderers;
import me.giiena.pigeonchat.client.screen.ItemScreen;
import me.giiena.pigeonchat.client.screen.TargetSelectionScreen;
import me.giiena.pigeonchat.data.BiomeModifiers;
import me.giiena.pigeonchat.data.EntityLootSubProvider;
import me.giiena.pigeonchat.data.ItemTagProvider;
import me.giiena.pigeonchat.data.ModelProvider;
import me.giiena.pigeonchat.data.RecipeProvider;
import me.giiena.pigeonchat.inventory.MenuTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(value = Dist.CLIENT)
public class PigeonChatClient {
    @SubscribeEvent
    private static void clientSetup(FMLClientSetupEvent event) {
        PigeonChatCommonClient.init();
        ItemScreen.init();
    }

    @SubscribeEvent
    private static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(PigeonChatCommon.identifier("ink_container"), InkContainer.MAP_CODEC);
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
        List<LootTableProvider.SubProviderEntry> entries = List.of(
                new LootTableProvider.SubProviderEntry(EntityLootSubProvider::new,
                        LootContextParamSets.ENTITY));
        generator.addProvider(true, new LootTableProvider(output, Set.of(), entries, registries));

        RegistrySetBuilder builder = new RegistrySetBuilder();
        builder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifiers::bootstrap);
        event.createDatapackRegistryObjects(builder);
    }

    @SubscribeEvent
    private static void registerLayerDefinitions(
            EntityRenderersEvent.RegisterLayerDefinitions event) {
        EntityModelLayers.registerModelLayers(event::registerLayerDefinition);
    }

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        EntityRenderers.register(event::registerEntityRenderer);
    }

    @SubscribeEvent
    private static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuTypes.MESSENGER, TargetSelectionScreen::new);
    }
}
