package me.giiena.pigeonchat;

import me.giiena.config.FabricConfig;
import me.giiena.pigeonchat.client.color.item.InkContainer;
import me.giiena.pigeonchat.client.screen.ItemScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.color.item.ItemTintSources;

public class PigeonChatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricConfig.initClients();
        PigeonChatCommonClient.init();
        ItemScreen.init();
        ItemTintSources.ID_MAPPER.put(PigeonChatCommon.identifier("ink_container"), InkContainer.MAP_CODEC);
    }
}
