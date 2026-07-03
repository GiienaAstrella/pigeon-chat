package com.ghifari160.pigeonchat;

import com.ghifari160.pigeonchat.client.renderer.item.SealedWritableProperty;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;

public class PigeonChatCommonClient {
    public static void init() {
        registerProperties();
    }

    public static void registerProperties() {
        ConditionalItemModelProperties.ID_MAPPER.put(
                PigeonChatCommon.identifier("sealed_writable"),
                SealedWritableProperty.MAP_CODEC);
    }
}
