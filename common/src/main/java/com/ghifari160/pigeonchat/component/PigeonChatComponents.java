package com.ghifari160.pigeonchat.component;

import com.ghifari160.pigeonchat.PigeonChatCommon;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

import java.util.function.BiConsumer;

public class PigeonChatComponents {
    public static DataComponentType<InkContainer> INK_CONTAINER;
    public static DataComponentType<Converted> CONVERTED;
    public static DataComponentType<Boolean> UNBREAKABLE;

    public static void register(BiConsumer<DataComponentType<?>, Identifier> consumer) {
        INK_CONTAINER = DataComponentType.<InkContainer>builder()
                .persistent(InkContainer.MAP_CODEC)
                .networkSynchronized(InkContainer.STREAM_CODEC)
                .build();
        CONVERTED = DataComponentType.<Converted>builder()
                .persistent(Converted.CODEC)
                .networkSynchronized(Converted.STREAM_CODEC)
                .build();
        UNBREAKABLE = DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .build();

        consumer.accept(INK_CONTAINER, PigeonChatCommon.identifier("ink_container"));
        consumer.accept(CONVERTED, PigeonChatCommon.identifier("converted"));
        consumer.accept(UNBREAKABLE, PigeonChatCommon.identifier("unbreakable"));
    }
}
