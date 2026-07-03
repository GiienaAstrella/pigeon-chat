package com.ghifari160.pigeonchat;

import com.ghifari160.config.FabricConfig;
import com.ghifari160.pigeonchat.client.color.item.InkContainer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.color.item.ItemTintSources;

public class PigeonChatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricConfig.initClients();
        ItemTintSources.ID_MAPPER.put(PigeonChatCommon.identifier("ink_container"), InkContainer.MAP_CODEC);
    }
}
