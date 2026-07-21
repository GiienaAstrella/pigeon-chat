package me.giiena.pigeonchat;

import me.giiena.pigeonchat.client.color.item.InkContainer;
import me.giiena.pigeonchat.client.renderer.entity.EntityModelLayers;
import me.giiena.pigeonchat.client.renderer.entity.EntityRenderers;
import me.giiena.pigeonchat.client.screen.ItemScreen;
import me.giiena.pigeonchat.client.screen.TargetSelectionScreen;
import me.giiena.pigeonchat.inventory.MenuTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.gui.screens.MenuScreens;

public class PigeonChatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PigeonChatCommonClient.init();
        ItemScreen.init();
        ItemTintSources.ID_MAPPER.put(
                PigeonChatCommon.identifier("ink_container"),
                InkContainer.MAP_CODEC);

        EntityModelLayers.registerModelLayers((loc, supp) ->
                ModelLayerRegistry.registerModelLayer(loc, supp::get));
        EntityRenderers.register(net.minecraft.client.renderer.entity.EntityRenderers::register);
        MenuScreens.register(MenuTypes.MESSENGER, TargetSelectionScreen::new);
    }
}
